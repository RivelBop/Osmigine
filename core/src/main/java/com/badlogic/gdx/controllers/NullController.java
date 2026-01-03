package com.badlogic.gdx.controllers;

import static com.badlogic.gdx.controllers.ControllerMapping.UNDEFINED;

public final class NullController implements Controller {
    public static final NullController INSTANCE = new NullController();

    private static final ControllerMapping NULL_MAPPING =
            new ControllerMapping(UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED,
                    UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED,
                    UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED);

    private NullController() {
    }

    @Override
    public boolean getButton(int buttonCode) {
        return false;
    }

    @Override
    public float getAxis(int axisCode) {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getUniqueId() {
        return "NULL_CONTROLLER";
    }

    @Override
    public int getMinButtonIndex() {
        return 0;
    }

    @Override
    public int getMaxButtonIndex() {
        return 0;
    }

    @Override
    public int getAxisCount() {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean canVibrate() {
        return false;
    }

    @Override
    public boolean isVibrating() {
        return false;
    }

    @Override
    public void startVibration(int duration, float strength) {
        // Intentionally empty
    }

    @Override
    public void cancelVibration() {
        // Intentionally empty
    }

    @Override
    public boolean supportsPlayerIndex() {
        return false;
    }

    @Override
    public int getPlayerIndex() {
        return PLAYER_IDX_UNSET;
    }

    @Override
    public void setPlayerIndex(int index) {
        // Intentionally empty
    }

    @Override
    public ControllerMapping getMapping() {
        return NULL_MAPPING;
    }

    @Override
    public ControllerPowerLevel getPowerLevel() {
        return ControllerPowerLevel.POWER_UNKNOWN;
    }

    @Override
    public void addListener(ControllerListener listener) {
        // Intentionally empty
    }

    @Override
    public void removeListener(ControllerListener listener) {
        // Intentionally empty
    }
}
