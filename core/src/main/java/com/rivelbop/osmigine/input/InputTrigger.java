package com.rivelbop.osmigine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public abstract class InputTrigger {
    public static final int UNDEFINED = -1;

    // Desktop
    public int key = UNDEFINED;
    public int mouse = UNDEFINED;

    // Controller
    public int button = UNDEFINED;
    public int axis = UNDEFINED;

    public InputTrigger() {
        // Intentionally empty
    }

    public InputTrigger(int key, int mouse, Mapping button, Mapping axis) {
        this(key, mouse, button, axis, Controllers.getCurrent());
    }

    public InputTrigger(int key, int mouse, Mapping button, Mapping axis,
                        ControllerSystem controllerSystem, int index) {
        Controller controller = controllerSystem != null ?
                controllerSystem.getController(index) : null;

        set(key, mouse, button != null ? button.map(controller) : UNDEFINED,
                axis != null ? axis.map(controller) : UNDEFINED);
    }

    public InputTrigger(int key, int mouse, Mapping button, Mapping axis, Controller controller) {
        set(key, mouse, button != null ? button.map(controller) : UNDEFINED,
                axis != null ? axis.map(controller) : UNDEFINED);
    }

    public InputTrigger(int key, int mouse, int button, int axis) {
        set(key, mouse, button, axis);
    }

    public void set(int key, int mouse, int button, int axis) {
        this.key = key;
        this.mouse = mouse;
        this.button = button;
        this.axis = axis;
    }

    public InputTrigger key(int key) {
        this.key = key;
        return this;
    }

    public InputTrigger mouse(int mouse) {
        this.mouse = mouse;
        return this;
    }

    public InputTrigger controller(Mapping button, Mapping axis) {
        return controller(button, axis, Controllers.getCurrent());
    }

    public InputTrigger controller(Mapping button, Mapping axis,
                                   ControllerSystem controllerSystem, int index) {
        Controller controller = controllerSystem != null ?
                controllerSystem.getController(index) : null;
        return controller(button, axis, controller);
    }

    public InputTrigger controller(Mapping button, Mapping axis, Controller controller) {
        this.button = (button != null ? button.map(controller) : UNDEFINED);
        this.axis = (axis != null ? axis.map(controller) : UNDEFINED);
        return this;
    }

    public InputTrigger controller(int button, int axis) {
        this.button = button;
        this.axis = axis;
        return this;
    }

    public InputTrigger button(Mapping button) {
        return button(button, Controllers.getCurrent());
    }

    public InputTrigger button(Mapping button, ControllerSystem controllerSystem, int index) {
        Controller controller = controllerSystem != null ?
                controllerSystem.getController(index) : null;
        return button(button, controller);
    }

    public InputTrigger button(Mapping button, Controller controller) {
        this.button = (button != null ? button.map(controller) : UNDEFINED);
        return this;
    }

    public InputTrigger button(int button) {
        this.button = button;
        return this;
    }

    public InputTrigger axis(Mapping axis) {
        return axis(axis, Controllers.getCurrent());
    }

    public InputTrigger axis(Mapping axis, ControllerSystem controllerSystem, int index) {
        Controller controller = controllerSystem != null ?
                controllerSystem.getController(index) : null;
        return axis(axis, controller);
    }

    public InputTrigger axis(Mapping axis, Controller controller) {
        this.axis = (axis != null ? axis.map(controller) : UNDEFINED);
        return this;
    }

    public InputTrigger axis(int axis) {
        this.axis = axis;
        return this;
    }

    public boolean isActive(InputSystem inputs, ControllerSystem controllers,
                            Controller controller) {
        return (key != UNDEFINED && isKeyActive(inputs, key)) ||
                (mouse != UNDEFINED && isMouseActive(inputs, mouse)) ||
                (button != UNDEFINED && isButtonActive(controllers, controller, button)) ||
                (axis != UNDEFINED && isAxisActive(controllers, controller, axis));
    }

    public abstract boolean isKeyActive(InputSystem inputs, int key);

    public abstract boolean isMouseActive(InputSystem inputs, int mouse);

    public abstract boolean isButtonActive(ControllerSystem controllers, Controller controller,
                                           int button);

    public abstract boolean isAxisActive(ControllerSystem controllers, Controller controller,
                                         int axis);

    public static class Pressed extends InputTrigger {
        public float deadZone = 0.07f;

        public Pressed() {
            // Intentionally empty
        }

        public Pressed(int key, int mouse, Mapping button, Mapping axis) {
            super(key, mouse, button, axis);
        }

        public Pressed(int key, int mouse, Mapping button, Mapping axis,
                       ControllerSystem controllerSystem, int index) {
            super(key, mouse, button, axis, controllerSystem, index);
        }

        public Pressed(int key, int mouse, Mapping button, Mapping axis, Controller controller) {
            super(key, mouse, button, axis, controller);
        }

        public Pressed(int key, int mouse, int button, int axis) {
            super(key, mouse, button, axis);
        }

        @Override
        public boolean isKeyActive(InputSystem inputs, int key) {
            return inputs.isKeyPressed(key);
        }

        @Override
        public boolean isMouseActive(InputSystem inputs, int mouse) {
            return inputs.isMousePressed(mouse);
        }

        @Override
        public boolean isButtonActive(ControllerSystem controllers, Controller controller,
                                      int button) {
            return controllers.isPressed(controller, button);
        }

        @Override
        public boolean isAxisActive(ControllerSystem controllers, Controller controller, int axis) {
            return Math.abs(controllers.getAxisValue(controller, axis)) >= deadZone;
        }

        public Pressed key(int key) {
            return (Pressed) super.key(key);
        }

        public Pressed mouse(int mouse) {
            return (Pressed) super.mouse(mouse);
        }

        public Pressed controller(Mapping button, Mapping axis) {
            return (Pressed) super.controller(button, axis);
        }

        public Pressed controller(Mapping button, Mapping axis,
                                  ControllerSystem controllerSystem, int index) {
            return (Pressed) super.controller(button, axis, controllerSystem, index);
        }

        public Pressed controller(Mapping button, Mapping axis, Controller controller) {
            return (Pressed) super.controller(button, axis, controller);
        }

        public Pressed controller(int button, int axis) {
            return (Pressed) super.controller(button, axis);
        }

        public Pressed button(Mapping button) {
            return (Pressed) super.button(button);
        }

        public Pressed button(Mapping button, ControllerSystem controllerSystem, int index) {
            return (Pressed) super.button(button, controllerSystem, index);
        }

        public Pressed button(Mapping button, Controller controller) {
            return (Pressed) super.button(button, controller);
        }

        public Pressed button(int button) {
            return (Pressed) super.button(button);
        }

        public Pressed axis(Mapping axis) {
            return (Pressed) super.axis(axis);
        }

        public Pressed axis(Mapping axis, ControllerSystem controllerSystem, int index) {
            return (Pressed) super.axis(axis, controllerSystem, index);
        }

        public Pressed axis(Mapping axis, Controller controller) {
            return (Pressed) super.axis(axis, controller);
        }

        public Pressed axis(int axis) {
            return (Pressed) super.axis(axis);
        }

        public Pressed deadZone(float deadZone) {
            this.deadZone = deadZone;
            return this;
        }
    }

    public static class JustPressed extends InputTrigger {
        public JustPressed() {
            // Intentionally empty
        }

        public JustPressed(int key, int mouse, Mapping button, Mapping axis) {
            super(key, mouse, button, axis);
        }

        public JustPressed(int key, int mouse, Mapping button, Mapping axis,
                           ControllerSystem controllerSystem, int index) {
            super(key, mouse, button, axis, controllerSystem, index);
        }

        public JustPressed(int key, int mouse, Mapping button, Mapping axis,
                           Controller controller) {
            super(key, mouse, button, axis, controller);
        }

        public JustPressed(int key, int mouse, int button, int axis) {
            super(key, mouse, button, axis);
        }

        @Override
        public boolean isKeyActive(InputSystem inputs, int key) {
            return inputs.isKeyJustPressed(key);
        }

        @Override
        public boolean isMouseActive(InputSystem inputs, int mouse) {
            return inputs.isMouseJustPressed(mouse);
        }

        @Override
        public boolean isButtonActive(ControllerSystem controllers, Controller controller,
                                      int button) {
            return controllers.isJustPressed(controller, button);
        }

        @Override
        public boolean isAxisActive(ControllerSystem controllers, Controller controller, int axis) {
            return false;
        }
    }

    public static class JustPressedOnTick extends InputTrigger {
        public JustPressedOnTick() {
            // Intentionally empty
        }

        public JustPressedOnTick(int key, int mouse, Mapping button, Mapping axis) {
            super(key, mouse, button, axis);
        }

        public JustPressedOnTick(int key, int mouse, Mapping button, Mapping axis,
                                 ControllerSystem controllerSystem, int index) {
            super(key, mouse, button, axis, controllerSystem, index);
        }

        public JustPressedOnTick(int key, int mouse, Mapping button, Mapping axis,
                                 Controller controller) {
            super(key, mouse, button, axis, controller);
        }

        public JustPressedOnTick(int key, int mouse, int button, int axis) {
            super(key, mouse, button, axis);
        }

        @Override
        public boolean isKeyActive(InputSystem inputs, int key) {
            return inputs.isKeyJustPressedOnTick(key);
        }

        @Override
        public boolean isMouseActive(InputSystem inputs, int mouse) {
            return inputs.isMouseJustPressedOnTick(mouse);
        }

        @Override
        public boolean isButtonActive(ControllerSystem controllers, Controller controller,
                                      int button) {
            return controllers.isJustPressedOnTick(controller, button);
        }

        @Override
        public boolean isAxisActive(ControllerSystem controllers, Controller controller, int axis) {
            return false;
        }
    }
}
