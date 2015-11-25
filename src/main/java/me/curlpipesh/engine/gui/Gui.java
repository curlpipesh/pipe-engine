package me.curlpipesh.engine.gui;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.Engine;

/**
 * @author audrey
 * @since 11/23/15.
 */
@SuppressWarnings("unused")
public abstract class Gui implements IGui {
    @Getter(AccessLevel.PROTECTED)
    private final Engine engine;

    public Gui(final Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(final int delta) {
    }

    @Override
    public void render(final Engine engine, final int delta) {
    }
}
