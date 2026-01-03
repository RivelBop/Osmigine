package com.rivelbop.osmigine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Disposable;
import org.jspecify.annotations.Nullable;

public final class ControllerSystem implements Disposable {
    private final ControllerProcessor controllerProcessor = new ControllerProcessor(false);
    private final InputEvent missingInputEvent = new InputEvent();

    public ControllerSystem(boolean addToAllControllers) {
        if (addToAllControllers) {
            Controllers.addListener(controllerProcessor);

            for (Controller c : Controllers.getControllers()) {
                controllerProcessor.connected(c);
            }
        }
    }

    /** DO NOT CALL IF ADDED ALL CONTROLLERS GLOBALLY! */
    public void addToController(Controller controller) {
        if (controller != null) {
            controllerProcessor.connected(controller);
            controller.addListener(controllerProcessor);
        }
    }

    public void removeFromController(Controller controller) {
        if (controller != null) {
            controllerProcessor.disconnected(controller);
            controller.removeListener(controllerProcessor);
        }
    }

    /** MUST BE CALLED! */
    public void postRender() {
        controllerProcessor.postRender();
    }

    /** MUST BE CALLED (IF USING TICKS)! */
    public void postTick() {
        controllerProcessor.postTick();
    }

    public boolean isPressed(Mapping button) {
        return getButtonEvent(button).isPressed;
    }

    public boolean isPressed(int index, Mapping button) {
        return getButtonEvent(index, button).isPressed;
    }

    public boolean isPressed(Controller controller, Mapping button) {
        return getButtonEvent(controller, button).isPressed;
    }

    public boolean isPressed(int button) {
        return getButtonEvent(button).isPressed;
    }

    public boolean isPressed(int index, int button) {
        return getButtonEvent(index, button).isPressed;
    }

    public boolean isPressed(Controller controller, int button) {
        return getButtonEvent(controller, button).isPressed;
    }

    public boolean isJustPressed(Mapping button) {
        return getButtonEvent(button).isJustPressed;
    }

    public boolean isJustPressed(int index, Mapping button) {
        return getButtonEvent(index, button).isJustPressed;
    }

    public boolean isJustPressed(Controller controller, Mapping button) {
        return getButtonEvent(controller, button).isJustPressed;
    }

    public boolean isJustPressed(int button) {
        return getButtonEvent(button).isJustPressed;
    }

    public boolean isJustPressed(int index, int button) {
        return getButtonEvent(index, button).isJustPressed;
    }

    public boolean isJustPressed(Controller controller, int button) {
        return getButtonEvent(controller, button).isJustPressed;
    }

    public boolean isJustPressedOnTick(Mapping button) {
        return getButtonEvent(button).isJustPressedOnTick;
    }

    public boolean isJustPressedOnTick(int index, Mapping button) {
        return getButtonEvent(index, button).isJustPressedOnTick;
    }

    public boolean isJustPressedOnTick(Controller controller, Mapping button) {
        return getButtonEvent(controller, button).isJustPressedOnTick;
    }

    public boolean isJustPressedOnTick(int button) {
        return getButtonEvent(button).isJustPressedOnTick;
    }

    public boolean isJustPressedOnTick(int index, int button) {
        return getButtonEvent(index, button).isJustPressedOnTick;
    }

    public boolean isJustPressedOnTick(Controller controller, int button) {
        return getButtonEvent(controller, button).isJustPressedOnTick;
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(Mapping button) {
        return getButtonEvent(Controllers.getCurrent(), button);
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(int index, Mapping button) {
        return getButtonEvent(getController(index), button);
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(Controller controller, Mapping button) {
        if (button == null) {
            return missingInputEvent;
        }
        return getButtonEvent(controller, button.map(controller));
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(int button) {
        return getButtonEvent(Controllers.getCurrent(), button);
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(int index, int button) {
        return getButtonEvent(getController(index), button);
    }

    /** @return READ-ONLY */
    public InputEvent getButtonEvent(Controller controller, int button) {
        if (controller == null || button < controller.getMinButtonIndex()
                || button > controller.getMaxButtonIndex()) {
            // All false, it won't trigger anything
            return missingInputEvent;
        }

        ControllerState state = controllerProcessor.getControllerState(controller);
        if (state == null) {
            // All false, it won't trigger anything
            return missingInputEvent;
        }

        return state.buttonEvents[button - controller.getMinButtonIndex()];
    }

    public float getAxisValue(Mapping axis) {
        return getAxisValue(Controllers.getCurrent(), axis);
    }

    public float getAxisValue(int index, Mapping axis) {
        return getAxisValue(getController(index), axis);
    }

    public float getAxisValue(Controller controller, Mapping axis) {
        return getAxisValue(controller, axis.map(controller));
    }

    public float getAxisValue(int axis) {
        return getAxisValue(Controllers.getCurrent(), axis);
    }

    public float getAxisValue(int index, int axis) {
        return getAxisValue(getController(index), axis);
    }

    public float getAxisValue(Controller controller, int axis) {
        if (controller == null || axis < 0 || axis >= controller.getAxisCount()) {
            return 0f;
        }

        ControllerState state = controllerProcessor.getControllerState(controller);
        if (state == null) {
            return 0f;
        }

        return state.axisValues[axis];
    }

    @Nullable
    public Controller getController(int index) {
        return controllerProcessor.getController(index);
    }

    public ControllerProcessor getControllerProcessor() {
        return controllerProcessor;
    }

    @Override
    public void dispose() {
        for (Controller c : Controllers.getControllers()) {
            removeFromController(c);
        }
        Controllers.removeListener(controllerProcessor);
    }
}
