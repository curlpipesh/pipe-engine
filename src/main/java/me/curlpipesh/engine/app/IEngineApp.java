package me.curlpipesh.engine.app;

/**
 * @author audrey
 * @since 11/24/15.
 */
@SuppressWarnings("unused")
public interface IEngineApp {
    void init();

    void runApp();

    void update(int delta);

    void render(int delta);
}
