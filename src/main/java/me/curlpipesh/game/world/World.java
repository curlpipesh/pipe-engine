package me.curlpipesh.game.world;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.game.Game;
import me.curlpipesh.game.Game.GameState;
import me.curlpipesh.game.logging.LoggerFactory;
import me.curlpipesh.game.render.RenderServer;
import me.curlpipesh.game.util.Vec2d;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author audrey
 * @since 11/12/15.
 */
public class World {
    @Getter
    private final Set<Chunk> loadedChunks;

    @Getter(AccessLevel.PACKAGE)
    private final GameState state;

    @Getter
    private final RenderServer renderServer;

    @Getter
    private final String name;

    @Getter(AccessLevel.PACKAGE)
    private final Logger logger;

    public World(final GameState state, final String name) {
        this.name = name;
        this.state = state;
        loadedChunks = new LinkedHashSet<>();
        renderServer = new RenderServer(state);
        logger = LoggerFactory.getLogger(state, "World(" + name + ")");
    }

    public void loadWorld() {
        final long t0 = System.nanoTime();
        loadedChunks.add(new Chunk(this, 0, 0));
        loadedChunks.add(new Chunk(this, 0, 1));
        loadedChunks.add(new Chunk(this, 1, 1));
        loadedChunks.add(new Chunk(this, 1, 0));
        loadedChunks.stream().forEach(Chunk::generate);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        Game.getLogger().config(String.format("World generation took %dms.", ms));
    }

    public void meshWorld() {
        final long t0 = System.nanoTime();
        loadedChunks.stream().forEach(Chunk::mesh);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        Game.getLogger().config(String.format("World mesh took %dms.", ms));
    }

    public void setColorAtPosition(final double x, final double y, final int color) {
        final long realX = Math.round(x) / Chunk.TILE_SIZE;
        final long realY = Math.round(y) / Chunk.TILE_SIZE;

        final long chunkX = realX / Chunk.SIZE;
        final long chunkY = realY / Chunk.SIZE;

        for(final Chunk chunk : loadedChunks) {
            if(chunk.getChunkPos().x() == chunkX && chunk.getChunkPos().y() == chunkY) {
                chunk.getTiles()[(int) (realX % Chunk.SIZE)][(int) (realY % Chunk.SIZE)] &= 0xFFFFFFFF00000000L;
                chunk.getTiles()[(int) (realX % Chunk.SIZE)][(int) (realY % Chunk.SIZE)] |= 0xFFFFFFFF00000000L | color;
                if(!state.isInTestMode()) {
                    chunk.mesh();
                }
                break;
            }
        }
    }

    public long getTileAtPosition(final double x, final double y) {
        long tile = 0x0L;
        boolean found = false;

        final long realX = Math.round(x) / Chunk.TILE_SIZE;
        final long realY = Math.round(y) / Chunk.TILE_SIZE;

        final long chunkX = realX / Chunk.SIZE;
        final long chunkY = realY / Chunk.SIZE;

        for(final Chunk chunk : loadedChunks) {
            if(chunk.getChunkPos().x() == chunkX && chunk.getChunkPos().y() == chunkY) {
                tile = chunk.getTiles()[(int) (realX % Chunk.SIZE)][(int) (realY % Chunk.SIZE)];
                found = true;
                break;
            }
        }

        if(!found) {
            throw new IllegalStateException("Wasn't able to find tile at position (" + x + ", " + y + ")!");
        }

        return tile;
    }

    @SuppressWarnings("unused")
    public void update(final int delta) {
        renderServer.update();
    }

    public void render(final Vec2d renderOffset) {
        renderServer.render(renderOffset);
    }
}
