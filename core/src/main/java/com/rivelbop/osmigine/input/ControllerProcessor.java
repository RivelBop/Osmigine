package com.rivelbop.osmigine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.NullController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.jspecify.annotations.Nullable;

public final class ControllerProcessor implements ControllerListener {
    public static final int PARTY_SIZE = 8;
    public final ObjectMap<String, ControllerState> controllers = new ObjectMap<>(PARTY_SIZE);
    public final Array<Controller> indexedControllers = new Array<>(PARTY_SIZE);

    // Automatically handling just pressed is slower but easier to integrate
    public final boolean handleJustPressed;

    public ControllerProcessor(boolean autoHandleJustPressed) {
        handleJustPressed = autoHandleJustPressed;

        // Prevent early sizing issues
        for (int i = 0; i < PARTY_SIZE; i++) {
            indexedControllers.add(null);
        }
    }

    /** Only call if not auto-handling justPressed! */
    public void postRender() {
        if (handleJustPressed) {
            return;
        }

        for (ControllerState state : controllers.values()) {
            for (InputEvent buttonEvent : state.buttonEvents) {
                buttonEvent.isJustPressed = false;
            }
        }
    }

    public void postTick() {
        for (ControllerState state : controllers.values()) {
            for (InputEvent buttonEvent : state.buttonEvents) {
                buttonEvent.isJustPressedOnTick = false;
            }
        }
    }

    @Override
    public void connected(Controller controller) {
        if (controller == null || controller == NullController.INSTANCE) {
            return;
        }
        controllers.put(controller.getUniqueId(), new ControllerState(controller));

        int index = controller.getPlayerIndex();
        if (index == Controller.PLAYER_IDX_UNSET) { // Controller doesn't have index support
            int emptyIndex = indexedControllers.indexOf(null, true);
            if (emptyIndex != -1) {
                controller.setPlayerIndex(emptyIndex);
                indexedControllers.set(emptyIndex, controller);
            } else {
                indexedControllers.add(controller);
                controller.setPlayerIndex(indexedControllers.size - 1);
            }
            return;
        }

        if (index >= indexedControllers.size) {
            int additionalCapacity = index - indexedControllers.size + 1;
            indexedControllers.ensureCapacity(additionalCapacity);

            for (int i = 0; i < additionalCapacity; i++) {
                indexedControllers.add(null);
            }
        }
        indexedControllers.set(index, controller);
    }

    @Override
    public void disconnected(Controller controller) {
        if (controller == null || controller == NullController.INSTANCE) {
            return;
        }
        controllers.remove(controller.getUniqueId());

        int index = controller.getPlayerIndex();
        if (index > Controller.PLAYER_IDX_UNSET && index < indexedControllers.size) {
            indexedControllers.set(index, null);
        }
        controller.setPlayerIndex(Controller.PLAYER_IDX_UNSET);
    }

    @Override
    public boolean buttonDown(Controller controller, int i) {
        ControllerState state = getControllerState(controller);
        if (state == null) {
            return false;
        }

        int eventIndex = i - controller.getMinButtonIndex();
        if (eventIndex < 0 || eventIndex >= state.buttonEvents.length) {
            return false;
        }

        InputEvent event = state.buttonEvents[eventIndex];
        event.isPressed = true;
        event.isJustPressed = true;
        event.isJustPressedOnTick = true;

        if (handleJustPressed) {
            Gdx.app.postRunnable(() -> event.isJustPressed = false);
        }

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int i) {
        ControllerState state = getControllerState(controller);
        if (state == null) {
            return false;
        }

        int eventIndex = i - controller.getMinButtonIndex();
        if (eventIndex > ControllerMapping.UNDEFINED && eventIndex < state.buttonEvents.length) {
            state.buttonEvents[eventIndex].isPressed = false;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int i, float v) {
        ControllerState state = getControllerState(controller);
        if (state == null) {
            return false;
        }

        if (i > ControllerMapping.UNDEFINED && i < state.axisValues.length) {
            state.axisValues[i] = v;
        }
        return false;
    }

    @Nullable
    public Controller getController(int index) {
        if (index < 0 || index >= indexedControllers.size) {
            return null;
        }
        return indexedControllers.get(index);
    }

    @Nullable
    public ControllerState getControllerState(Controller controller) {
        if (controller == null) {
            return null;
        }
        return controllers.get(controller.getUniqueId());
    }
}
