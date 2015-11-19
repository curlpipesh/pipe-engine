package me.curlpipesh.game.world;

import lombok.Getter;
import me.curlpipesh.game.Game;
import me.curlpipesh.game.render.RenderRequest;
import me.curlpipesh.game.render.RenderType;
import me.curlpipesh.game.util.AxisAlignedBB;
import me.curlpipesh.game.util.Vec2i;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.TimeUnit;

/**
 * @author audrey
 * @since 11/11/15.
 */
public class Chunk {
    /**
     * Tile structure:
     * <pre>
     *     Solid  Type   Unused Active Alpha  Red    Green  Blue
     *     0x00   00     00     00     00     00     00     00L
     * </pre>
     */
    @Getter
    private final long[][] tiles;
    
    public static final int SIZE = 32;
    public static final int TILE_SIZE = 16;

    @Getter
    private final Vec2i chunkPos;

    @Getter
    private final AxisAlignedBB bb;

    @SuppressWarnings("FieldCanBeLocal")
    private final World world;
    
    public Chunk(final World world, final int chunkX, final int chunkY) {
        this.world = world;
        chunkPos = new Vec2i(chunkX, chunkY);
        tiles = new long[32][32];
        bb = new AxisAlignedBB(chunkX * SIZE * TILE_SIZE, chunkY * SIZE * TILE_SIZE, SIZE * TILE_SIZE, SIZE * TILE_SIZE);
    }

    public void generate() {
        final long t0 = System.nanoTime();
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                // > 16 ? green : grey
                tiles[i][j] = j > SIZE / 2 ? 0xFF0200FFFF337733L : 0xFF0100FFFF777777L;
            }
        }
        final long t1 = System.nanoTime();
        final long ms = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        Game.getLogger().config(String.format("Chunk %s generation took %dms.", chunkPos.toString(), ms));
    }

    public void mesh() {
        // TODO: Attempt face merges
        determineActiveStates();

        final RenderRequest chunkRequest = new RenderRequest("Chunk " + chunkPos, RenderType.VBO, GL11.GL_QUADS);
        final RenderRequest debugRequest = new RenderRequest("ChunkDebug " + chunkPos, RenderType.VBO, GL11.GL_LINES);

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(isActive(tiles[i][j])) {
                    chunkRequest.color(getColor(tiles[i][j]))
                            .vertex((i * TILE_SIZE), (j * TILE_SIZE), 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE), 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE) + TILE_SIZE, 0)
                            .vertex((i * TILE_SIZE), (j * TILE_SIZE) + TILE_SIZE, 0);

                    debugRequest.color(isActive(tiles[i][j]) ? 0xFF00FF00 : 0xFFFF0000)
                            // tldr: negative Z axis renders closer to the near plane, which
                            // makes it render towards the top. Way counter-intuitive
                            .vertex((i * TILE_SIZE), (j * TILE_SIZE), 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE), 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE) + TILE_SIZE, 0)
                            .vertex((i * TILE_SIZE), (j * TILE_SIZE) + TILE_SIZE, 0)

                            .vertex((i * TILE_SIZE), (j * TILE_SIZE), 0)
                            .vertex((i * TILE_SIZE), (j * TILE_SIZE) + TILE_SIZE, 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE) + TILE_SIZE, 0)
                            .vertex((i * TILE_SIZE) + TILE_SIZE, (j * TILE_SIZE), 0);
                }
            }
        }
        chunkRequest.position(chunkPos.x() * SIZE * TILE_SIZE, chunkPos.y() * SIZE * TILE_SIZE).compile();
        debugRequest.position(chunkPos.x() * SIZE * TILE_SIZE, chunkPos.y() * SIZE * TILE_SIZE).compile();
        if(!world.getRenderServer().request(chunkRequest)) {
            Game.getLogger().severe("[Chunk" + chunkPos + "] Render request rejected!?");
        } else {
            world.getRenderServer().request(debugRequest);
        }
    }

    public static boolean isActive(final long tile) {
        return getType(tile) != 0;
    }

    public static int getType(final long tile) {
        return (int) (((tile & 0x00FF000000000000L) >> 48) & 0x000000FF);
    }

    public static boolean isSolid(final long tile) {
        return ((tile & 0xFF00000000000000L) >> 56) != 0;
    }

    public static int getColor(final long tile) {
        return (int) (tile & 0x00000000FFFFFFFFL);
    }

    public static long setActive(final long tile, final boolean state) {
        if(state) {
            return tile | 0x000000FF00000000L;
        } else {
            return tile & ~0x000000FF00000000L;
        }
    }

    private void determineActiveStates() {
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                tiles[i][j] = setActive(tiles[i][j], getType(tiles[i][j]) == 0);
            }
        }
    }
}
