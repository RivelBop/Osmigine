package com.rivelbop.osmigine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerMapping;

public enum Mapping {
    AXIS_LEFT_X,
    AXIS_LEFT_Y,
    AXIS_RIGHT_X,
    AXIS_RIGHT_Y,

    BUTTON_A,
    BUTTON_B,
    BUTTON_X,
    BUTTON_Y,
    BUTTON_BACK,
    BUTTON_START,

    BUTTON_L1,
    BUTTON_L2,
    BUTTON_R1,
    BUTTON_R2,

    BUTTON_DPAD_UP,
    BUTTON_DPAD_DOWN,
    BUTTON_DPAD_LEFT,
    BUTTON_DPAD_RIGHT,

    BUTTON_LEFT_STICK,
    BUTTON_RIGHT_STICK;

    public static int map(Controller c, Mapping e) {
        if (c == null) {
            return ControllerMapping.UNDEFINED;
        }

        ControllerMapping m = c.getMapping();
        switch (e) {
            case AXIS_LEFT_X:
                return m.axisLeftX;
            case AXIS_LEFT_Y:
                return m.axisLeftY;
            case AXIS_RIGHT_X:
                return m.axisRightX;
            case AXIS_RIGHT_Y:
                return m.axisRightY;
            case BUTTON_A:
                return m.buttonA;
            case BUTTON_B:
                return m.buttonB;
            case BUTTON_X:
                return m.buttonX;
            case BUTTON_Y:
                return m.buttonY;
            case BUTTON_BACK:
                return m.buttonBack;
            case BUTTON_START:
                return m.buttonStart;
            case BUTTON_L1:
                return m.buttonL1;
            case BUTTON_L2:
                return m.buttonL2;
            case BUTTON_R1:
                return m.buttonR1;
            case BUTTON_R2:
                return m.buttonR2;
            case BUTTON_DPAD_UP:
                return m.buttonDpadUp;
            case BUTTON_DPAD_DOWN:
                return m.buttonDpadDown;
            case BUTTON_DPAD_LEFT:
                return m.buttonDpadLeft;
            case BUTTON_DPAD_RIGHT:
                return m.buttonDpadRight;
            case BUTTON_LEFT_STICK:
                return m.buttonLeftStick;
            case BUTTON_RIGHT_STICK:
                return m.buttonRightStick;
            default:
                return ControllerMapping.UNDEFINED;
        }
    }

    public int map(Controller c) {
        return map(c, this);
    }
}
