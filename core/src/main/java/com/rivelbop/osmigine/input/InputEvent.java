package com.rivelbop.osmigine.input;

import com.badlogic.gdx.math.Vector2;

public class InputEvent {
    public boolean isPressed = false;
    public boolean isJustPressed = false;
    public boolean isJustPressedOnTick = false;

    public static final class Touch extends InputEvent {
        public final Vector2 position = new Vector2();
    }
}
