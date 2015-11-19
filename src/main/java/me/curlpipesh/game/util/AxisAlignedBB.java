package me.curlpipesh.game.util;

import lombok.Getter;
import me.curlpipesh.game.world.Chunk;

/**
 * @author audrey
 * @since 11/12/15.
 */
public final class AxisAlignedBB {
    @Getter
    private final Vec2d position;

    @Getter
    private final Vec2d dimensions;

    public AxisAlignedBB() {
        this(0, 0);
    }

    public AxisAlignedBB(final double x, final double y) {
        this(x, y, Chunk.TILE_SIZE, Chunk.TILE_SIZE);
    }

    public AxisAlignedBB(final double x, final double y, final double w, final double h) {
        position = new Vec2d(x, y);
        dimensions = new Vec2d(w, h);
    }

    public double xMin() {
        return position.x();
    }

    public double yMin() {
        return position.y();
    }

    public double xMax() {
        return position.x() + dimensions.x();
    }

    public double yMax() {
        return position.y() + dimensions.y();
    }

    public boolean intersects(final AxisAlignedBB bb) {
        return (xMin() < bb.xMax() && xMin() > bb.xMin())
                || (xMax() < bb.xMax() && xMax() > bb.xMin())
                || (yMin() < bb.yMax() && yMin() > bb.yMin())
                || (yMax() < bb.yMax() && yMax() > bb.yMin());
    }
}
