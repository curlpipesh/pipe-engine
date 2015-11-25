package me.curlpipesh.engine.entity.player;

import lombok.Getter;
import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.entity.Entity;
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
public class Player extends Entity {
    @Getter
    private final AxisAlignedBB boundingBox = new AxisAlignedBB();

    private final AxisAlignedBB internalBoundingBox = new AxisAlignedBB();

    public Player(final Engine engine) {
        super(engine);
        internalBoundingBox.getDimensions().x(Chunk.TILE_SIZE);
        internalBoundingBox.getDimensions().y(Chunk.TILE_SIZE);
        internalBoundingBox.getPosition().x(Display.getWidth() / 2);
        internalBoundingBox.getPosition().y(Display.getHeight() / 2);
    }

    @Override
    public boolean update(final Engine engine) {
        // Set offsets, make internal-only bounding box that holds actual position
        boundingBox.getPosition().x(Display.getWidth() / 2 + engine.getOffset().x());
        boundingBox.getPosition().y(Display.getHeight() / 2 + engine.getOffset().y());
        internalBoundingBox.getPosition().x(Display.getWidth() / 2);
        internalBoundingBox.getPosition().y(Display.getHeight() / 2);
        return true;
    }

    @Override
    public RenderRequest render(final Vec2f offset) {
        final RenderRequest r = new RenderRequest("Player", RenderType.VAO, GL11.GL_QUADS);
        return r.dimension(Chunk.TILE_SIZE, Chunk.TILE_SIZE)
                .absolute(true)
                .color(0xFF0000FF)
                .vertex(internalBoundingBox.xMin(), internalBoundingBox.yMin())
                .vertex(internalBoundingBox.xMin(), internalBoundingBox.yMax())
                .vertex(internalBoundingBox.xMax(), internalBoundingBox.yMax())
                .vertex(internalBoundingBox.xMax(), internalBoundingBox.yMin())
                .compile();
    }

    /*@Override
    public void setWorldPos(final float x, final float y) {
        boundingBox.getPosition().x(x);
        boundingBox.getPosition().y(y);
    }*/
}
