package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class ScalingSystem {
    private final Viewport viewport = new ScreenViewport();

    private float targetWidth;
    private float targetHeight;
    private float targetAspectRatio;

    private float currentAspectRatio;
    private float scale;

    public ScalingSystem(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.targetAspectRatio = (float) targetWidth / targetHeight;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void apply() {
        viewport.apply(true);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        currentAspectRatio = (float) width / height;
        scale = Math.min((float) width / targetWidth, (float) height / targetHeight);
    }

    public Matrix4 combined() {
        return viewport.getCamera().combined;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public float getTargetHeight() {
        return targetHeight;
    }

    public float getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public float getCurrentWidth() {
        return viewport.getScreenWidth();
    }

    public float getCurrentHeight() {
        return viewport.getScreenHeight();
    }

    public float getCurrentAspectRatio() {
        return currentAspectRatio;
    }

    public float getScale() {
        return scale;
    }
}
