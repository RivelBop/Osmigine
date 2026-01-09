package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public abstract class ScalingElement {
    protected final Rectangle target;
    protected final Rectangle current;

    protected float scale;
    protected float percentX;
    protected float percentY;

    protected float targetScreenWidth;
    protected float targetScreenHeight;

    public ScalingElement(float targetX, float targetY, float targetWidth, float targetHeight,
                          int targetScreenWidth, int targetScreenHeight) {
        target = new Rectangle(targetX, targetY, targetWidth, targetHeight);
        current = new Rectangle(target);

        this.targetScreenWidth = targetScreenWidth;
        this.targetScreenHeight = targetScreenHeight;

        // Get percentage of screen covered
        this.percentX = targetX / targetScreenWidth;
        this.percentY = targetY / targetScreenHeight;
    }

    public final void resize(int screenWidth, int screenHeight, float scale) {
        this.scale = scale;
        current.set(
                percentX * screenWidth,
                percentY * screenHeight,
                target.width * scale,
                target.height * scale);
        resize(current.x, current.y, current.width, current.height, scale);
    }

    /**
     * Custom application for resizing.
     *
     * @param newX      percentX * screenWidth
     * @param newY      percentY * screenHeight
     * @param newWidth  targetWidth * scale
     * @param newHeight targetHeight * scale
     * @param newScale  The new scale to set to (can use new dimensions directly if preferred).
     */
    protected abstract void resize(float newX, float newY, float newWidth, float newHeight,
                                   float newScale);

    public void setTargetX(float x) {
        target.x = x;
        percentX = x / targetScreenWidth;
        current.x = percentX * Gdx.graphics.getWidth();
    }

    public void setTargetY(float y) {
        target.y = y;
        percentY = y / targetScreenHeight;
        current.y = percentY * Gdx.graphics.getHeight();
    }

    public void setTargetWidth(float width) {
        target.width = width;
        current.width = width * scale;
    }

    public void setTargetHeight(float height) {
        target.height = height;
        current.height = height * scale;
    }

    public float getTargetX() {
        return target.x;
    }

    public float getTargetY() {
        return target.y;
    }

    public float getTargetWidth() {
        return target.width;
    }

    public float getTargetHeight() {
        return target.height;
    }

    public float getCurrentX() {
        return current.x;
    }

    public float getCurrentY() {
        return current.y;
    }

    public float getCurrentWidth() {
        return current.width;
    }

    public float getCurrentHeight() {
        return current.height;
    }

    public float getScale() {
        return scale;
    }

    public float getPercentX() {
        return percentX;
    }

    public float getPercentY() {
        return percentY;
    }

    public float getTargetScreenWidth() {
        return targetScreenWidth;
    }

    public float getTargetScreenHeight() {
        return targetScreenHeight;
    }
}
