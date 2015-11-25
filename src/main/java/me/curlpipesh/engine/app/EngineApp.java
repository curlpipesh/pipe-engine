package me.curlpipesh.engine.app;

import lombok.AccessLevel;
import lombok.Getter;
import me.curlpipesh.engine.Engine;
import me.curlpipesh.gl.util.DisplayUtil;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * @author audrey
 * @since 11/24/15.
 */
public abstract class EngineApp implements IEngineApp {
    @Getter(AccessLevel.PROTECTED)
    private final Engine engine;

    private long lastFrame;
    private long lastFPS;

    public EngineApp() {
        engine = new Engine();
    }

    @Override
    public void init() {
        DisplayUtil.buildDisplay(800, 600);
        Display.setLocation(100, 100);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 10, -10);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        lastFPS = getTime();
        getDelta();
    }

    @Override
    public void runApp() {
        while(!Display.isCloseRequested()) {
            final int delta = getDelta();
            update(delta);
            render(delta);
            Display.sync(getEngine().getFpsTarget());
        }
        Display.destroy();
    }

    @Override
    public void update(final int delta) {
        updateFPS();
    }

    @Override
    public void render(final int delta) {

    }


    /**
     * Calculate how many milliseconds have passed
     * since last frame.
     *
     * @return milliseconds passed since last frame
     */
    private int getDelta() {
        final long time = getTime();
        final int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Calculate the FPS
     */
    private void updateFPS() {
        if(getTime() - lastFPS > 1000) {
            engine.setFps(getEngine().getFpsCounter());
            engine.setFpsCounter(0);
            engine.setVaos(0);
            lastFPS += 1000;
        }
        engine.incrementFps();
    }
}
