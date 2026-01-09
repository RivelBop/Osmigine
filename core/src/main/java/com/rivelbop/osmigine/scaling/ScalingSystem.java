package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.rivelbop.osmigine.scaling.ScalingElement.Anchor;

public final class ScalingSystem {
    public final Anchor bottomLeft;
    public final Anchor bottom;
    public final Anchor bottomRight;
    public final Anchor left;
    public final Anchor center;
    public final Anchor right;
    public final Anchor topLeft;
    public final Anchor top;
    public final Anchor topRight;

    private final Anchor[] anchors = new Anchor[9];
    private final Viewport viewport = new ScreenViewport();

    private int lastWidth;
    private int lastHeight;

    private int targetWidth;
    private int targetHeight;
    private float targetAspectRatio;

    private float currentAspectRatio;
    private float scale;

    public ScalingSystem(int targetWidth, int targetHeight) {
        anchors[0] = bottomLeft  = new Anchor(Align.bottomLeft, targetWidth, targetHeight);
        anchors[1] = bottom      = new Anchor(Align.bottom, targetWidth, targetHeight);
        anchors[2] = bottomRight = new Anchor(Align.bottomRight, targetWidth, targetHeight);
        anchors[3] = left        = new Anchor(Align.left, targetWidth, targetHeight);
        anchors[4] = center      = new Anchor(Align.center, targetWidth, targetHeight);
        anchors[5] = right       = new Anchor(Align.right, targetWidth, targetHeight);
        anchors[6] = topLeft     = new Anchor(Align.topLeft, targetWidth, targetHeight);
        anchors[7] = top         = new Anchor(Align.top, targetWidth, targetHeight);
        anchors[8] = topRight    = new Anchor(Align.topRight, targetWidth, targetHeight);

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

        for (Anchor anchor : anchors) {
            anchor.resize(width, height, scale);
        }
    }

    public void apply() {
        viewport.apply(true);
    }

    public void setTargetSize(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.targetAspectRatio = (float) targetWidth / targetHeight;

        for (Anchor anchor : anchors) {
            // Don't call setTargetScreenSize() - Avoids recalculating twice
            anchor.targetScreenWidth = targetWidth;
            anchor.percentX = anchor.target.x / targetWidth;
            anchor.targetScreenHeight = targetHeight;
            anchor.percentY = anchor.target.y / targetHeight;
        }
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
