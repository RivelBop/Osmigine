package com.rivelbop.osmigine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rivelbop.osmigine.audio.AudioSystem;
import com.rivelbop.osmigine.audio.MusicAsset;
import com.rivelbop.osmigine.audio.SoundAsset;
import com.rivelbop.osmigine.input.ControllerSystem;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.input.InputMap;
import com.rivelbop.osmigine.input.InputSystem;
import com.rivelbop.osmigine.scaling.ScalingSystem;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.transition.ScreenTransition;

/**
 * @param <I> The class type for InputMap keys.
 * @param <S> The enum type for SoundAssets for the AudioSystem.
 * @param <M> The enum type for MusicAssets for the AudioSystem.
 */
public abstract class SceneManager<I, S extends Enum<S> & SoundAsset,
        M extends Enum<M> & MusicAsset> extends ManagedGame<Scene<I, S, M>, ScreenTransition> {
    protected AssetManager assets;
    protected SpriteBatch spriteBatch;

    protected InputSystem inputs;
    protected ControllerSystem controllers;
    protected InputMap<I> inputMap;

    protected AudioSystem<S, M> audio;
    protected Class<S> soundClass;
    protected Class<M> musicClass;

    protected ScalingSystem scaling;

    private final CursorProvider cursorProvider;
    private final int initialTargetScreenWidth;
    private final int initialTargetScreenHeight;

    public SceneManager(CursorProvider cursorProvider, Class<S> soundClass, Class<M> musicClass,
                        int targetScreenWidth, int targetScreenHeight) {
        this.cursorProvider = cursorProvider;
        this.soundClass = soundClass;
        this.musicClass = musicClass;
        this.initialTargetScreenWidth = targetScreenWidth;
        this.initialTargetScreenHeight = targetScreenHeight;
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

    public final void setScene(Scene<I, S, M> scene) {
        screenManager.pushScreen(scene, null);
    }

    public final void setScene(Scene<I, S, M> scene, ScreenTransition transition) {
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

        // Creates audio system with a default hear range "balanced" to the current window width
        audio = new AudioSystem<>(soundClass, musicClass, Gdx.graphics.getWidth() * 0.75f);
        audio.loadToAssets(assets);
        assets.finishLoading();
        audio.loadFromAssets(assets);

        scaling = new ScalingSystem(initialTargetScreenWidth, initialTargetScreenHeight);

        init();
    }

    @Override
    public final void render() {
        preRender();
        super.render();
        postRender();

        inputs.postRender();
        controllers.postRender();
        audio.postRender();
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        scaling.resize(width, height);       // Update the scaling system
        screenManager.resize(width, height); // Update the current scene
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

    public InputMap<I> inputMap() {
        return inputMap;
    }

    public AudioSystem<S, M> audio() {
        return audio;
    }

    public ScalingSystem scaling() {
        return scaling;
    }
}
