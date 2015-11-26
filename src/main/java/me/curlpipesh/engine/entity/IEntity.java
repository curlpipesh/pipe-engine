package me.curlpipesh.engine.entity;

import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.render.RenderRequest;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.util.Vec2f;

/**
 * @author audrey
 * @since 11/12/15.
 */
@SuppressWarnings("unused")
public interface IEntity {
    AxisAlignedBB getBoundingBox();

    boolean update(final Engine state);

    RenderRequest render(final Vec2f offset);

    /*
     * Moves the entity to the given xy coordinates
     */
    void setWorldPos(float x, float y);

    /**
     * Attempts to apply the given vector to the entity's current position.
     *
     * @param v The vector to apply
     * @return <tt>true</tt> if the entity's position after application is
     *         valid, <tt>false</tt> otherwise.
     */
    boolean applyVector(Vec2f v);

    boolean isCollidingWithWorld(Engine engine);

    boolean isInAir();

    boolean isJumping();

    void jump();
}
