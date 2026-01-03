package com.rivelbop.osmigine.input;

import com.badlogic.gdx.math.Vector2;

/** To fix issues with precise cursor positions via GLFW. */
public interface CursorProvider {
    Vector2 getPrecisePosition(InputSystem inputSystem);

    final class Default implements CursorProvider {
        @Override
        public Vector2 getPrecisePosition(InputSystem inputSystem) {
            return inputSystem.getCursorPosition(false);
        }
    }
}
