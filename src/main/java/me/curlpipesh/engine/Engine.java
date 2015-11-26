package me.curlpipesh.engine;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import me.curlpipesh.engine.entity.player.Player;
import me.curlpipesh.engine.gui.IGui;
import me.curlpipesh.engine.logging.LoggerFactory;
import me.curlpipesh.engine.render.FontRenderer;
import me.curlpipesh.engine.render.RenderServer;
import me.curlpipesh.engine.util.JavaUtils;
import me.curlpipesh.engine.util.Vec2f;
import me.curlpipesh.engine.world.World;

import java.util.Arrays;
import java.util.logging.Logger;

@Value
public final class Engine {

    private final Vec2f offset = new Vec2f(0, 0);

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

    private final int fpsTarget = 60;

    @Setter
    @NonFinal
    @SuppressWarnings("FieldMayBeFinal")
    private String glVendor = "", glRenderer = "", glVersion = "", glExtensions = "";

    private static final Logger logger;

    @Setter
    @NonFinal
    private World world;

    private final RenderServer renderServer;

    @NonFinal
    private Player player;

    @Setter
    @NonFinal
    private int fpsCounter;

    @Setter
    @NonFinal
    private int fps;

    @Setter
    @NonFinal
    @SuppressWarnings("FieldMayBeFinal")
    private int vaos = 0;

    private final boolean inTestMode;

    @Setter
    @NonFinal
    @SuppressWarnings("FieldMayBeFinal")
    private boolean inDebugMode = false;

    private final FontRenderer fontRenderer;

    @Setter
    @NonFinal
    private IGui currentGui;

    private final Vec2f gravityVector = new Vec2f(0, -4.9F);

    public Engine() {
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

        if(!isInTestMode()) {
            fontRenderer = new FontRenderer(this);
            renderServer = new RenderServer(this);
        } else {
            fontRenderer = null;
            renderServer = null;
        }
    }

    public void init() {
        fontRenderer.init();
    }

    public void setPlayer(final Player player) {
        if(world.getEntities().contains(this.player)) {
            world.getEntities().remove(this.player);
        }
        this.player = player;
        world.getEntities().add(this.player);
    }
    
    public Logger getLogger() {
        return logger;
    }

    public void incrementFps() {
        ++fpsCounter;
    }

    static {
        logger = LoggerFactory.getLogger(Arrays.stream(Thread.currentThread().getStackTrace())
                .filter(e -> e.getClassName().contains("org.junit")).count() > 0, "Engine");
    }
}
