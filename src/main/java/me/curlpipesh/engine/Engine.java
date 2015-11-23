package me.curlpipesh.engine;

import lombok.Getter;
import me.curlpipesh.engine.entity.player.Player;
import me.curlpipesh.engine.logging.GeneralLogHandler;
import me.curlpipesh.engine.profiler.Profiler;
import me.curlpipesh.engine.profiler.Profiler.Section;
import me.curlpipesh.engine.render.FontRenderer;
import me.curlpipesh.engine.world.Chunk;
import me.curlpipesh.engine.world.World;
import me.curlpipesh.gl.util.DisplayUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author audrey
 * @since 11/11/15.
 */
@SuppressWarnings("unused")
public class Engine {
    private static final Engine instance = new Engine();

    private long lastFrame;
    private long lastFPS;

    private int debugFps = 0;

    /**
     * We don't use the <tt>LoggerFactory</tt> for this because it's before
     * everything else is ready, meaning that we don't have a usable
     * <tt>EngineState</tt> etc.
     */
    @Getter
    private static final Logger logger;

    @Getter
    private EngineState state;

    private Engine() {}

    private void run() {
        DisplayUtil.buildDisplay(800, 600);
        Display.setLocation(100, 100);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 10, -10);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);

        state = new EngineState();

        logger.setLevel(instance.getState().isInTestMode() ? Level.INFO : Level.FINEST);

        state.setGlVendor(GL11.glGetString(GL11.GL_VENDOR));
        state.setGlRenderer(GL11.glGetString(GL11.GL_RENDERER));
        state.setGlVersion(GL11.glGetString(GL11.GL_VERSION));
        state.setGlExtensions(GL11.glGetString(GL11.GL_EXTENSIONS));
        printTechnicalInfo();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        state.setWorld(new World(state, "Test world", 0xDEADBEEFBABEL, 16, 4));
        state.setPlayer(new Player());
        state.getPlayer().getBoundingBox().getPosition().x((Display.getWidth() / 2) - (Chunk.TILE_SIZE / 2));
        state.getPlayer().getBoundingBox().getPosition().y((Display.getHeight() / 2) - (Chunk.TILE_SIZE / 2));
        state.getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);
        state.getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);
        lastFPS = getTime();
        getDelta();
        state.getWorld().loadWorld();
        state.getWorld().meshWorld();
        while(!Display.isCloseRequested()) {
            final int delta = getDelta();
            update(delta);
            render(delta);
            Display.sync(state.getFpsTarget());
        }
        Display.destroy();
    }

    private void printTechnicalInfo() {
        logger.config("Runtime info:");
        logger.config("-------------");
        logger.config("Runtime name:       " + state.getRuntimeName());
        logger.config("Runtime version:    " + state.getRuntimeVersion());
        logger.config("JVM:                " + state.getJvmName());
        logger.config("CPU Architecture:   " + state.getCpuArch());
        logger.config("OS Name:            " + state.getOsName());
        logger.config("Max Memory:         " + state.getMaxMem());
        logger.config("Total Memory:       " + state.getTotalMem());
        logger.config("CPU Threads:        " + state.getCpuThreads());
        logger.config("Debugger attached?: " + state.isDebuggerAttached());
        logger.config("Running from JAR?:  " + state.isRunningFromJar());
        logger.config("OpenGL Vendor:      " + state.getGlVendor());
        logger.config("OpenGL Renderer:    " + state.getGlRenderer());
        logger.config("OpenGL Version:     " + state.getGlVersion());
        //logger.config("OpenGL Extensions:  " + state.getGlExtensions());
    }

    private void update(final int delta) {
        updateFPS();
        Profiler.startSection("input");

        if(Display.isActive()) {
            // 4 * ratio based off of 60 FPS
            // TODO: Prevent movement if player would intersect with solid tile
            final float offsetAmount = 4F * ((float) delta / 16.67F);
            if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                state.getOffset().addY(offsetAmount);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                state.getOffset().addY(-offsetAmount);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                state.getOffset().addX(offsetAmount);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                state.getOffset().addX(-offsetAmount);
            }

            while(Mouse.next()) {
                if(Mouse.getEventButtonState()) {
                    if(Mouse.getEventButton() == 0) {
                        final double x = state.getOffset().x() + Mouse.getX();
                        final double y = state.getOffset().y() + Mouse.getY();
                        logger.warning("Color change at (" + x + ", " + y + ")");
                        state.getWorld().setColorAtPosition(x, y, 0xFFFFFFFF);
                    }
                }
            }
        }

        Profiler.endStartSection("worldUpdate");
        state.getWorld().update(delta);
        //state.getPlayer().update(state);
        Profiler.endSection();
    }

    private void render(final int delta) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        Profiler.startSection("worldRender");
        state.getWorld().render(state.getOffset());
        //state.getWorld().getRenderServer().request(state.getPlayer().render(state.getOffset()));

        Profiler.endStartSection("profilingData");
        GL11.glTranslated(0, 0, -1);


        state.getFontRenderer().drawString("FPS: " + debugFps, 2, 2);

        int y = Display.getHeight() - FontRenderer.GLYPH_SIZE - 2;
        state.getFontRenderer().drawString("Profiling data:", 2, y);
        state.getFontRenderer().drawString("---------------", 2, y -= FontRenderer.GLYPH_SIZE + 2);
        for(final Section s : Profiler.getSections()) {
            state.getFontRenderer().drawString(s.getSection() + ": " + s.getAverageTime() + "ms", 2, y -= FontRenderer.GLYPH_SIZE + 2);
        }
        state.getFontRenderer().drawString("World meshes: " + state.getRenderServer().getMeshes().size(), 2, y -= FontRenderer.GLYPH_SIZE + 2);
        //noinspection UnusedAssignment
        state.getFontRenderer().drawString(
                String.format("Player: (%.2f, %.2f)<%.2f, %.2f>",
                        state.getPlayer().getBoundingBox().xMin(), state.getPlayer().getBoundingBox().yMin(),
                        state.getPlayer().getBoundingBox().xMax(), state.getPlayer().getBoundingBox().yMax()),
                2, y -= FontRenderer.GLYPH_SIZE + 2);

        GL11.glTranslated(0, 0, 1);
        Profiler.endSection();

        try {
            Util.checkGLError();
        } catch(final Exception e) {
            logger.severe("OpenGL error: " + Util.translateGLErrorString(GL11.glGetError()));
        }
        Display.update();
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
            debugFps = state.getFps();
            state.setFps(0);
            state.setVaos(0);
            lastFPS += 1000;
        }
        state.incrementFps();
    }

    public static void main(final String[] args) {
        instance.run();
    }

    static {
        logger = Logger.getLogger("Engine");
        logger.setUseParentHandlers(false);
        logger.addHandler(new GeneralLogHandler());
    }
}
