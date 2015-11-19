package me.curlpipesh.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import me.curlpipesh.game.player.Player;
import me.curlpipesh.game.logging.GeneralLogHandler;
import me.curlpipesh.game.util.JavaUtils;
import me.curlpipesh.game.util.Vec2d;
import me.curlpipesh.game.world.World;
import me.curlpipesh.gl.util.DisplayUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author audrey
 * @since 11/11/15.
 */
@SuppressWarnings("unused")
public class Game {
    private static final Game instance = new Game();

    private long lastFrame;
    private long lastFPS;

    @Getter
    private static final Logger logger;

    @Getter
    private final GameState state = new GameState();

    public static void main(final String[] args) {
        instance.run();
    }

    static {
        logger = Logger.getLogger("Engine");
        logger.setUseParentHandlers(false);
        logger.setLevel(instance.getState().isInTestMode() ? Level.INFO : Level.FINEST);
        logger.addHandler(new GeneralLogHandler());
    }

    private void run() {
        DisplayUtil.buildDisplay(800, 600);
        Display.setLocation(100, 100);
        DisplayUtil.basicOpenGLInit();

        // Breaking my own rules about accessing stuff like this, I know
        // TODO: Change that eventually
        state.glVendor = GL11.glGetString(GL11.GL_VENDOR);
        state.glRenderer = GL11.glGetString(GL11.GL_RENDERER);
        state.glVersion = GL11.glGetString(GL11.GL_VERSION);
        // TODO: Check these for something?
        //state.glExtensions = GL11.glGetString(GL11.GL_EXTENSIONS);
        printTechnicalInfo();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        state.setWorld(new World(state, "Test world"));
        state.setPlayer(new Player());
        lastFPS = getTime();
        getDelta();
        state.getWorld().loadWorld();
        state.getWorld().meshWorld();
        while(!Display.isCloseRequested()) {
            final int delta = getDelta();
            update(delta);
            render(delta);
            Display.sync(60);
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
        logger.config("OpenGL Extensions:  " + state.getGlExtensions());
    }

    private void update(final int delta) {
        updateFPS();
        state.getWorld().update(delta);
        // 4 * ratio based off of 60 FPS
        final double offsetAmount = 4D * ((double) delta / 16.67D);
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

        if(Display.isActive()) {
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
    }

    private void render(final int delta) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        state.getWorld().render(state.getOffset());
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
            logger.config("FPS: " + getState().getFps());
            state.setFps(0);
            lastFPS += 1000;
        }
        state.incrementFps();
    }

    @Value
    public static final class GameState {
        private final Vec2d offset = new Vec2d(0, 0);

        private final String runtimeName;
        private final String runtimeVersion;
        private final String jvmName;
        private final String cpuArch;
        private final String osName;
        private final long maxMem;
        private final long totalMem;
        private final int cpuThreads;
        private final boolean isDebuggerAttached;
        private final boolean isRunningFromJar;

        @NonFinal
        private String glVendor;
        @NonFinal
        private String glRenderer;
        @NonFinal
        private String glVersion;
        @NonFinal
        private String glExtensions;

        @Setter
        @NonFinal
        private World world;

        @Setter
        @NonFinal
        private Player player;

        @Setter(AccessLevel.PRIVATE)
        @NonFinal
        private int fps;

        boolean inTestMode;

        private GameState() {
            // Tests whether or not we're in JUnit test mode. If we are, some stuff (eg. meshing) is disabled
            inTestMode = Arrays.stream(Thread.currentThread().getStackTrace())
                    .filter(e -> e.getClassName().contains("org.junit")).count() > 0;

            runtimeName = System.getProperty("java.runtime.name");
            runtimeVersion = System.getProperty("java.runtime.version");
            jvmName = System.getProperty("java.vm.name");
            cpuArch = System.getProperty("os.arch") + "/" + System.getProperty("sun.arch.data.model");
            osName = System.getProperty("os.name");
            maxMem = Runtime.getRuntime().maxMemory() / 1048576L;
            totalMem = Runtime.getRuntime().totalMemory() / 1048576L;
            cpuThreads = Runtime.getRuntime().availableProcessors();
            isDebuggerAttached = JavaUtils.isDebuggerAttached();
            isRunningFromJar = JavaUtils.isRunningInJar();
        }

        private void incrementFps() {
            ++fps;
        }
    }
}
