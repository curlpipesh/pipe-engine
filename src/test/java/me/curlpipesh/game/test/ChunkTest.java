package me.curlpipesh.game.test;

import me.curlpipesh.game.Game;
import me.curlpipesh.game.world.Chunk;
import me.curlpipesh.game.world.World;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class ChunkTest {
    private static final Game game = new Game();

    @Before
    public void before() {
        game.getState().setWorld(new World(game.getState(), "TEST"));
        game.getState().getWorld().loadWorld();
    }

    @After
    public void after() {
        game.getState().setWorld(null);
    }

    @Test
    public void testChunkPosGet() {
        // Is this a terrible thing to be doing? Yes. Should I actually be exposing this kind of access? Probably not.
        // But I'm lazy, so meh.
        for(int i = 0; i < Chunk.SIZE; i++) {
            game.getState().getWorld().getLoadedChunks().stream().toArray(Chunk[]::new)[0].getTiles()[i][i] = 0xFF0100FFFF123456L;
        }
        for(int i = 0; i < Chunk.SIZE; i++) {
            assertEquals(game.getState().getWorld().getTileAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1), 0xFF0100FFFF123456L);
        }
    }

    @Test
    public void testSetColorAtPos() {
        for(int i = 0; i < Chunk.SIZE; i++) {
            game.getState().getWorld().setColorAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1, 0xFFFFFFFF);
        }
        for(int i = 0; i < Chunk.SIZE; i++) {
            assertEquals(Chunk.getColor(game.getState().getWorld()
                    .getTileAtPosition(i * Chunk.TILE_SIZE + 1, i * Chunk.TILE_SIZE + 1)), 0xFFFFFFFF);
        }
    }
}
