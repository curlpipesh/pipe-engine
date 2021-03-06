package me.curlpipesh.engine.render;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.util.Vec2f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author audrey
 * @since 11/17/15.
 */
@SuppressWarnings("unused")
public class RenderRequest {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Getter(AccessLevel.PACKAGE)
    private final List<Vertex> vertices;

    private int color;

    @Getter
    private int texture = 0;

    @Getter
    private boolean isPositionAbsolute = false;

    @Getter
    private boolean isDebug = false;

    @Getter(AccessLevel.PACKAGE)
    private final RenderType type;

    @Getter(AccessLevel.PACKAGE)
    private final int mode;

    @Getter(AccessLevel.PACKAGE)
    private final String name;

    @Getter(AccessLevel.PACKAGE)
    private boolean compiled = false;

    @Getter(AccessLevel.PACKAGE)
    private final Vec2f position = new Vec2f(0, 0);

    @Getter(AccessLevel.PACKAGE)
    private final Vec2f dimensions = new Vec2f(0, 0);

    public RenderRequest(final String name, final RenderType type, final int mode) {
        vertices = new ArrayList<>();
        color = 0;
        this.name = name;
        this.type = type;
        this.mode = mode;
    }

    public RenderRequest vertex(final float x, final float y) {
        if(compiled) {
            throw new IllegalStateException("Can't add vertices to a finalized render request!");
        }
        vertex(x, y, 0);
        return this;
    }

    public RenderRequest vertex(final float x, final float y, final float z) {
        if(compiled) {
            throw new IllegalStateException("Can't add vertices to a finalized render request!");
        }
        return vertex(x, y, z, 0, 0);
    }

    public RenderRequest vertex(final float x, final float y, final float z, final float u, final float v) {
        vertices.add(new Vertex(x, y, z, color, u, v));
        return this;
    }

    public RenderRequest color(final int color) {
        if(compiled) {
            throw new IllegalStateException("Can't add colors to a finalized render request!");
        }
        this.color = color;
        return this;
    }

    public RenderRequest position(final float x, final float y) {
        position.x(x);
        position.y(y);
        return this;
    }

    public RenderRequest dimension(final float w, final float h) {
        dimensions.x(w);
        dimensions.y(h);
        return this;
    }

    public RenderRequest texture(final int texture) {
        this.texture = texture;
        return this;
    }

    public RenderRequest absolute(final boolean e) {
        isPositionAbsolute = e;
        return this;
    }

    public RenderRequest debug(final boolean e) {
        isDebug = e;
        return this;
    }

    public RenderRequest compile() {
        compiled = true;
        return this;
    }
}
