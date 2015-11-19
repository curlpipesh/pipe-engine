package me.curlpipesh.engine.player;

import lombok.Getter;
import me.curlpipesh.engine.Engine.EngineState;
import me.curlpipesh.engine.entity.IEntity;
import me.curlpipesh.engine.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;

/**
 * @author audrey
 * @since 11/17/15.
 */
public class Player implements IEntity {
    @Getter
    private final AxisAlignedBB boundingBox = new AxisAlignedBB();

    @Override
    public boolean update(final EngineState state) {
        boundingBox.getPosition().x((Display.getWidth() / 2) + state.getOffset().x());
        boundingBox.getPosition().y((Display.getHeight() / 2) + state.getOffset().y());
        return true;
    }
}
