package me.curlpipesh.engine.test;

import me.curlpipesh.engine.EngineTestApp;
import me.curlpipesh.engine.world.Chunk;
import me.curlpipesh.engine.world.World;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class ChunkTest {
    private static final EngineTestApp ENGINE = new EngineTestApp();

    @Before
    public void before() {
        ENGINE.getEngine().setWorld(new World(ENGINE.getEngine(), "TEST", 0xDEADBEEF, 4, 4));
        ENGINE.getEngine().getWorld().loadWorld();
    }

    @After
    public void after() {
        ENGINE.getEngine().setWorld(null);
    }

    @Test
    public void testChunkPosGet() {
        // Is this a terrible thing to be doing? Yes. Should I actually be exposing this kind of access? Probably not.
        // But I'm lazy, so meh.
        for(int i = 0; i < Chunk.SIZE; i++) {
            ENGINE.getEngine().getWorld().getLoadedChunks().stream().toArray(Chunk[]::new)[0].getTiles()[i][i] = 0xFF0100FFFF123456L;
        }
        for(int i = 0; i < Chunk.SIZE; i++) {
            assertEquals(ENGINE.getEngine().getWorld().getTileAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1), 0xFF0100FFFF123456L);
        }
    }

    @Test
    public void testSetColorAtPos() {
        for(int i = 0; i < Chunk.SIZE; i++) {
            ENGINE.getEngine().getWorld().setColorAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1, 0xFFFFFFFF);
        }
        for(int i = 0; i < Chunk.SIZE; i++) {
            assertEquals(Chunk.getColor(ENGINE.getEngine().getWorld()
                    .getTileAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1)), 0xFFFFFFFF);
        }
    }
}
