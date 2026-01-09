package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class ScalingSystem {
    private final Viewport viewport = new ScreenViewport();

    private int lastWidth;
    private int lastHeight;

    private int targetWidth;
    private int targetHeight;
    private float targetAspectRatio;

    private float currentAspectRatio;
    private float scale;

    public ScalingSystem(int targetWidth, int targetHeight) {
        lastWidth = Gdx.graphics.getWidth();
        lastHeight = Gdx.graphics.getHeight();
        setTargetSize(targetWidth, targetHeight);
    }

    public void resize(int width, int height) {
        lastWidth = width;
        lastHeight = height;

        viewport.update(width, height, true);
        currentAspectRatio = (float) width / height;
        scale = Math.min((float) width / targetWidth, (float) height / targetHeight);
    }

    public void apply() {
        viewport.apply(true);
    }

    public void setTargetSize(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.targetAspectRatio = (float) targetWidth / targetHeight;
        resize(lastWidth, lastHeight);
    }

    public Matrix4 combined() {
        return viewport.getCamera().combined;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public float getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public int getCurrentWidth() {
        return viewport.getScreenWidth();
    }

    public int getCurrentHeight() {
        return viewport.getScreenHeight();
    }

    public float getCurrentAspectRatio() {
        return currentAspectRatio;
    }

    public float getScale() {
        return scale;
    }
}
