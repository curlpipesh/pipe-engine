package me.curlpipesh.engine.gui.impl;

import me.curlpipesh.engine.EngineState;
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
    public GuiIngame(final EngineState state) {
        super(state);
    }

    @Override
    public void update(final int delta) {
        // TODO: Something goes here?
    }

    @Override
    public void render(final EngineState state, final int delta) {
        GL11.glTranslated(0, 0, -1);

        state.getFontRenderer().drawString("FPS: " + state.getFps(), 2, 2);

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
    }
}
