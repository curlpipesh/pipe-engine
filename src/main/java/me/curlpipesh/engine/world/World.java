package me.curlpipesh.engine.world;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.Engine.EngineState;
import me.curlpipesh.engine.logging.LoggerFactory;
import me.curlpipesh.engine.render.RenderServer;
import me.curlpipesh.engine.util.Vec2d;

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
    private final EngineState state;

    @Getter
    private final RenderServer renderServer;

    @Getter
    private final String name;

    @Getter(AccessLevel.PACKAGE)
    private final Logger logger;

    private final int x;
    private final int y;

    public World(final EngineState state, final String name, final int x, final int y) {
        this.name = name;
        this.state = state;
        this.x = x;
        this.y = y;
        loadedChunks = new LinkedHashSet<>();
        renderServer = new RenderServer(state);
        logger = LoggerFactory.getLogger(state, "World(" + name + ")");
    }

    public void loadWorld() {
        final long t0 = System.nanoTime();
        for(int i = 0; i < x; i++) {
            for(int j = 0; j < y; j++) {
                loadedChunks.add(new Chunk(this, i, j));
            }
        }
        loadedChunks.stream().forEach(Chunk::generate);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        Engine.getLogger().config(String.format("World generation took %dms.", ms));
    }

    public void meshWorld() {
        final long t0 = System.nanoTime();
        loadedChunks.stream().forEach(Chunk::mesh);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        Engine.getLogger().config(String.format("World mesh took %dms.", ms));
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
