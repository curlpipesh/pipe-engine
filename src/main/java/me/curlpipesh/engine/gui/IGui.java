package me.curlpipesh.engine.gui;

import me.curlpipesh.engine.Engine;

/**
 * @author audrey
 * @since 11/23/15.
 */
@SuppressWarnings("unused")
public interface IGui {
    void update(int delta);

    void render(Engine engine, int delta);
}
