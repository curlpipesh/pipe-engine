package me.curlpipesh.engine.render;

import lombok.Value;

/**
 * @author audrey
 * @since 11/17/15.
 */
@Value
public class Vertex {
    private final float x;
    private final float y;
    private final float z;
    private final int color;
    private final float u;
    private final float v;

    public Vertex(final float x, final float y, final int color) {
        this(x, y, 0, color);
    }

    public Vertex(final float x, final float y, final float z, final int color) {
        this(x, y, z, color, 0, 0);
    }

    public Vertex(final float x, final float y, final float z, final int color, final float u, final float v) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.u = u;
        this.v = v;
    }
}
