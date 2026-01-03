package com.rivelbop.osmigine.scene;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rivelbop.osmigine.input.ControllerSystem;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.input.InputMap;
import com.rivelbop.osmigine.input.InputSystem;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.transition.ScreenTransition;

/** @param <T> The class type for InputMap keys. */
public abstract class SceneManager<T> extends ManagedGame<Scene<T>, ScreenTransition> {
    protected AssetManager assets;
    protected SpriteBatch spriteBatch;

    protected InputSystem inputs;
    protected ControllerSystem controllers;
    protected InputMap<T> inputMap;

    private final CursorProvider cursorProvider;

    public SceneManager(CursorProvider cursorProvider) {
        this.cursorProvider = cursorProvider;
    }

    /** Replacement for create() method. */
    public abstract void init();

    /** Called before the current scene is updated/rendered. */
    public void preRender() {
        // Intentionally empty
    }

    /** Called after the current scene is updated/rendered. */
    public void postRender() {
        // Intentionally empty
    }

    public final void setScene(Scene<T> scene) {
        screenManager.pushScreen(scene, null);
    }

    public final void setScene(Scene<T> scene, ScreenTransition transition) {
        screenManager.pushScreen(scene, transition);
    }

    @Override
    public final void create() {
        super.create();
        getScreenManager().setAutoDispose(true, true);

        assets = new AssetManager();
        spriteBatch = new SpriteBatch();

        inputs = new InputSystem(cursorProvider, true);
        controllers = new ControllerSystem(true);
        inputMap = new InputMap<>(inputs, controllers);

        init();
    }

    @Override
    public final void render() {
        preRender();
        super.render();
        postRender();

        inputs.postRender();
        controllers.postRender();
    }

    /** CALL super.dispose() IF OVERRIDE! */
    @Override
    public void dispose() {
        super.dispose();

        controllers.dispose();
        inputs.dispose();
        spriteBatch.dispose();
        assets.dispose();
    }

    public AssetManager assets() {
        return assets;
    }

    public SpriteBatch spriteBatch() {
        return spriteBatch;
    }

    public InputSystem inputs() {
        return inputs;
    }

    public ControllerSystem controllers() {
        return controllers;
    }

    public InputMap<T> inputMap() {
        return inputMap;
    }
}
