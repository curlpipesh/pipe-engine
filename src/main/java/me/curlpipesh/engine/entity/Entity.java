package me.curlpipesh.engine.entity;

import lombok.Getter;
import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.util.AxisAlignedBB;
import me.curlpipesh.engine.util.NoSuchTileException;
import me.curlpipesh.engine.util.Vec2f;
import me.curlpipesh.engine.world.Chunk;

/**
 * @author audrey
 * @since 11/24/15.
 */
public abstract class Entity implements IEntity {
    @Getter
    private final AxisAlignedBB boundingBox;

    private final Engine engine;

    private boolean isJumping = false;

    private final Vec2f jumpVector = new Vec2f(0, 0);

    private static final float OFFSET = 0.5F;

    public Entity(final Engine engine) {
        this.engine = engine;
        boundingBox = new AxisAlignedBB();
    }

    @Override
    public boolean update(final Engine engine) {
        if(isJumping) {
            engine.getLogger().config(jumpVector.y() + "");
            if(jumpVector.y() > 1) {
                if(!applyVector(jumpVector)) {
                    engine.getLogger().warning("Couldn't apply jump vector!?");
                } else {
                    jumpVector.y(jumpVector.y() / 2);
                }
            } else {
                applyVector(jumpVector);
                jumpVector.y(0);
                isJumping = false;
            }
        } else {
            if(isInAir()) {
                applyVector(engine.getGravityVector());
            }
        }
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
        if(isCollidingWithWorld(engine)) {
            getBoundingBox().getPosition().sub(v);
            return false;
        }
        return true;
    }

    @Override
    public boolean isCollidingWithWorld(final Engine engine) {
        return isSolidWrapper(engine, getBoundingBox().xMin() + OFFSET, getBoundingBox().yMin() + OFFSET)
                || isSolidWrapper(engine, getBoundingBox().xMax() - OFFSET, getBoundingBox().yMax() - OFFSET)
                || isSolidWrapper(engine, getBoundingBox().xMin() + OFFSET, getBoundingBox().yMax() - OFFSET)
                || isSolidWrapper(engine, getBoundingBox().xMax() - OFFSET, getBoundingBox().yMin() + OFFSET);
    }

    private boolean isSolidWrapper(final Engine engine, final float x, final float y) {
        try {
            return Chunk.isSolid(engine.getWorld().getTileAtPosition(x, y));
        } catch(final NoSuchTileException e) {
            // e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isInAir() {
        return !(Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMin() + OFFSET, getBoundingBox().yMin() - 1F + OFFSET))
                || Chunk.isSolid(engine.getWorld().getTileAtPosition(getBoundingBox().xMax() - OFFSET, getBoundingBox().yMin() - 1F + OFFSET)));
    }

    @Override
    public boolean isJumping() {
        return isJumping;
    }

    @Override
    public void jump() {
        System.out.println("Jump!");
        if(isJumping) {
            return;
        }
        isJumping = true;
        jumpVector.y(Chunk.SIZE);
    }
}
