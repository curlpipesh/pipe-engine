package me.curlpipesh.engine;

import me.curlpipesh.engine.app.EngineApp;
import me.curlpipesh.engine.entity.player.Player;
import me.curlpipesh.engine.gui.impl.GuiIngame;
import me.curlpipesh.engine.profiler.Profiler;
import me.curlpipesh.engine.util.NoSuchTileException;
import me.curlpipesh.engine.util.Vec2f;
import me.curlpipesh.engine.world.Chunk;
import me.curlpipesh.engine.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

/**
 * @author audrey
 * @since 11/11/15.
 */
@SuppressWarnings("unused")
public class EngineTestApp extends EngineApp {
    private static final EngineTestApp instance = new EngineTestApp();


    private final Vec2f horizontalMotionVector = new Vec2f(0, 0), verticalMotionVector = new Vec2f(0, 0);

    public EngineTestApp() {
    }
    
    @Override
    public void init() {
        super.init();

        getEngine().setWorld(new World(getEngine(), "Test world", 0xDEADBEEFBABEL, 16, 4));
        getEngine().setPlayer(new Player(getEngine()));
        getEngine().getPlayer().getBoundingBox().getPosition().x((Display.getWidth() / 2) - (Chunk.TILE_SIZE / 2));
        getEngine().getPlayer().getBoundingBox().getPosition().y((Display.getHeight() / 2) - (Chunk.TILE_SIZE / 2));
        getEngine().getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);
        getEngine().getPlayer().getBoundingBox().getDimensions().y(Chunk.TILE_SIZE);

        getEngine().setCurrentGui(new GuiIngame(getEngine()));
        getEngine().getWorld().loadWorld();
        getEngine().getWorld().spawnPlayer(getEngine().getPlayer(), 3);
        getEngine().getWorld().meshWorld();
    }

    @Override
    public void update(final int delta) {
        super.update(delta);

        Profiler.startSection("input");

        if(Display.isActive()) {
            ////////////////////
            // Input handling //
            ////////////////////

            // 4 * ratio based off of 60 FPS
            final float offsetAmount = 4F * ((float) delta / 16.67F);
            horizontalMotionVector.x(0F);
            verticalMotionVector.y(0F);
            float verticalOffsetTotal = 0F;
            float horizontalOffsetTotal = 0F;
            /*if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                verticalMotionVector.y(offsetAmount);
                verticalOffsetTotal += offsetAmount;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                verticalMotionVector.y(-offsetAmount);
                verticalOffsetTotal -= offsetAmount;
            }*/
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                horizontalMotionVector.x(offsetAmount);
                horizontalOffsetTotal += offsetAmount;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                horizontalMotionVector.x(-offsetAmount);
                horizontalOffsetTotal -= offsetAmount;
            }

            while(Keyboard.next()) {
                if(Keyboard.getEventKeyState()) {
                    if(Keyboard.getEventKey() == Keyboard.KEY_D) {
                        getEngine().setInDebugMode(!getEngine().isInDebugMode());
                    }
                    if(Keyboard.getEventKey() == Keyboard.KEY_UP) {
                        getEngine().getPlayer().jump();
                        getEngine().getPlayer().applyVector(new Vec2f(0, 64));
                    }
                }
            }

            //////////////////////////////////////
            // Applying input vectors to player //
            //////////////////////////////////////

            if(horizontalOffsetTotal != 0) {
                if(getEngine().getPlayer().applyVector(horizontalMotionVector)) {
                    getEngine().getOffset().addX(horizontalOffsetTotal);
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
                    if(getEngine().getPlayer().applyVector(horizontalMotionVector)) {
                        getEngine().getOffset().addX(horizontalOffsetTotal);
                    }
                }
            }

            /*if(verticalOffsetTotal != 0) {
                if(getEngine().getPlayer().applyVector(verticalMotionVector)) {
                    getEngine().getOffset().addY(verticalOffsetTotal);
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
                    if(getEngine().getPlayer().applyVector(verticalMotionVector)) {
                        getEngine().getOffset().addY(verticalOffsetTotal);
                    }
                }
            }*/

            //////////////////////////
            // Mouse event handling //
            //////////////////////////

            while(Mouse.next()) {
                if(Mouse.getEventButtonState()) {
                    if(Mouse.getEventButton() == 0) {
                        final double x = getEngine().getOffset().x() + Mouse.getX();
                        final double y = getEngine().getOffset().y() + Mouse.getY();
                        try {
                            getEngine().getWorld().setColorAtPosition(x, y, 0xFFFFFFFF);
                            getEngine().getLogger().warning("Color change at (" + x + ", " + y + ")");
                        } catch(final NoSuchTileException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        ///////////////////////////
        // World update handling //
        ///////////////////////////

        Profiler.endStartSection("worldUpdate");
        getEngine().getWorld().update(delta);
        Profiler.endSection();
    }

    @Override
    public void render(final int delta) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        Profiler.startSection("worldRender");
        getEngine().getWorld().render(getEngine().getOffset());
        Profiler.endStartSection("gui");
        getEngine().getCurrentGui().render(getEngine(), delta);
        Profiler.endSection();

        try {
            Util.checkGLError();
        } catch(final Exception e) {
            getEngine().getLogger().severe("OpenGL error: " + Util.translateGLErrorString(GL11.glGetError()));
        }
        Display.update();
    }


    public static void main(final String[] args) {
        instance.init();
        instance.runApp();
    }
}
