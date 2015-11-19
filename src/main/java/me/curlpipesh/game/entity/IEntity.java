package me.curlpipesh.game.entity;

import me.curlpipesh.game.Game;
import me.curlpipesh.game.Game.GameState;
import me.curlpipesh.game.util.AxisAlignedBB;
import me.curlpipesh.game.world.Chunk;

/**
 * @author audrey
 * @since 11/12/15.
 */
@SuppressWarnings("unused")
public interface IEntity {
    AxisAlignedBB getBoundingBox();

    boolean update(GameState state);

    default boolean isColliding(final GameState state) {
        return Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMin()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMax()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMin(), getBoundingBox().yMax()))
                || Chunk.isSolid(state.getWorld().getTileAtPosition(getBoundingBox().xMax(), getBoundingBox().yMin()));
    }
}
