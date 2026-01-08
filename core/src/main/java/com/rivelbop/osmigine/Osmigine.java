package com.rivelbop.osmigine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.rivelbop.osmigine.audio.SoundInstance;
import com.rivelbop.osmigine.audio.Sounds;
import com.rivelbop.osmigine.audio.Tracks;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.scene.Scene;
import com.rivelbop.osmigine.scene.SceneManager;

/** DON'T COPY THIS CLASS (EXAMPLE ONLY) - TYPICALLY ENUM USED INSTEAD OF STRING */
public final class Osmigine extends SceneManager<String, Sounds, Tracks> {
    public Osmigine() {
        super(new CursorProvider.Default(), Sounds.class, Tracks.class);
    }

    /** CALL FOR LWJGL3 */
    public Osmigine(CursorProvider cursorProvider) {
        super(cursorProvider, Sounds.class, Tracks.class);
    }

    @Override
    public void init() {
        setScene(new Scene<>(1 / 30f) {
            private final Texture texture = new Texture("libgdx.png");
            private final Vector2 listenerPos = new Vector2();

            private final SoundInstance<Sounds> test;
            private boolean isPaused = false;

            {
                audio.queueMusic(Tracks.SWEDEN);
                audio.queueMusic(Tracks.SWEDEN, true);
                test = audio.playSoundAt(Vector2.Zero, Sounds.TEST);
                test.setLooping(true);
                test.setPan(2f);
            }

            @Override
            public void tick() {
                System.out.println("TICK");
                audio.playSound(Sounds.HURT);
            }

            @Override
            public void render() {
                if (inputs.isKeyPressed(Input.Keys.W)) {
                    listenerPos.y += 50f * Gdx.graphics.getDeltaTime();
                }

                if (inputs.isKeyPressed(Input.Keys.A)) {
                    listenerPos.x -= 50f * Gdx.graphics.getDeltaTime();
                }

                if (inputs.isKeyPressed(Input.Keys.S)) {
                    listenerPos.y -= 50f * Gdx.graphics.getDeltaTime();
                }

                if (inputs.isKeyPressed(Input.Keys.D)) {
                    listenerPos.x += 50f * Gdx.graphics.getDeltaTime();
                }

                if (inputs.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    isPaused = !isPaused;
                    if (isPaused) {
                        audio.pauseAllSounds();
                        audio.pauseMusic();
                    } else {
                        audio.resumeAllSounds();
                        audio.resumeMusic();
                    }
                }

                if (inputs.isKeyJustPressed(Input.Keys.Q)) {
                    audio.stopAllSounds();
                }

                if (inputs.isKeyJustPressed(Input.Keys.R)) {
                    audio.stopMusic();
                }

                audio.setSoundListenerPosition(listenerPos);

                spriteBatch.begin();
                spriteBatch.draw(texture, 0f, 0f);
                spriteBatch.end();
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void dispose() {
                texture.dispose();
            }
        });
    }
}
