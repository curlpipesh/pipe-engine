package me.curlpipesh.engine;

import lombok.Getter;
import me.curlpipesh.engine.entity.player.Player;
import me.curlpipesh.engine.gui.impl.GuiIngame;
import me.curlpipesh.engine.logging.GeneralLogHandler;
import me.curlpipesh.engine.profiler.Profiler;
import me.curlpipesh.engine.util.NoSuchTileException;
import me.curlpipesh.engine.util.Vec2f;
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

    /**
     * We don't use the <tt>LoggerFactory</tt> for this because it's before
     * everything else is ready, meaning that we don't have a usable
     * <tt>EngineState</tt> etc.
     */
    @Getter
    private static final Logger logger;

    @Getter
    private EngineState state;

    private final Vec2f horizontalMotionVector = new Vec2f(0, 0), verticalMotionVector = new Vec2f(0, 0);

    private Engine() {
    }

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
        state.setPlayer(new Player(state));
        state.getPlayer().getBoundingBox().getPosition().x((Display.getWidth() / 2) - (Chunk.TILE_SIZE / 2));
        state.getPlayer().getBoundingBox().getPosition().y((Display.getHeight() / 2) - (Chunk.TILE_SIZE / 2));
        state.getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);
        state.getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);

        state.setCurrentGui(new GuiIngame(state));

        lastFPS = getTime();
        getDelta();
        state.getWorld().loadWorld();
        state.getWorld().spawnPlayer(state.getPlayer());
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
            // TODO: Prevent movement if player would intersect with solid tile
            // Better solution: Store player position, do world update and
            // stuff, then update offset by newPos - oldPos.

            // 4 * ratio based off of 60 FPS
            final float offsetAmount = 4F * ((float) delta / 16.67F);
            horizontalMotionVector.x(0F);
            verticalMotionVector.y(0F);
            float verticalOffsetTotal = 0F;
            float horizontalOffsetTotal = 0F;
            if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                //state.getOffset().addY(offsetAmount);
                verticalMotionVector.y(offsetAmount);
                verticalOffsetTotal += offsetAmount;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                //state.getOffset().addY(-offsetAmount);
                verticalMotionVector.y(-offsetAmount);
                verticalOffsetTotal -= offsetAmount;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                //state.getOffset().addX(offsetAmount);
                horizontalMotionVector.x(offsetAmount);
                horizontalOffsetTotal += offsetAmount;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                //state.getOffset().addX(-offsetAmount);
                horizontalMotionVector.x(-offsetAmount);
                horizontalOffsetTotal -= offsetAmount;
            }

            if(horizontalOffsetTotal != 0) {
                if(state.getPlayer().applyVector(horizontalMotionVector)) {
                    state.getOffset().addX(horizontalOffsetTotal);
                } else {
                    if(horizontalOffsetTotal > 0) {
                        while(horizontalOffsetTotal > 0) {
                            horizontalMotionVector.addX(-0.1F);
                            horizontalOffsetTotal -= 0.1F;
                        }
                        if(horizontalOffsetTotal < 0) {
                            horizontalOffsetTotal = 0;
                        }
                    } else if(horizontalOffsetTotal < 0) {
                        while(horizontalOffsetTotal < 0) {
                            horizontalMotionVector.addX(0.1F);
                            horizontalOffsetTotal += 0.1F;
                        }
                        if(horizontalOffsetTotal > 0) {
                            horizontalOffsetTotal = 0;
                        }
                    }
                    if(state.getPlayer().applyVector(horizontalMotionVector)) {
                        state.getOffset().addX(horizontalOffsetTotal);
                    }
                }
            }

            if(verticalOffsetTotal != 0) {
                if(state.getPlayer().applyVector(verticalMotionVector)) {
                    state.getOffset().addY(verticalOffsetTotal);
                } else {
                    if(verticalOffsetTotal > 0) {
                        while(verticalOffsetTotal > 0) {
                            verticalMotionVector.addY(-0.1F);
                            verticalOffsetTotal -= 0.1F;
                        }
                        if(verticalOffsetTotal < 0) {
                            verticalOffsetTotal = 0;
                        }
                    } else if(verticalOffsetTotal < 0) {
                        while(verticalOffsetTotal < 0) {
                            verticalMotionVector.addY(0.1F);
                            verticalOffsetTotal += 0.1F;
                        }
                        if(verticalOffsetTotal > 0) {
                            verticalOffsetTotal = 0;
                        }
                    }
                    if(state.getPlayer().applyVector(verticalMotionVector)) {
                        state.getOffset().addY(verticalOffsetTotal);
                    }
                }
            }

            // TODO: Proper input handling
            while(Mouse.next()) {
                if(Mouse.getEventButtonState()) {
                    if(Mouse.getEventButton() == 0) {
                        final double x = state.getOffset().x() + Mouse.getX();
                        final double y = state.getOffset().y() + Mouse.getY();
                        try {
                            state.getWorld().setColorAtPosition(x, y, 0xFFFFFFFF);
                            logger.warning("Color change at (" + x + ", " + y + ")");
                        } catch(final NoSuchTileException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        Profiler.endStartSection("worldUpdate");
        state.getWorld().update(delta);
        Profiler.endSection();
    }

    private void render(final int delta) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        Profiler.startSection("worldRender");
        state.getWorld().render(state.getOffset());
        Profiler.endStartSection("gui");
        state.getCurrentGui().render(state, delta);
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
            state.setFps(state.getFpsCounter());
            state.setFpsCounter(0);
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
