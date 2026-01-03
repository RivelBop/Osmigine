package com.rivelbop.osmigine.input;

import com.badlogic.gdx.controllers.Controller;

public final class ControllerState {
    public final Controller controller;
    public final InputEvent[] buttonEvents;
    public final float[] axisValues;

    public ControllerState(Controller controller) {
        this.controller = controller;
        if (controller == null) {
            buttonEvents = new InputEvent[0];
            axisValues = new float[0];
            return;
        }

        // Create button events to avoid null
        buttonEvents = new InputEvent[controller.getMaxButtonIndex() -
                controller.getMinButtonIndex() + 1];
        for (int i = 0; i < buttonEvents.length; i++) {
            buttonEvents[i] = new InputEvent();
        }

        axisValues = new float[controller.getAxisCount()];
    }
}
