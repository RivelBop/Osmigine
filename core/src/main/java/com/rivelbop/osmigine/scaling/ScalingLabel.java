package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;

public final class ScalingLabel extends ScalingElement {
    private final Color color = new Color(1f, 1f, 1f, 1f);
    private final Matrix4 oldMatrix = new Matrix4();
    private final GlyphLayout layout = new GlyphLayout();

    private BitmapFont font;
    private String text;

    private float drawX;
    private float drawY;

    public ScalingLabel(BitmapFont font, String text, float x, float y, int alignment) {
        super(x, y, 0f, 0f, alignment);
        set(font, text);
    }

    public ScalingLabel(BitmapFont font, String text, float offsetX, float offsetY, Anchor anchor,
                        int alignment) {
        super(offsetX, offsetY, 0f, 0f, anchor, alignment);
        set(font, text);
    }

    public ScalingLabel(BitmapFont font, String text, float targetX, float targetY,
                        int targetScreenWidth, int targetScreenHeight, int alignment) {
        super(targetX, targetY, 0f, 0f, targetScreenWidth, targetScreenHeight, alignment);
        set(font, text);
    }

    public void draw(SpriteBatch batch) {
        Matrix4 newMatrix = batch.getTransformMatrix();
        oldMatrix.set(newMatrix);

        newMatrix.scale(scale, scale, 1f);
        batch.setTransformMatrix(newMatrix);

        boolean wasInt = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);

        font.draw(batch, layout, drawX, drawY);

        font.setUseIntegerPositions(wasInt);
        batch.setTransformMatrix(oldMatrix);
    }

    public void setFont(BitmapFont font) {
        set(font, text);
    }

    public void setText(String text) {
        set(font, text);
    }

    public void setColor(Color rgb, float a) {
        this.color.set(rgb, a);
        set(font, text);
    }

    public void setColor(Color color) {
        this.color.set(color);
        set(font, text);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        set(font, text);
    }

    public void setColor(int rgba) {
        this.color.set(rgba);
        set(font, text);
    }

    public void set(BitmapFont font, String text, Color color) {
        this.color.set(color);
        set(font, text);
    }

    public void set(BitmapFont font, String text) {
        this.font = font;
        this.text = text;
        forceUpdate();
    }

    public void forceUpdate() {
        layout.setText(font, text, 0, text.length(), color, 0, Align.left, false, null);
        setTargetSize(layout.width, layout.height);
    }

    /** IF EDITED, MUST CALL forceUpdate() */
    public BitmapFont getFont() {
        return font;
    }

    /** IF EDITED, MUST CALL forceUpdate() */
    public String getText() {
        return text;
    }

    /** IF EDITED, MUST CALL forceUpdate() */
    public Color getColor() {
        return color;
    }

    public float getDrawX() {
        return drawX;
    }

    public float getDrawY() {
        return drawY;
    }

    @Override
    protected void resize(float newX, float newY, float newWidth, float newHeight, float newScale) {
        if (font == null) {
            return;
        }

        // Positioned based on top-left, not top-right
        // Division accounts for matrix rescale
        drawX = current.x / newScale;
        drawY = (current.y + newHeight) / newScale;
    }
}
