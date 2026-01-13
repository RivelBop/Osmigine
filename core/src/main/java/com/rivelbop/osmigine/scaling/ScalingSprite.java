package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class ScalingSprite extends ScalingElement {
    private final Sprite sprite;

    public ScalingSprite(Texture texture, float x, float y, int alignment) {
        super(x, y, texture.getWidth(), texture.getHeight(), alignment);
        sprite = new Sprite(texture);
        resize(current.x, current.y, current.width, current.height, scale);
    }

    public ScalingSprite(Texture texture, float offsetX, float offsetY, Anchor anchor,
                         int alignment) {
        super(offsetX, offsetY, texture.getWidth(), texture.getHeight(), anchor, alignment);
        sprite = new Sprite(texture);
        resize(current.x, current.y, current.width, current.height, scale);
    }

    public ScalingSprite(Texture texture, float targetX, float targetY, int targetScreenWidth,
                         int targetScreenHeight, int alignment) {
        super(targetX, targetY, texture.getWidth(), texture.getHeight(), targetScreenWidth,
                targetScreenHeight, alignment);
        sprite = new Sprite(texture);
        resize(current.x, current.y, current.width, current.height, scale);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
        setTargetSize(texture.getWidth(), texture.getHeight());
    }

    /** POSITIONING AND SIZE SHOULD NOT BE MESSED WITH */
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    protected void resize(float newX, float newY, float newWidth, float newHeight, float newScale) {
        if (sprite == null) {
            return;
        }
        sprite.setBounds(newX, newY, newWidth, newHeight);
    }
}
