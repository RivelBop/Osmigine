package com.rivelbop.osmigine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class InputSystem implements Disposable {
    private final InputSystemProcessor inputProcessor = new InputSystemProcessor(false);
    private final CursorProvider cursorProvider;

    // Invalid Key Handling
    private final InputEvent invalidKeyEvent = new InputEvent();

    // Missing Mouse Handling
    private final InputEvent.Touch missingMouseButton = new InputEvent.Touch();

    // Garbage-Collection Performance
    private final Vector3 cursorWorldVec3 = new Vector3();
    private final Vector2 cursorWorldVec2 = new Vector2();
    private final Vector3 preciseCursorWorldVec3 = new Vector3();
    private final Vector2 preciseCursorWorldVec2 = new Vector2();
    private final Vector3 touchWorldVec3 = new Vector3();
    private final Vector2 touchWorldVec2 = new Vector2();

    public InputSystem(CursorProvider cursorProvider, boolean setAsGlobalInputProcessor) {
        this.cursorProvider = cursorProvider;
        if (setAsGlobalInputProcessor) {
            InputProcessor globalInput = Gdx.input.getInputProcessor();
            if (globalInput instanceof InputMultiplexer) {
                ((InputMultiplexer) globalInput).addProcessor(inputProcessor);
            } else {
                Gdx.input.setInputProcessor(inputProcessor);
            }
        }
    }

    /** MUST BE CALLED! */
    public void postRender() {
        inputProcessor.postRender();
    }

    /** MUST BE CALLED (IF USING TICKS)! */
    public void postTick() {
        inputProcessor.postTick();
    }

    /** @return READ-ONLY */
    public InputEvent getKeyEvent(int keycode) {
        if (keycode < 0 || keycode >= inputProcessor.keyEvents.length) {
            return invalidKeyEvent;
        }
        return inputProcessor.keyEvents[keycode];
    }

    public boolean isKeyPressed(int keycode) {
        return getKeyEvent(keycode).isPressed;
    }

    public boolean isKeyJustPressed(int keycode) {
        return getKeyEvent(keycode).isJustPressed;
    }

    public boolean isKeyJustPressedOnTick(int keycode) {
        return getKeyEvent(keycode).isJustPressedOnTick;
    }

    /** @return READ-ONLY */
    public InputEvent.Touch getMouseEvent(int button) {
        if (button >= inputProcessor.mouseEvents.size) {
            return missingMouseButton;
        }
        return inputProcessor.mouseEvents.get(button);
    }

    public boolean isMousePressed(int button) {
        return getMouseEvent(button).isPressed;
    }

    public boolean isMouseJustPressed(int button) {
        return getMouseEvent(button).isJustPressed;
    }

    public boolean isMouseJustPressedOnTick(int button) {
        return getMouseEvent(button).isJustPressedOnTick;
    }

    public Vector2 getMousePressedPosition(int button, boolean copy) {
        Vector2 pos = getMouseEvent(button).position;
        return copy ? pos.cpy() : pos;
    }

    /** @return READ-ONLY */
    public InputEvent.Touch getTouchEvent(int pointer) {
        return inputProcessor.pointerEvents[pointer];
    }

    /** @return READ-ONLY */
    public InputEvent.Touch getPointerEvent(int pointer) {
        return getTouchEvent(pointer);
    }

    public boolean isTouched(int pointer) {
        return getTouchEvent(pointer).isPressed;
    }

    public boolean isJustTouched(int pointer) {
        return getTouchEvent(pointer).isJustPressed;
    }

    public boolean isJustTouchedOnTick(int pointer) {
        return getTouchEvent(pointer).isJustPressedOnTick;
    }

    public Vector2 getTouchPosition(int pointer, boolean copy) {
        Vector2 pos = getTouchEvent(pointer).position;
        return copy ? pos.cpy() : pos;
    }

    public Vector2 getPointerPosition(int pointer, boolean copy) {
        return getTouchPosition(pointer, copy);
    }

    public Vector2 getCursorPosition(boolean copy) {
        Vector2 pos = getPointerEvent(InputSystemProcessor.CURSOR_POINTER_ID).position;
        return copy ? pos.cpy() : pos;
    }

    public Vector2 getPreciseCursorPosition(boolean copy) {
        Vector2 precisePosition = cursorProvider.getPrecisePosition(this);
        return copy ? precisePosition.cpy() : precisePosition;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getCursorWorldPosition(Viewport viewport, boolean copy) {
        cursorWorldVec2.set(getCursorPosition(false));
        viewport.unproject(cursorWorldVec2);
        return copy ? cursorWorldVec2.cpy() : cursorWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getCursorWorldPosition(Camera camera, boolean copy) {
        cursorWorldVec3.set(getCursorPosition(false), 0f);
        camera.unproject(cursorWorldVec3);
        cursorWorldVec2.set(cursorWorldVec3.x, cursorWorldVec3.y);
        return copy ? cursorWorldVec2.cpy() : cursorWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getPreciseCursorWorldPosition(Viewport viewport, boolean copy) {
        preciseCursorWorldVec2.set(getPreciseCursorPosition(false));
        viewport.unproject(preciseCursorWorldVec2);
        return copy ? preciseCursorWorldVec2.cpy() : preciseCursorWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getPreciseCursorWorldPosition(Camera camera, boolean copy) {
        preciseCursorWorldVec3.set(getPreciseCursorPosition(false), 0f);
        camera.unproject(preciseCursorWorldVec3);
        preciseCursorWorldVec2.set(preciseCursorWorldVec3.x, preciseCursorWorldVec3.y);
        return copy ? preciseCursorWorldVec2.cpy() : preciseCursorWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getTouchWorldPosition(Viewport viewport, int pointer, boolean copy) {
        touchWorldVec2.set(getTouchPosition(pointer, false));
        viewport.unproject(touchWorldVec2);
        return copy ? touchWorldVec2.cpy() : touchWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getTouchWorldPosition(Camera camera, int pointer, boolean copy) {
        touchWorldVec3.set(getTouchPosition(pointer, false), 0f);
        camera.unproject(touchWorldVec3);

        touchWorldVec2.set(touchWorldVec3.x, touchWorldVec3.y);
        return copy ? touchWorldVec2.cpy() : touchWorldVec2;
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getPointerWorldPosition(Viewport viewport, int pointer, boolean copy) {
        return getTouchWorldPosition(viewport, pointer, copy);
    }

    /** VALUE ONLY VALID UNTIL THE NEXT CALL TO THIS METHOD! */
    public Vector2 getPointerWorldPosition(Camera camera, int pointer, boolean copy) {
        return getTouchWorldPosition(camera, pointer, copy);
    }

    public Vector2 getScrollAmount(boolean copy) {
        Vector2 scroll = inputProcessor.scrollAmount;
        return copy ? scroll.cpy() : scroll;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void dispose() {
        InputProcessor globalInput = Gdx.input.getInputProcessor();
        if (globalInput == inputProcessor) {
            Gdx.input.setInputProcessor(null);
        } else if (globalInput instanceof InputMultiplexer) {
            ((InputMultiplexer) globalInput).removeProcessor(inputProcessor);
        }
    }
}
