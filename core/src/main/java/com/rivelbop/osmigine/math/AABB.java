package com.rivelbop.osmigine.math;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public final class AABB {
    private final Vector2 center;
    private float width;
    private float height;

    public AABB(Vector2 centerPos, float width, float height, boolean isReference) {
        this.center = isReference ? centerPos : new Vector2(centerPos);
        this.width = width;
        this.height = height;
    }

    public AABB(Vector2 centerPos, float width, float height) {
        this(centerPos, width, height, true);
    }

    public CollisionResult collides(AABB other) {
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        float otherHalfWidth = other.width / 2f;
        float otherHalfHeight = other.height / 2f;

        float centerX = center.x;
        float centerY = center.y;

        float otherCenterX = other.center.x;
        float otherCenterY = other.center.y;

        float dX = otherCenterX - centerX;
        float dY = otherCenterY - centerY;

        float absDx = Math.abs(dX);
        float absDy = Math.abs(dY);

        float minRangeX = halfWidth + otherHalfWidth;
        float minRangeY = halfHeight + otherHalfHeight;

        if (absDx >= minRangeX || absDy >= minRangeY) {
            // Null result
            return new CollisionResult(this, other);
        }

        int horizontal = dX > 0 ? Align.right : dX < 0 ? Align.left : 0;
        int vertical = dY > 0 ? Align.top : dY < 0 ? Align.bottom : 0;

        float overlapX = minRangeX - absDx;
        float overlapY = minRangeY - absDy;

        if (absDx > absDy) {
            vertical = 0;
            overlapY = 0f;
        } else if (absDy > absDx) {
            horizontal = 0;
            overlapX = 0f;
        }

        int direction = horizontal | vertical;
        return new CollisionResult(this, other, direction, overlapX, overlapY);
    }

    public void translate(float x, float y) {
        center.add(x, y);
    }

    public void translate(Vector2 translation) {
        center.add(translation);
    }

    public void translateX(float x) {
        center.x += x;
    }

    public void translateY(float y) {
        center.y += y;
    }

    public void setCenter(float x, float y) {
        center.set(x, y);
    }

    public void setCenter(Vector2 position) {
        center.set(position);
    }

    public void setSize(float w, float h) {
        width  = w;
        height = h;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float centerX() {
        return center.x;
    }

    public float centerY() {
        return center.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getCenter() {
        return center;
    }

    public static final class CollisionResult {
        public final AABB boxA;
        public final AABB boxB;

        public final int direction;
        public final float overlapX;
        public final float overlapY;

        public CollisionResult(AABB a, AABB b, int dir, float oX, float oY) {
            boxA = a;
            boxB = b;
            direction = dir;
            overlapX = oX;
            overlapY = oY;
        }

        public CollisionResult(AABB a, AABB b) {
            this(a, b, 0, 0f, 0f);
        }
    }
}
