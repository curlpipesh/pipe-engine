package me.curlpipesh.engine.entity.player;

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
@SuppressWarnings("Duplicates")
public class Player extends Entity {
    private final AxisAlignedBB internalBoundingBox = new AxisAlignedBB();

    private final Vec2f gravity = new Vec2f(0, 0);

    public Player(final Engine engine) {
        super(engine);
        internalBoundingBox.getDimensions().x(Chunk.TILE_SIZE);
        internalBoundingBox.getDimensions().y(Chunk.TILE_SIZE * 2);
        internalBoundingBox.getPosition().x(Display.getWidth() / 2);
        internalBoundingBox.getPosition().y(Display.getHeight() / 2);
    }

    @Override
    public boolean update(final Engine engine) {
        //////////////////////////////////////////////////////////////////////////////////////////////
        // This doesn't just use the parent functionality for this for a reason: Player position is //
        // reliant on offsetting the entire world's rendering position and whatnot, and so the      //
        // player class ends up having to duplicate this functionality.                             //
        //////////////////////////////////////////////////////////////////////////////////////////////
        if(isJumping()) {
            jumpVector.x(0);
            if(jumpVector.y() > 1) {
                jumpVector.mul(new Vec2f(0, engine.getDelta() / engine.getFrameMillisecondGoal()));
                if(!applyVector(jumpVector)) {
                    engine.getLogger().warning("Couldn't apply jump vector!?");
                }/* else {
                    engine.getOffset().addY(jumpVector.y());
                }*/
                jumpVector.div(new Vec2f(0, engine.getDelta() / engine.getFrameMillisecondGoal()));
                jumpVector.y(jumpVector.y() / 2);
            } else {
                applyVector(jumpVector);
                jumpVector.y(0);
                setJumping(false);
            }
        } else {
            if(isInAir()) {
                gravity.x(0);
                gravity.y(engine.getGravityVector().y() * (engine.getDelta() / engine.getFrameMillisecondGoal()));
                applyVector(gravity);

                //engine.getOffset().addY(gravity.y());
            }
        }

        getBoundingBox().getPosition().x(Display.getWidth() / 2 + engine.getOffset().x());
        getBoundingBox().getPosition().y(Display.getHeight() / 2 + engine.getOffset().y());
        return true;
    }


    @Override
    public boolean applyVector(final Vec2f v) {
        getBoundingBox().getPosition().add(v);
        if(isCollidingWithWorld(getEngine())) {
            getBoundingBox().getPosition().sub(v);
            return false;
        }
        getEngine().getOffset().addX(v.x());
        getEngine().getOffset().addY(v.y());
        return true;
    }

    @Override
    public RenderRequest render(final Vec2f offset) {
        final RenderRequest r = new RenderRequest("Player", RenderType.VAO, GL11.GL_QUADS);
        return r.dimension(internalBoundingBox.getDimensions().x(), internalBoundingBox.getDimensions().y())
                .absolute(true)
                .color(0xFF0000FF)
                .vertex(internalBoundingBox.xMin(), internalBoundingBox.yMin())
                .vertex(internalBoundingBox.xMin(), internalBoundingBox.yMax())
                .vertex(internalBoundingBox.xMax(), internalBoundingBox.yMax())
                .vertex(internalBoundingBox.xMax(), internalBoundingBox.yMin())
                .compile();
    }
}
