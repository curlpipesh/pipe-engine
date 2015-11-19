package me.curlpipesh.game.player;

import lombok.Getter;
import me.curlpipesh.game.Game.GameState;
import me.curlpipesh.game.entity.IEntity;
import me.curlpipesh.game.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class Player implements IEntity {
    @Getter
    private final AxisAlignedBB boundingBox = new AxisAlignedBB();

    @Override
    public boolean update(final GameState state) {
        boundingBox.getPosition().x((Display.getWidth() / 2) + state.getOffset().x());
        boundingBox.getPosition().y((Display.getHeight() / 2) + state.getOffset().y());
        return true;
    }
}
