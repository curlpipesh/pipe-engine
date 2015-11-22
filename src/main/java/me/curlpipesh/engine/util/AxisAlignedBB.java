package me.curlpipesh.engine.util;

import lombok.Getter;
import lombok.Setter;
import me.curlpipesh.engine.world.Chunk;

/**
 * @author audrey
 * @since 11/12/15.
 */
@SuppressWarnings("unused")
public final class AxisAlignedBB {
    @Getter
    private final Vec2f position;

    @Getter
    private final Vec2f dimensions;

    public AxisAlignedBB() {
        this(0, 0);
    }

    public AxisAlignedBB(final float x, final float y) {
        this(x, y, Chunk.TILE_SIZE, Chunk.TILE_SIZE);
    }

    public AxisAlignedBB(final float x, final float y, final float w, final float h) {
        position = new Vec2f(x, y);
        dimensions = new Vec2f(w, h);
    }

    public float xMin() {
        return position.x();
    }

    public float yMin() {
        return position.y();
    }

    public float xMax() {
        return position.x() + dimensions.x();
    }

    public float yMax() {
        return position.y() + dimensions.y();
    }

    public boolean intersects(final AxisAlignedBB bb) {
        return (xMin() < bb.xMax() && xMin() > bb.xMin())
                || (xMax() < bb.xMax() && xMax() > bb.xMin())
                || (yMin() < bb.yMax() && yMin() > bb.yMin())
                || (yMax() < bb.yMax() && yMax() > bb.yMin());
    }
}
