package me.curlpipesh.engine.entity.player;

import lombok.Getter;
import me.curlpipesh.engine.EngineState;
import me.curlpipesh.engine.entity.IEntity;
import me.curlpipesh.engine.render.RenderRequest;
import me.curlpipesh.engine.render.RenderType;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.util.Vec2f;
import me.curlpipesh.engine.world.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class Player implements IEntity {
    @Getter
    private final AxisAlignedBB boundingBox = new AxisAlignedBB();

    @Override
    public boolean update(final EngineState state) {
        boundingBox.getPosition().x(Display.getWidth() / 2 + state.getOffset().x());
        boundingBox.getPosition().y(Display.getHeight() / 2 + state.getOffset().y());
        return true;
    }

    @Override
    public RenderRequest render(final Vec2f offset) {
        final RenderRequest r = new RenderRequest("Player", RenderType.VAO, GL11.GL_QUADS);
        r.dimension(Chunk.TILE_SIZE, Chunk.TILE_SIZE)
                .position(-offset.x() * 2, -offset.y() * 2)
                .color(0xFF0000FF)
                .vertex(boundingBox.xMin(), boundingBox.yMin())
                .vertex(boundingBox.xMin(), boundingBox.yMax())
                .vertex(boundingBox.xMax(), boundingBox.yMax())
                .vertex(boundingBox.xMax(), boundingBox.yMin())
                .compile();
        return r;
    }
}
