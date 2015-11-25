package me.curlpipesh.engine.world;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.EngineTestApp;
import me.curlpipesh.engine.entity.IEntity;
import me.curlpipesh.engine.entity.player.Player;
import me.curlpipesh.engine.logging.LoggerFactory;
import me.curlpipesh.engine.util.NoSuchTileException;
import me.curlpipesh.engine.util.Vec2f;
import org.lwjgl.opengl.Display;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author audrey
 * @since 11/12/15.
 */
public class World {
    @Getter
    private final Set<Chunk> loadedChunks;

    @Getter(AccessLevel.PACKAGE)
    private final Engine engine;

    @Getter
    private final String name;

    @Getter(AccessLevel.PACKAGE)
    private final Logger logger;

    @Getter(AccessLevel.PACKAGE)
    private final int worldChunkWidth;
    @Getter(AccessLevel.PACKAGE)
    private final int worldChunkHeight;

    @Getter(AccessLevel.PACKAGE)
    private final long seed;

    @Getter(AccessLevel.PACKAGE)
    private final Random rng;

    @Getter
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<IEntity> entities;

    public World(final Engine engine, final String name, final long seed, final int worldChunkWidth, final int worldChunkHeight) {
        this.name = name;
        this.engine = engine;
        this.seed = seed;
        this.worldChunkWidth = worldChunkWidth;
        this.worldChunkHeight = worldChunkHeight;
        rng = new Random(seed);
        entities = new ArrayList<>();
        loadedChunks = new LinkedHashSet<>();
        logger = LoggerFactory.getLogger(engine, "World(" + name + ")");
    }

    public void loadWorld() {
        final long t0 = System.nanoTime();
        for(int i = 0; i < worldChunkWidth; i++) {
            for(int j = 0; j < worldChunkHeight; j++) {
                loadedChunks.add(new Chunk(this, i, j));
            }
        }
        loadedChunks.stream().forEach(Chunk::generate);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        engine.getLogger().config(String.format("World generation took %dms.", ms));
    }

    public void meshWorld() {
        final long t0 = System.nanoTime();
        loadedChunks.stream().forEach(Chunk::mesh);
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        engine.getLogger().config(String.format("World mesh took %dms.", ms));
    }

    public void setColorAtPosition(final double x, final double y, final int color) throws NoSuchTileException {
        final long realX = Math.round(x) / Chunk.TILE_SIZE;
        final long realY = Math.round(y) / Chunk.TILE_SIZE;

        final long chunkX = realX / Chunk.SIZE;
        final long chunkY = realY / Chunk.SIZE;

        for(final Chunk chunk : loadedChunks) {
            if(chunk.getChunkPos().x() == chunkX && chunk.getChunkPos().y() == chunkY) {
                chunk.getTiles()[(int) (realX % Chunk.SIZE)][(int) (realY % Chunk.SIZE)] &= 0xFFFFFFFF00000000L;
                chunk.getTiles()[(int) (realX % Chunk.SIZE)][(int) (realY % Chunk.SIZE)] |= 0xFFFFFFFF00000000L | color;
                if(!engine.isInTestMode()) {
                    chunk.mesh();
                }
                return;
            }
        }

        throw new NoSuchTileException(String.format("Wasn't able to find tile at position (%.2f, %.2f)!", x, y));
    }

    public long getTileAtPosition(final double x, final double y) throws NoSuchTileException {
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
            throw new NoSuchTileException(String.format("Wasn't able to find tile at position (%.2f, %.2f)!", x, y));
        }

        return tile;
    }

    @SuppressWarnings("unused")
    public void update(final int delta) {
        engine.getRenderServer().update();
        entities.forEach(e -> e.update(engine));
    }

    public void render(final Vec2f renderOffset) {
        engine.getRenderServer().render(renderOffset);
        entities.forEach(e -> engine.getRenderServer().request(e.render(renderOffset)));
    }

    @SuppressWarnings("unused")
    public void spawnPlayer(final Player player) {
        // Normal RNG will produce predictable results because seed etc. Temporary hack
        final Random r = new Random();
        final int chunkX = r.nextInt(worldChunkWidth); // rng.nextInt(worldChunkWidth);
        final List<Chunk> spawnColumnChunks = loadedChunks.stream().filter(c -> c.getChunkPos().x() == chunkX).collect(Collectors.toCollection(LinkedList::new));
        spawnColumnChunks.sort((o1, o2) -> o1.getChunkPos().y() < o2.getChunkPos().y() ? -1 : o1.getChunkPos().y() > o2.getChunkPos().y() ? 1 : 0);

        logger.info("Searching potential spawn column: " + chunkX);
        chunkLoop: for(final Chunk c : spawnColumnChunks) {
            for(int i = 0; i < Chunk.SIZE; i++) {
                for(int j = 0; j < Chunk.SIZE; j++) {
                    final long tile = c.getTiles()[i][j];
                    // TODO: Proper type management
                    if(Chunk.getType(tile) != 0x0) {
                        // At least two tiles of air above player spawn
                        if(Chunk.SIZE - j > 1) {
                            if(Chunk.getType(c.getTiles()[i][j + 1]) == 0) {
                                if(Chunk.getType(c.getTiles()[i][j + 2]) == 0) {
                                    final float x = i * Chunk.TILE_SIZE + (c.getChunkPos().x() * Chunk.SIZE * Chunk.TILE_SIZE);
                                    final float y = (j + 1) * Chunk.TILE_SIZE + (c.getChunkPos().y() * Chunk.SIZE * Chunk.TILE_SIZE);
                                    player.setWorldPos(x, y);
                                    engine.getOffset().add(new Vec2f(x - (Display.getWidth() / 2), y - (Display.getHeight() / 2)));
                                    logger.info(String.format("Spawned player at (%.2f, %.2f)", x, y));
                                    break chunkLoop;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
