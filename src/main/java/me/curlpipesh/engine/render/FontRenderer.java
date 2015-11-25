package me.curlpipesh.engine.render;

import me.curlpipesh.engine.Engine;
import me.curlpipesh.gl.texture.TextureLoader;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class FontRenderer {
    @SuppressWarnings("FieldCanBeLocal")
    private final String charString =
            // @formatter:off
            ("                " +
             "                " +
             " !\"#$%&'()*+,-./" +
             "0123456789:;<=>?" +
             "@ABCDEFGHIJKLMNO" +
             "PQRSTUVWXYZ[\\]^_" +
             "`abcdefghijklmno" +
             "pqrstuvwxyz{|}~ ");
    // @formatter:on

    private final int textureId;
    
    public static final int GLYPH_SIZE = 16;
    private static final int FONT_ATLAS_SIZE = 256;
    private static final float GLYPH_UV_SIZE = GLYPH_SIZE / (float) FONT_ATLAS_SIZE;

    private final Engine engine;

    public FontRenderer(final Engine engine) {
        this.engine = engine;
        textureId = TextureLoader.loadTexture(TextureLoader.loadImage("/me/curlpipesh/engine/font/font.png"));
    }

    public void drawString(final String s, int x, final int y) {
        if(s.length() > 65536) {
            throw new IllegalArgumentException("Provided string is too long!?");
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4d(1, 1, 1, 1);

        for(final char e : s.toCharArray()) {
            drawChar(e, x, y);
            x += GLYPH_SIZE;
        }

        GL11.glColor4d(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    
    private void drawChar(final char c, final int x, final int y) {
        final float xIndex = (charString.contains("" + c) ? (charString.indexOf(c) % 16) * GLYPH_SIZE : 0) / (float) FONT_ATLAS_SIZE;
        final float yIndex = (charString.contains("" + c) ? (charString.indexOf(c) / 16) * GLYPH_SIZE : 0) / (float) FONT_ATLAS_SIZE;


        engine.getRenderServer().request(new RenderRequest("Render '" + c + "'", RenderType.VAO, GL11.GL_QUADS)
                .color(0xFFFFFFFF)
                .texture(textureId)
                .vertex(x, y, 0, xIndex, yIndex + GLYPH_UV_SIZE)
                .vertex(x, y + GLYPH_SIZE, 0, xIndex, yIndex)
                .vertex(x + GLYPH_SIZE, y + GLYPH_SIZE, 0, xIndex + GLYPH_UV_SIZE, yIndex)
                .vertex(x + GLYPH_SIZE, y, 0, xIndex + GLYPH_UV_SIZE, yIndex + GLYPH_UV_SIZE)
                .absolute(true)
                .compile());
    }
}
