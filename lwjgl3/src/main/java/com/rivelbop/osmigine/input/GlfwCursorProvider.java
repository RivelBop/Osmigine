package com.rivelbop.osmigine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.math.Vector2;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.input.InputSystem;
import org.lwjgl.glfw.GLFW;

public final class GlfwCursorProvider implements CursorProvider {
    private final double[] glfwCursorPosX = new double[1];
    private final double[] glfwCursorPosY = new double[1];
    private final Vector2 glfwCursorPosition = new Vector2();
    private Long window;

    @Override
    public Vector2 getPrecisePosition(InputSystem inputSystem) {
        if (window == null) {
            window = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
        }

        GLFW.glfwGetCursorPos(window, glfwCursorPosX, glfwCursorPosY);
        glfwCursorPosition.set((float) glfwCursorPosX[0], (float) glfwCursorPosY[0]);
        glfwCursorPosition.scl(Gdx.graphics.getBackBufferScale());
        return glfwCursorPosition;
    }
}
