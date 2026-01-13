package com.rivelbop.osmigine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.rivelbop.osmigine.audio.SoundInstance;
import com.rivelbop.osmigine.audio.Sounds;
import com.rivelbop.osmigine.audio.Tracks;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.scaling.ScalingLabel;
import com.rivelbop.osmigine.scaling.ScalingSprite;
import com.rivelbop.osmigine.scene.Scene;
import com.rivelbop.osmigine.scene.SceneManager;

/** DON'T COPY THIS CLASS (EXAMPLE ONLY) - TYPICALLY ENUM USED INSTEAD OF STRING */
public final class Osmigine extends SceneManager<String, Sounds, Tracks> {
    public Osmigine() {
        // Creates a SceneManager with the default cursor provider (non-precise cursor position ->
        // polling), uses the example Sounds and Tracks enums for the AudioSystem, the
        // ScalingSystem has a target screen width and height of 640x480 (the default parameters ->
        // from the LWJGL3 Launcher)
        super(new CursorProvider.Default(), Sounds.class, Tracks.class, 640, 480);
    }

    /** CALL FOR LWJGL3 */
    public Osmigine(CursorProvider cursorProvider) {
        // Creates a SceneManager with the provided cursor provider (see LWJGL3 Launcher parameter),
        // uses the example Sounds and Tracks enums for the AudioSystem, the ScalingSystem has a
        // target screen width and height of 640x480 (the default params from the LWJGL3 Launcher)
        super(cursorProvider, Sounds.class, Tracks.class, 640, 480);
    }

    @Override
    public void init() {
        // Create and set a new scene with a 5 ticks per second tick rate (20-30 tps is common)
        // Typically the user would create a new class for a Scene, this is just an example!
        setScene(new Scene<>(1 / 5f) {
            // Loops and stores a positional test sound instance at (0,0)
            private final SoundInstance<Sounds> testSound =
                    audio.playSoundAt(Vector2.Zero, Sounds.TEST, true);
            // Used to manipulate the sound listener's position (to test positional audio)
            private final Vector2 listenerPos = new Vector2();
            // Manipulates the test sound pause state
            private boolean testSoundIsPaused = false;

            // libGDX sample texture (for sprite), disposed at the end of the scenes lifetime
            private final Texture texture = new Texture("libgdx.png");
            // Creates a perfectly centered scaling sprite from the texture above
            // Handled by the SceneManager's ScalingSystem (by using anchor scaling.center)
            private final ScalingSprite scalingSprite =
                    new ScalingSprite(texture, 0f, 0f, scaling.center, Align.center);
            // Initialized below
            private final ScalingLabel scalingLabel;

            {
                // CREDIT: Music by C418
                audio.queueMusic(Tracks.SWEDEN, true);

                // Generates a bitmap font of size 56
                // CREDIT: https://ekobimantara.com/product/mamboe/
                FreeTypeFontGenerator fontGenerator =
                        new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
                BitmapFont bitmapFont = fontGenerator.generateFont(
                        new FreeTypeFontGenerator.FreeTypeFontParameter() {{
                            this.size = 56;
                        }});
                fontGenerator.dispose();

                // Creates a scaling label with text "TEST" offset 10 pixels to the right and
                // 10 pixels down from the top-left anchor using top-left alignment
                scalingLabel = new ScalingLabel(bitmapFont, "TEST",
                        10, -10, scaling.topLeft, Align.topLeft);
            }

            @Override
            public void tick() {
                // Print a message for testing
                System.out.println("TICK");
                // Play a regular, non-positional sample hurt sound effect
                audio.playSound(Sounds.HURT);
            }

            @Override
            public void render() {
                // Move the sound listener's position
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
                audio.setSoundListenerPosition(listenerPos);

                // Toggle the test sound and music on/off (by pausing/resuming)
                if (inputs.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    testSoundIsPaused = !testSoundIsPaused;
                    if (testSoundIsPaused) {
                        testSound.pause();
                        audio.pauseMusic();
                    } else {
                        testSound.resume();
                        audio.resumeMusic();
                    }
                }

                // Applies the SceneManager's ScalingSystem's viewport and sets the appropriate
                // combined projection matrix to the SceneManager's sprite batch
                scaling.apply(spriteBatch);

                // Draw the scaling elements
                spriteBatch.begin();
                scalingSprite.draw(spriteBatch);
                scalingLabel.draw(spriteBatch);
                spriteBatch.end();
            }

            @Override
            public void resize(int width, int height) {
                // Nothing to call here, the ScalingSystem (and all its elements) are automatically
                // resized by the SceneManager
            }

            @Override
            public void dispose() {
                texture.dispose();
            }
        });
    }
}
