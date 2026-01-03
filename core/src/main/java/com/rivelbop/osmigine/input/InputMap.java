package com.rivelbop.osmigine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.NullController;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Objects;

public final class InputMap<K> {
    private final ObjectMap<K, InputTrigger> inputMap = new ObjectMap<>();
    private final InputSystem inputSystem;
    private final ControllerSystem controllerSystem;

    public InputMap(InputSystem inputSystem, ControllerSystem controllerSystem) {
        this.inputSystem = inputSystem;
        this.controllerSystem = controllerSystem;
    }

    public void put(K key, InputTrigger value) {
        inputMap.put(key, value);
    }

    public void put(ObjectMap<? extends K, ? extends InputTrigger> map) {
        inputMap.putAll(map);
    }

    public void remapKey(K key, int keycode) {
        InputTrigger trigger = inputMap.get(key);
        if (trigger != null) {
            trigger.key = keycode;
        }
    }

    public void remapMouse(K key, int mouse) {
        InputTrigger trigger = inputMap.get(key);
        if (trigger != null) {
            trigger.mouse = mouse;
        }
    }

    public void remapButton(K key, Mapping button) {
        remapButton(key, button, Controllers.getCurrent());
    }

    public void remapButton(K key, Mapping button, int index) {
        remapButton(key, button, controllerSystem.getController(index));
    }

    public void remapButton(K key, Mapping button, Controller controller) {
        if (button != null) {
            remapButton(key, button.map(controller));
        }
    }

    public void remapButton(K key, int button) {
        InputTrigger trigger = inputMap.get(key);
        if (trigger != null) {
            trigger.button = button;
        }
    }

    public void remapAxis(K key, Mapping axis) {
        remapAxis(key, axis, Controllers.getCurrent());
    }

    public void remapAxis(K key, Mapping axis, int index) {
        remapAxis(key, axis, controllerSystem.getController(index));
    }

    public void remapAxis(K key, Mapping axis, Controller controller) {
        if (axis != null) {
            remapAxis(key, axis.map(controller));
        }
    }

    public void remapAxis(K key, int axis) {
        InputTrigger trigger = inputMap.get(key);
        if (trigger != null) {
            trigger.axis = axis;
        }
    }

    public boolean get(K key) {
        return get(key, Controllers.getCurrent());
    }

    public boolean get(K key, int controllerIndex) {
        return get(key, controllerSystem.getController(controllerIndex));
    }

    public boolean get(K key, Controller controller) {
        InputTrigger trigger = inputMap.get(key);
        if (trigger != null) {
            // There should be no null controllers passed into the isActive() in case the
            // user wants to use the controller outside the controller system
            return trigger.isActive(inputSystem, controllerSystem,
                    Objects.requireNonNullElse(controller, NullController.INSTANCE));
        }
        return false;
    }

    public ObjectMap<K, InputTrigger> getInputMap() {
        return inputMap;
    }

    public InputSystem getInputSystem() {
        return inputSystem;
    }

    public ControllerSystem getControllerSystem() {
        return controllerSystem;
    }
}
