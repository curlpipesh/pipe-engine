package me.curlpipesh.engine.entity;

import lombok.Getter;
import me.curlpipesh.engine.EngineState;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.util.Vec2f;

/**
 * @author audrey
 * @since 11/24/15.
 */
public abstract class Entity implements IEntity {
    @Getter
    private final AxisAlignedBB boundingBox;

    private final EngineState state;

    public Entity(final EngineState state) {
        this.state = state;
        boundingBox = new AxisAlignedBB();
    }

    @Override
    public boolean update(final EngineState state) {
        return true;
    }

    @Override
    public void setWorldPos(final float x, final float y) {
        getBoundingBox().getPosition().x(x);
        getBoundingBox().getPosition().y(y);
    }

    @Override
    public boolean applyVector(final Vec2f v) {
        getBoundingBox().getPosition().add(v);
        if(isColliding(state)) {
            getBoundingBox().getPosition().sub(v).sub(v);
            return false;
        }
        return true;
    }
}
