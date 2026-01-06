package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Sound;

import static com.rivelbop.osmigine.audio.AudioSystem.INVALID_SOUND_ID;

public final class NullSound implements Sound {
    public static final NullSound INSTANCE = new NullSound();

    private NullSound() {
    }

    @Override
    public long play() {
        return INVALID_SOUND_ID;
    }

    @Override
    public long play(float volume) {
        return INVALID_SOUND_ID;
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return INVALID_SOUND_ID;
    }

    @Override
    public long loop() {
        return INVALID_SOUND_ID;
    }

    @Override
    public long loop(float volume) {
        return INVALID_SOUND_ID;
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return INVALID_SOUND_ID;
    }

    @Override
    public void stop() {
        // Intentionally empty
    }

    @Override
    public void pause() {
        // Intentionally empty
    }

    @Override
    public void resume() {
        // Intentionally empty
    }

    @Override
    public void dispose() {
        // Intentionally empty
    }

    @Override
    public void stop(long soundId) {
        // Intentionally empty
    }

    @Override
    public void pause(long soundId) {
        // Intentionally empty
    }

    @Override
    public void resume(long soundId) {
        // Intentionally empty
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        // Intentionally empty
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        // Intentionally empty
    }

    @Override
    public void setVolume(long soundId, float volume) {
        // Intentionally empty
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        // Intentionally empty
    }
}
