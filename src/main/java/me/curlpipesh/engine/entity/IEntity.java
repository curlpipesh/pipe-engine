package me.curlpipesh.engine.entity;

import me.curlpipesh.engine.Engine.EngineState;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.world.Chunk;

/**
 * @author audrey
 * @since 11/12/15.
 */
@SuppressWarnings("unused")
public interface IEntity {
    AxisAlignedBB getBoundingBox();

    boolean update(EngineState state);

    default boolean isColliding(final EngineState state) {
        return Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMin()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMax()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMax()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMin()));
    }
}
