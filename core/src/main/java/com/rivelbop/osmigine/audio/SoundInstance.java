package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

public final class SoundInstance <S extends Enum<S> & SoundAsset> {
    public final S soundAsset;
    public final Sound sound;
    public final float duration;
    public final long id;

    private final long durationNanos;

    private float volume;
    private float pitch;
    private float pan;
    private boolean isLooping;

    private long startTime;
    private long pauseTime;
    private boolean isPaused;
    private boolean isFinished;

    public SoundInstance(S soundAsset, float volume, float pitch, float pan, boolean loop,
                         long id, float duration) {
        this.soundAsset = soundAsset;
        this.sound = soundAsset.get();
        this.id = id;

        this.volume = volume;
        this.pitch = pitch;
        this.pan = pan;
        this.isLooping = loop;

        this.duration = duration;
        this.durationNanos = (long) (duration * 1000000000L);
        this.startTime = TimeUtils.nanoTime();
    }

    public void update() {
        if (isPaused || isFinished) {
            return;
        }

        long pitchedDurationNanos = (long) (durationNanos / pitch);
        long elapsed = TimeUtils.timeSinceNanos(startTime);
        if (elapsed >= pitchedDurationNanos) {
            if (isLooping) {
                startTime += pitchedDurationNanos;
            } else {
                isFinished = true;
            }
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

    public void setVolume(float newVolume) {
        if (isFinished || newVolume < AudioSystem.FULL_VOLUME_RANGE[0] ||
                newVolume > AudioSystem.FULL_VOLUME_RANGE[1]) {
            return;
        }

        volume = newVolume;
        sound.setVolume(id, volume);
    }

    public void setPitch(float newPitch) {
        if (isFinished || newPitch < AudioSystem.FULL_PITCH_RANGE[0] ||
                newPitch > AudioSystem.FULL_PITCH_RANGE[1]) {
            return;
        }

        // Calculate progress percentage
        long elapsed = TimeUtils.timeSinceNanos(startTime);
        double progress = (double) elapsed / (durationNanos / pitch);

        // Set pitch
        pitch = newPitch;
        sound.setPitch(id, pitch);

        // Move startTime to be the same percentage of progress with the new duration
        long newPitchedDuration = (long) (durationNanos / pitch);
        startTime = TimeUtils.nanoTime() - (long) (progress * newPitchedDuration);
    }

    public void setPan(float newPan) {
        if (isFinished || newPan < AudioSystem.FULL_PAN_RANGE[0] ||
                newPan > AudioSystem.FULL_PAN_RANGE[1]) {
            return;
        }

        pan = newPan;
        sound.setPan(id, pan, volume);
    }

    public void setLooping(boolean loop) {
        if (isFinished) {
            return;
        }

        isLooping = loop;
        sound.setLooping(id, loop);
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public float getPan() {
        return pan;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
