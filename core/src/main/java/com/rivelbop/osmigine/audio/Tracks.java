package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

/** DON'T COPY THIS CLASS (EXAMPLE ONLY) */
public enum Tracks implements MusicAsset {
    SWEDEN("sweden.ogg");

    private final String filename;
    private Music track;

    Tracks(String filename) {
        this.filename = "audio/tracks/" + filename;
    }

    @Override
    public void loadToAssets(AssetManager assets) {
        assets.load(filename, Music.class);
    }

    @Override
    public void loadFromAssets(AssetManager assets) {
        track = assets.get(filename, Music.class);
    }

    @Override
    public float duration() {
        return AudioSystem.getDuration(Gdx.files.internal(filename));
    }

    @Override
    public Music get() {
        return track;
    }
}
