package me.curlpipesh.engine.gui;

import me.curlpipesh.engine.EngineState;

/**
 * @author audrey
 * @since 11/23/15.
 */
@SuppressWarnings("unused")
public interface IGui {
    void update(int delta);

    void render(EngineState state, int delta);
}
