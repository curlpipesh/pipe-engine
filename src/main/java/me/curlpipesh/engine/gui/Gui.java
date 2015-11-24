package me.curlpipesh.engine.gui;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.EngineState;

/**
 * @author audrey
 * @since 11/23/15.
 */
@SuppressWarnings("unused")
public abstract class Gui implements IGui {
    @Getter(AccessLevel.PROTECTED)
    private final EngineState state;

    public Gui(final EngineState state) {
        this.state = state;
    }

    @Override
    public void update(final int delta) {
    }

    @Override
    public void render(final EngineState state, final int delta) {
    }
}
