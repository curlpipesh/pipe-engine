package me.curlpipesh.engine.gui.impl;

import me.curlpipesh.engine.Engine;
import me.curlpipesh.engine.gui.Gui;
import me.curlpipesh.engine.profiler.Profiler;
import me.curlpipesh.engine.profiler.Profiler.Section;
import me.curlpipesh.engine.render.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * @author audrey
 * @since 11/23/15.
 */
public class GuiIngame extends Gui {
    public GuiIngame(final Engine engine) {
        super(engine);
    }

    @Override
    public void update(final int delta) {
    }

    @Override
    public void render(final Engine engine, final int delta) {
        GL11.glTranslated(0, 0, -1);

        engine.getFontRenderer().drawString("FPS: " + engine.getFps(), 2, 2);

        int y = Display.getHeight() - FontRenderer.GLYPH_SIZE - 2;
        engine.getFontRenderer().drawString("Profiling data:", 2, y);
        engine.getFontRenderer().drawString("---------------", 2, y -= FontRenderer.GLYPH_SIZE + 2);
        for(final Section s : Profiler.getSections()) {
            engine.getFontRenderer().drawString(s.getSection() + ": " + s.getAverageTime() + "ms", 2, y -= FontRenderer.GLYPH_SIZE + 2);
        }
        engine.getFontRenderer().drawString("World meshes: " + engine.getRenderServer().getMeshes().size(), 2, y -= FontRenderer.GLYPH_SIZE + 2);
        //noinspection UnusedAssignment
        engine.getFontRenderer().drawString(
                String.format("Player: (%.2f, %.2f)<%.2f, %.2f>",
                        engine.getPlayer().getBoundingBox().xMin(), engine.getPlayer().getBoundingBox().yMin(),
                        engine.getPlayer().getBoundingBox().xMax(), engine.getPlayer().getBoundingBox().yMax()),
                2, y -= FontRenderer.GLYPH_SIZE + 2);

        GL11.glTranslated(0, 0, 1);
    }
}
