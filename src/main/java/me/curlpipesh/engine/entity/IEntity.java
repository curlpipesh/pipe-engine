package me.curlpipesh.engine.entity;

import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.render.RenderRequest;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.util.NoSuchTileException;
import me.curlpipesh.engine.util.Vec2f;
import me.curlpipesh.engine.world.Chunk;

/**
 * @author audrey
 * @since 11/12/15.
 */
@SuppressWarnings("unused")
public interface IEntity {
    AxisAlignedBB getBoundingBox();

    boolean update(final Engine engine);

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

    default boolean isColliding(final Engine engine) {
        /*return Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMin()))
                || Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMax()))
                || Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMax()))
                || Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMin()));*/
        return isSolidWrapper(engine, getBoundingBox().xMin() + 1F, getBoundingBox().yMin() + 1F)
                || isSolidWrapper(engine, getBoundingBox().xMax() - 1F, getBoundingBox().yMax() - 1F)
                || isSolidWrapper(engine, getBoundingBox().xMin() + 1F, getBoundingBox().yMax() - 1F)
                || isSolidWrapper(engine, getBoundingBox().xMax() - 1F, getBoundingBox().yMin() + 1F);
    }

    default boolean isSolidWrapper(final Engine engine, final float x, final float y) {
        try {
            return Chunk.isSolid(engine.getWorld().getTileAtPosition(x, y));
        } catch(final NoSuchTileException e) {
            // e.printStackTrace();
            return false;
        }
    }
}
