package com.rivelbop.osmigine.scene;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rivelbop.osmigine.input.InputMap;
import de.eskalon.commons.screen.ManagedScreen;

public abstract class Scene<T> extends ManagedScreen {
    public final SceneManager<T> sceneManager;
    public final float tickRate;

    protected AssetManager assets;
    protected SpriteBatch spriteBatch;
    protected InputMap<T> inputMap;

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
            sceneManager = (SceneManager<T>) app;
        } else {
            throw new IllegalArgumentException("App must be an instance of SceneManager!");
        }
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
            }
            alpha = tickTimer / tickRate;
        }
        render();
    }

    public final float alpha() {
        return alpha;
    }
}
