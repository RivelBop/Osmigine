package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public abstract class ScalingElement {
    protected final Rectangle target;
    protected final Rectangle current;

    protected int alignment;

    protected float scale;
    protected float percentX;
    protected float percentY;

    protected int lastScreenWidth;
    protected int lastScreenHeight;

    protected float targetScreenWidth;
    protected float targetScreenHeight;

    public ScalingElement(float targetX, float targetY, float targetWidth, float targetHeight,
                          int targetScreenWidth, int targetScreenHeight) {
        this(targetX, targetY, targetWidth, targetHeight, targetScreenWidth, targetScreenHeight,
                Align.bottomLeft);
    }

    public ScalingElement(float targetX, float targetY, float targetWidth, float targetHeight,
                          int targetScreenWidth, int targetScreenHeight, int alignment) {
        this.target = new Rectangle(targetX, targetY, targetWidth, targetHeight);
        this.current = new Rectangle();

        this.alignment = alignment;

        this.percentX = targetX / targetScreenWidth;
        this.percentY = targetY / targetScreenHeight;

        this.lastScreenWidth = Gdx.graphics.getWidth();
        this.lastScreenHeight = Gdx.graphics.getHeight();

        this.targetScreenWidth = targetScreenWidth;
        this.targetScreenHeight = targetScreenHeight;

        resize(lastScreenWidth, lastScreenHeight);
    }

    /** Called if without ScalingSystem */
    public final void resize(int screenWidth, int screenHeight) {
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;
        scale = Math.min((float) screenWidth / targetScreenWidth,
                (float) screenHeight / targetScreenHeight);

        updateCurrent(screenWidth, screenHeight);
    }

    /** Should be called by the ScalingSystem */
    public final void resize(int screenWidth, int screenHeight, float scale) {
        this.lastScreenWidth = screenWidth;
        this.lastScreenHeight = screenHeight;
        this.scale = scale;

        updateCurrent(screenWidth, screenHeight);
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

    protected final void updateCurrent(int screenWidth, int screenHeight) {
        current.set(percentX * screenWidth, percentY * screenHeight, target.width * scale,
                target.height * scale);

        if (Align.isCenterHorizontal(alignment)) {
            current.x -= current.width / 2f;
        } else if (Align.isRight(alignment)) {
            current.x -= current.width;
        }

        if (Align.isCenterVertical(alignment)) {
            current.y -= current.height / 2f;
        } else if (Align.isTop(alignment)) {
            current.y -= current.height;
        }

        resize(current.x, current.y, current.width, current.height, scale);
    }

    public void setTargetPosition(Vector2 position) {
        if (position != null) {
            setTargetX(position.x);
            setTargetY(position.y);
        }
    }

    public void setTargetPosition(float x, float y) {
        target.x = x;
        percentX = x / targetScreenWidth;

        target.y = y;
        percentY = y / targetScreenHeight;

        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetX(float x) {
        target.x = x;
        percentX = x / targetScreenWidth;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetY(float y) {
        target.y = y;
        percentY = y / targetScreenHeight;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetSize(float width, float height) {
        target.width = width;
        current.width = width * scale;

        target.height = height;
        current.height = height * scale;

        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetWidth(float width) {
        target.width = width;
        current.width = width * scale;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetHeight(float height) {
        target.height = height;
        current.height = height * scale;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetScreenSize(float width, float height) {
        targetScreenWidth = width;
        percentX = target.x / width;

        targetScreenHeight = height;
        percentY = target.y / height;

        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetScreenWidth(float width) {
        targetScreenWidth = width;
        percentX = target.x / width;
        updateCurrent(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetScreenHeight(float height) {
        targetScreenHeight = height;
        percentY = target.y / height;
        updateCurrent(lastScreenWidth, lastScreenHeight);
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
