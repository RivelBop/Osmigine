package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

public final class SoundInstance <S extends Enum<S> & SoundAsset> {
    public final S soundAsset;
    public final Sound sound;
    public final float duration;
    public final long id;

    private final long durationNanos;
    private long startTime;
    private long pauseTime;
    private boolean isPaused;
    private boolean isFinished;
    private boolean isLooping;

    public SoundInstance(S soundAsset, long id, float duration, boolean isLooping) {
        this.soundAsset = soundAsset;
        this.sound = soundAsset.get();
        this.id = id;

        this.duration = duration;
        this.durationNanos = (long) (duration * 1000000000L);

        this.isLooping = isLooping;
        this.startTime = TimeUtils.nanoTime();
    }

    public void update() {
        if (isPaused || isFinished) {
            return;
        }

        long elapsed = TimeUtils.timeSinceNanos(startTime);
        if (elapsed >= durationNanos) {
            isFinished = !isLooping;
            startTime = TimeUtils.nanoTime() - (elapsed - durationNanos);
        }
    }

    public void pause() {
        if (isPaused || isFinished) {
            return;
        }

        isPaused = true;
        pauseTime = TimeUtils.nanoTime();
        sound.pause(id);
    }

    public void resume() {
        if (!isPaused || isFinished) {
            return;
        }

        long pauseDuration = TimeUtils.timeSinceNanos(pauseTime);
        startTime += pauseDuration;

        isPaused = false;
        sound.resume(id);
    }

    public void stop() {
        if (isFinished) {
            return;
        }

        isFinished = true;
        sound.stop(id);
    }

    public void setLooping(boolean loop) {
        if (isFinished) {
            return;
        }

        isLooping = loop;
        sound.setLooping(id, loop);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isLooping() {
        return isLooping;
    }
}
