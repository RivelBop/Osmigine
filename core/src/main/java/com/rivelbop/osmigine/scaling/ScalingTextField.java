package com.rivelbop.osmigine.scaling;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class ScalingTextField extends ScalingLabel implements InputProcessor {
    public static final int IPV4_LENGTH = 21; // [IP=15] + [:=1] + [PORT=5] = 21

    private final StringBuilder text;
    private Filter filter;

    // Performance critical, will only be used when the text is set directly
    // Set to false when appending/removing characters
    private boolean setStringBuilderDirectly = true;
    private boolean isActive = false;
    private boolean isDisabled = false;

    public ScalingTextField(BitmapFont font, String text, float offsetX, float offsetY,
                            ScalingElement.Anchor anchor, int alignment) {
        this(font, text, null, offsetX, offsetY, anchor, alignment);
    }

    public ScalingTextField(BitmapFont font, String text, Filter filter,
                            float offsetX, float offsetY,
                            ScalingElement.Anchor anchor, int alignment) {
        super(font, text, offsetX, offsetY, anchor, alignment);
        this.text = new StringBuilder(text);
        setFilter(filter);
    }

    public void update(Vector2 cursor, boolean pressed) {
        if (isDisabled) {
            return;
        }

        if (pressed && current.contains(cursor)) {
            isActive = true;
        } else if (pressed) {
            isActive = false;
        }
    }

    public void update(Rectangle cursor, boolean pressed) {
        if (isDisabled) {
            return;
        }

        if (pressed && current.overlaps(cursor)) {
            isActive = true;
        } else if (pressed) {
            isActive = false;
        }
    }

    @Override
    public void set(BitmapFont font, String text) {
        if (setStringBuilderDirectly) {
            this.text.delete(0, this.text.length());
            this.text.append(text);
        }
        super.set(font, text);
    }

    public void setFilter(Filter filter) {
        this.filter = (filter != null) ? filter : Filter.DEFAULT;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        if (isDisabled) {
            isActive = false;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (isDisabled || !isActive || character == 0) {
            return false;
        }

        setStringBuilderDirectly = false;
        if (character == '\b' && text.length() > 0) {
            text.deleteCharAt(text.length() - 1);
            setText(text.toString());
            setStringBuilderDirectly = true;
            return true;
        }

        if (!filter.filter(text.toString(), character)) {
            return false;
        }

        text.append(character);
        setText(text.toString());
        setStringBuilderDirectly = true;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    @FunctionalInterface
    public interface Filter {
        Filter DEFAULT = (text, character) -> true;
        Filter NO_NEW_LINE = (text, character) -> character != '\n';
        Filter IPV4 = (text, character) ->
            (Character.isDigit(character) || character == '.' || character == ':') &&
                text.length() < IPV4_LENGTH;

        /** Set the rules to allow a certain action based on the current text and character. */
        boolean filter(String text, char character);
    }
}
