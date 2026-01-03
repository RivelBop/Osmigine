package com.rivelbop.osmigine;

import com.badlogic.gdx.graphics.Texture;
import com.rivelbop.osmigine.input.CursorProvider;
import com.rivelbop.osmigine.scene.Scene;
import com.rivelbop.osmigine.scene.SceneManager;

/** DON'T COPY THIS CLASS (EXAMPLE ONLY) - TYPICALLY ENUM USED INSTEAD OF STRING */
public final class Osmigine extends SceneManager<String> {
    public Osmigine() {
        super(new CursorProvider.Default());
    }

    /** CALL FOR LWJGL3 */
    public Osmigine(CursorProvider cursorProvider) {
        super(cursorProvider);
    }

    @Override
    public void init() {
        setScene(new Scene<>(1f) {
            private final Texture texture = new Texture("libgdx.png");

            @Override
            public void tick() {
                System.out.println("TICK");
            }

            @Override
            public void render() {
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
