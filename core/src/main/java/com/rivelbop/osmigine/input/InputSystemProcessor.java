package com.rivelbop.osmigine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public final class InputSystemProcessor implements InputProcessor {
    public static final int CURSOR_POINTER_ID = 0;

    public final InputEvent[] keyEvents = new InputEvent[Input.Keys.MAX_KEYCODE + 1];

    // There is no definitive amount of maximum mouse buttons - this array needs to grow
    public final Array<InputEvent.Touch> mouseEvents = new Array<>(Input.Buttons.FORWARD + 1);

    public final InputEvent.Touch[] pointerEvents = new InputEvent.Touch[Gdx.input.getMaxPointers()];

    public final Vector2 scrollAmount = new Vector2();

    // Automatically handling just pressed is slower but easier to integrate
    public final boolean handleJustPressed;

    public InputSystemProcessor(boolean autoHandleJustPressed) {
        // Avoid null
        for (int i = 0; i < keyEvents.length; i++) {
            keyEvents[i] = new InputEvent();
        }
        for (int i = 0; i < Input.Buttons.FORWARD + 1; i++) {
            mouseEvents.add(new InputEvent.Touch());
        }
        for (int i = 0; i < pointerEvents.length; i++) {
            pointerEvents[i] = new InputEvent.Touch();
        }

        handleJustPressed = autoHandleJustPressed;
    }

    /** Only call if not auto-handling justPressed! */
    public void postRender() {
        if (handleJustPressed) {
            return;
        }

        for (InputEvent keyEvent : keyEvents) {
            keyEvent.isJustPressed = false;
        }

        for (InputEvent mouseEvent : mouseEvents) {
            mouseEvent.isJustPressed = false;
        }

        for (InputEvent touchEvent : pointerEvents) {
            touchEvent.isJustPressed = false;
        }

        scrollAmount.setZero();
    }

    public void postTick() {
        for (InputEvent keyEvent : keyEvents) {
            keyEvent.isJustPressedOnTick = false;
        }

        for (InputEvent mouseEvent : mouseEvents) {
            mouseEvent.isJustPressedOnTick = false;
        }

        for (InputEvent touchEvent : pointerEvents) {
            touchEvent.isJustPressedOnTick = false;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        InputEvent keyEvent = keyEvents[keycode];
        keyEvent.isPressed = true;
        keyEvent.isJustPressed = true;
        keyEvent.isJustPressedOnTick = true;

        if (handleJustPressed) {
            Gdx.app.postRunnable(() -> keyEvent.isJustPressed = false);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyEvents[keycode].isPressed = false;
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // Useful for typing as it converts keys pressed into their actual characters
        // Ex: SHIFT + S = 'S'
        //             S = 's'
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Expand the available mouse button IDs (in case of side mouse buttons)
        if (button >= mouseEvents.size) {
            int additionalCapacity = button - mouseEvents.size + 1;
            mouseEvents.ensureCapacity(additionalCapacity);

            for (int i = 0; i < additionalCapacity; i++) {
                mouseEvents.add(new InputEvent.Touch());
            }
        }

        InputEvent.Touch mouseEvent = mouseEvents.get(button);
        mouseEvent.position.set(screenX, screenY);
        mouseEvent.isPressed = true;
        mouseEvent.isJustPressed = true;
        mouseEvent.isJustPressedOnTick = true;

        InputEvent.Touch pointerEvent = pointerEvents[pointer];
        pointerEvent.position.set(screenX, screenY);
        pointerEvent.isPressed = true;
        pointerEvent.isJustPressed = true;
        pointerEvent.isJustPressedOnTick = true;

        if (handleJustPressed) {
            Gdx.app.postRunnable(() -> {
                mouseEvent.isJustPressed = false;
                pointerEvent.isJustPressed = false;
            });
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Expand the available mouse button IDs (in case of side mouse buttons)
        if (button >= mouseEvents.size) {
            int additionalCapacity = button - mouseEvents.size + 1;
            mouseEvents.ensureCapacity(additionalCapacity);
            for (int i = 0; i < additionalCapacity; i++) {
                mouseEvents.add(new InputEvent.Touch());
            }
        }

        InputEvent.Touch mouseEvent = mouseEvents.get(button);
        mouseEvent.position.set(screenX, screenY);
        mouseEvent.isPressed = false;

        InputEvent.Touch pointerEvent = pointerEvents[pointer];
        pointerEvent.position.set(screenX, screenY);
        pointerEvent.isPressed = false;

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        touchUp(screenX, screenY, pointer, button);

        InputEvent.Touch event = mouseEvents.get(button);
        event.isJustPressed = false;
        event.isJustPressedOnTick = false;

        event = pointerEvents[pointer];
        event.isJustPressed = false;
        event.isJustPressedOnTick = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        InputEvent.Touch pointerEvent = pointerEvents[pointer];
        pointerEvent.position.set(screenX, screenY);
        pointerEvent.isPressed = true;

        // For non-touchscreen devices, we only need the primary cursor
        if (pointer != CURSOR_POINTER_ID) {
            return false;
        }

        // Any mouse button can be held down to be considered a touch dragged
        for (InputEvent.Touch mouseEvent : mouseEvents) {
            if (mouseEvent.isPressed) {
                mouseEvent.position.set(screenX, screenY);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        pointerEvents[CURSOR_POINTER_ID].position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollAmount.set(amountX, amountY);

        if (handleJustPressed) {
            Gdx.app.postRunnable(scrollAmount::setZero);
        }

        return false;
    }
}
