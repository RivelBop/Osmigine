package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.jspecify.annotations.Nullable;

public abstract class ScalingElement {
    protected final Rectangle target;
    protected final Rectangle current;

    @Nullable
    protected Anchor anchor;
    protected int alignment;

    protected float scale;
    protected float percentX;
    protected float percentY;

    protected int lastScreenWidth;
    protected int lastScreenHeight;

    protected int targetScreenWidth;
    protected int targetScreenHeight;

    int anchorIndex = -1;

    /** CALL IF ANCHOR ASSIGNED LATER OR VALUES NOT OBTAINABLE - IDEAL FOR SCALING_SYSTEM */
    public ScalingElement(float x, float y, float targetWidth, float targetHeight, int alignment) {
        this(x, y, targetWidth, targetHeight, null, alignment);
    }

    public ScalingElement(float offsetX, float offsetY, float targetWidth, float targetHeight,
                          Anchor anchor, int alignment) {
        this.target = new Rectangle(offsetX, offsetY, targetWidth, targetHeight);
        this.current = new Rectangle();

        this.alignment = alignment;

        this.lastScreenWidth = Gdx.graphics.getWidth();
        this.lastScreenHeight = Gdx.graphics.getHeight();

        setAnchor(anchor);
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
        current.setSize(target.width * scale, target.height * scale);
        if (anchor == null) {
            current.setPosition(percentX * screenWidth, percentY * screenHeight);
        } else {
            current.setPosition(anchor.current.x + target.x * scale,
                    anchor.current.y + target.y * scale);
        }

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
            setTargetPosition(position.x, position.y);
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

    public void setAnchor(Anchor anchor) {
        if (this.anchor == anchor || anchor == this) {
            return;
        }

        if (this.anchor != null) {
            this.anchor.remove(this);
        }

        this.anchor = anchor;
        if (anchor != null) {
            scale = anchor.scale;
            setTargetScreenSize(anchor.targetScreenWidth, anchor.targetScreenHeight);
            anchor.add(this);
            return;
        }
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

    public void setTargetScreenSize(int width, int height) {
        // Can be simplified to not calculate percent if on anchor but kept in case removed from one
        targetScreenWidth = width;
        percentX = target.x / width;

        targetScreenHeight = height;
        percentY = target.y / height;

        resize(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetScreenWidth(int width) {
        targetScreenWidth = width;
        percentX = target.x / width;
        resize(lastScreenWidth, lastScreenHeight);
    }

    public void setTargetScreenHeight(int height) {
        targetScreenHeight = height;
        percentY = target.y / height;
        resize(lastScreenWidth, lastScreenHeight);
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

    public int getTargetScreenWidth() {
        return targetScreenWidth;
    }

    public int getTargetScreenHeight() {
        return targetScreenHeight;
    }

    @Nullable
    public Anchor getAnchor() {
        return anchor;
    }

    public static final class Anchor extends ScalingElement {
        // Half default capacity
        private final Array<ScalingElement> connectedElements = new Array<>(false, 8);

        public Anchor(int alignment, int targetScreenWidth, int targetScreenHeight) {
            super(
                    Align.isLeft(alignment) ? 0f :
                            Align.isCenterHorizontal(alignment) ? targetScreenWidth / 2f :
                                    Align.isRight(alignment) ? targetScreenWidth : 0f,
                    Align.isBottom(alignment) ? 0f :
                            Align.isCenterVertical(alignment) ? targetScreenHeight / 2f :
                                    Align.isTop(alignment) ? targetScreenHeight : 0f,
                    0f, 0f, targetScreenWidth, targetScreenHeight, alignment);
        }

        @Override
        protected void resize(float newX, float newY, float newWidth, float newHeight,
                              float newScale) {
            if (connectedElements == null) {
                return;
            }

            for (ScalingElement e : connectedElements) {
                e.resize(lastScreenWidth, lastScreenHeight, newScale);
            }
        }

        /** Same as calling ScalingElement#setAnchor() */
        public void addElement(ScalingElement element) {
            element.setAnchor(this);
        }

        /** Same as calling ScalingElement#setAnchor(null) */
        public void removeElement(ScalingElement element) {
            element.setAnchor(null);
        }

        /** To be called by ScalingElement setAnchor */
        void add(ScalingElement element) {
            element.anchorIndex = connectedElements.size;
            connectedElements.add(element);
        }

        /** To be called by ScalingElement setAnchor */
        void remove(ScalingElement element) {
            if (element.anchor == this) {
                connectedElements.get(connectedElements.size - 1).anchorIndex = element.anchorIndex;
                connectedElements.removeIndex(element.anchorIndex);
                element.anchorIndex = -1;
            }
        }
    }
}
