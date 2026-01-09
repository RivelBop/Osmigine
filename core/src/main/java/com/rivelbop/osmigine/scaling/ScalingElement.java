package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;

public abstract class ScalingElement {
    protected float targetX;
    protected float targetY;
    protected float targetWidth;
    protected float targetHeight;

    protected float currentX;
    protected float currentY;
    protected float currentWidth;
    protected float currentHeight;

    protected float scale;
    protected float percentX;
    protected float percentY;

    protected float targetScreenWidth;
    protected float targetScreenHeight;

    public ScalingElement(float targetX, float targetY, float targetWidth, float targetHeight,
                          int targetScreenWidth, int targetScreenHeight) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;

        this.targetScreenWidth = targetScreenWidth;
        this.targetScreenHeight = targetScreenHeight;

        // Get percentage of screen covered
        this.percentX = targetX / targetScreenWidth;
        this.percentY = targetY / targetScreenHeight;
    }

    public final void resize(int screenWidth, int screenHeight, float scale) {
        this.scale = scale;

        currentX = percentX * screenWidth;
        currentY = percentY * screenHeight;
        currentWidth = targetWidth * scale;
        currentHeight = targetHeight * scale;

        resize(currentX, currentY, currentWidth, currentHeight, scale);
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
        targetX = x;
        percentX = x / targetScreenWidth;
        currentX = percentX * Gdx.graphics.getWidth();
    }

    public void setTargetY(float y) {
        targetY = y;
        percentY = y / targetScreenHeight;
        currentY = percentY * Gdx.graphics.getHeight();
    }

    public void setTargetWidth(float width) {
        targetWidth = width;
        currentWidth = targetWidth * scale;
    }

    public void setTargetHeight(float height) {
        targetHeight = height;
        currentHeight = targetHeight * scale;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public float getTargetHeight() {
        return targetHeight;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public float getCurrentWidth() {
        return currentWidth;
    }

    public float getCurrentHeight() {
        return currentHeight;
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
