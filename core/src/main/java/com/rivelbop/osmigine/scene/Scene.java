package com.rivelbop.osmigine.scene;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rivelbop.osmigine.audio.AudioSystem;
import com.rivelbop.osmigine.audio.MusicAsset;
import com.rivelbop.osmigine.audio.SoundAsset;
import com.rivelbop.osmigine.input.ControllerSystem;
import com.rivelbop.osmigine.input.InputMap;
import com.rivelbop.osmigine.input.InputSystem;
import de.eskalon.commons.screen.ManagedScreen;

/**
 * @param <I> The class type for InputMap keys.
 * @param <S> The enum type for SoundAssets for the AudioSystem.
 * @param <M> The enum type for MusicAssets for the AudioSystem.
 */
public abstract class Scene<I, S extends Enum<S> & SoundAsset,
        M extends Enum<M> & MusicAsset> extends ManagedScreen {
    public final SceneManager<I, S, M> sceneManager;
    public final float tickRate;

    // Easier access to the "necessary" parts of the SceneManager
    protected final AssetManager assets;
    protected final SpriteBatch spriteBatch;
    protected final InputMap<I> inputMap;
    protected final AudioSystem<S, M> audio;

    // Private - force user to use a mapping system for better future-proofing
    private final InputSystem inputs;
    private final ControllerSystem controllers;

    private float tickTimer;
    private float alpha;

    /**
     * Creates a Scene with the given tick rate.
     *
     * @param tickRate If <= 0, tick() will not be called.
     */
    @SuppressWarnings("unchecked")
    public Scene(float tickRate) {
        ApplicationListener app = Gdx.app.getApplicationListener();
        if (app instanceof SceneManager) {
            sceneManager = (SceneManager<I, S, M>) app;
        } else {
            throw new IllegalArgumentException("App must be an instance of SceneManager!");
        }

        assets = sceneManager.assets;
        spriteBatch = sceneManager.spriteBatch;
        inputMap = sceneManager.inputMap;
        audio = sceneManager.audio;

        inputs = sceneManager.inputs;
        controllers = sceneManager.controllers;

        this.tickRate = tickRate;
    }

    /** OVERRIDE IF USING TICK SYSTEM! */
    public void tick() {
        // Intentionally empty
    }

    public abstract void render();

    @Override
    public final void render(float delta) {
        if (tickRate > 0f) {
            tickTimer += delta;
            while (tickTimer >= tickRate) {
                tick();
                tickTimer -= tickRate;

                inputs.postTick();
                controllers.postTick();
            }
            alpha = tickTimer / tickRate;
        }
        render();
    }

    public final float alpha() {
        return alpha;
    }
}
