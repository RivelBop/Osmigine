package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

/** DON'T COPY THIS CLASS (EXAMPLE ONLY) */
public enum Sounds implements SoundAsset {
    TEST("test.ogg"),
    HURT("hurt.ogg");

    private final String filename;
    private Sound sound;

    Sounds(String filename) {
        this.filename = "audio/sounds/" + filename;
    }

    @Override
    public void loadToAssets(AssetManager assets) {
        assets.load(filename, Sound.class);
    }

    @Override
    public void loadFromAssets(AssetManager assets) {
        sound = assets.get(filename, Sound.class);
    }

    @Override
    public float duration() {
        return AudioSystem.getDuration(Gdx.files.internal(filename));
    }

    @Override
    public Sound get() {
        return sound;
    }
}
