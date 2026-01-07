package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import org.jspecify.annotations.Nullable;

import static com.rivelbop.osmigine.audio.AudioSystem.*;

public final class SoundInstance <S extends Enum<S> & SoundAsset> {
    public final S soundAsset;
    public final Sound sound;
    public final float duration;
    public final long id;

    // default - only really needed for the AudioSystem
    int positionArrayIndex = -1;
    boolean positionUpdated;

    private final long durationNanos;

    private float relativeVolume;
    private float masterVolume;
    private float positionalVolume = 1f;
    private float rawVolume;

    private float pitch;
    private float pan;
    private boolean isLooping;

    private long startTime;
    private long pauseTime;
    private boolean isPaused;
    private boolean isFinished;

    private Vector2 position;

    public SoundInstance(S soundAsset, float relativeVolume, float masterVolume, float pitch,
                         float pan, boolean loop, long id, float duration) {
        this.soundAsset = soundAsset;
        this.sound = soundAsset != null ? soundAsset.get() : NullSound.INSTANCE;
        this.id = id;

        this.relativeVolume = relativeVolume;
        this.masterVolume = masterVolume;
        this.rawVolume = relativeVolume * masterVolume * positionalVolume;

        this.pitch = pitch;
        this.pan = pan;
        this.isLooping = loop;

        this.duration = duration;
        this.durationNanos = (long) (duration * 1000000000L);
        this.startTime = TimeUtils.nanoTime();

        if (sound == NullSound.INSTANCE || id == AudioSystem.INVALID_SOUND_ID) {
            isFinished = true;
        }
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

    public void setRelativeVolume(float newVolume) {
        if (isFinished) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        relativeVolume = newVolume;
        rawVolume = relativeVolume * masterVolume * positionalVolume;
        sound.setVolume(id, rawVolume);
    }

    public void setMasterVolume(float newVolume) {
        if (isFinished) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        masterVolume = newVolume;
        rawVolume = relativeVolume * masterVolume * positionalVolume;
        sound.setVolume(id, rawVolume);
    }

    public void setPositionalVolume(float newVolume) {
        if (isFinished) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        positionalVolume = newVolume;
        rawVolume = relativeVolume * masterVolume * positionalVolume;
        sound.setVolume(id, rawVolume);
    }

    public void setPositionalVolume(float newVolume, float newPan) {
        if (isFinished) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        newPan = MathUtils.clamp(newPan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);

        positionalVolume = newVolume;
        rawVolume = relativeVolume * masterVolume * positionalVolume;
        pan = newPan;

        sound.setPan(id, pan, rawVolume);
    }

    public void setPitch(float newPitch) {
        if (isFinished) {
            return;
        }

        // Calculate progress percentage
        long elapsed = TimeUtils.timeSinceNanos(startTime);
        double progress = (double) elapsed / (durationNanos / pitch);

        // Set pitch
        newPitch = MathUtils.clamp(newPitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        pitch = newPitch;
        sound.setPitch(id, pitch);

        // Move startTime to be the same percentage of progress with the new duration
        long newPitchedDuration = (long) (durationNanos / pitch);
        startTime = TimeUtils.nanoTime() - (long) (progress * newPitchedDuration);
    }

    public void setPan(float newPan) {
        if (isFinished) {
            return;
        }

        newPan = MathUtils.clamp(newPan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);
        pan = newPan;
        sound.setPan(id, pan, rawVolume);
    }

    public void setLooping(boolean loop) {
        if (isFinished) {
            return;
        }

        isLooping = loop;
        sound.setLooping(id, isLooping);
    }

    public void setPosition(Vector2 newPos) {
        if (newPos != null) {
            setPosition(newPos.x, newPos.y);
        }
    }

    public void setPosition(float x, float y) {
        if (position == null) { // No need to update position (this is called on position creation)
            position = new Vector2(x, y);
            return;
        }

        if (Float.floatToIntBits(position.x) != Float.floatToIntBits(x) ||
                Float.floatToIntBits(position.y) != Float.floatToIntBits(y)) {
            positionUpdated = true;
            position.set(x, y);
        }
    }

    // NO GETTER FOR ID (TO AVOID DIRECTLY CALLING METHODS FROM THEM)

    public S getSoundAsset() {
        return soundAsset;
    }

    public Sound getSound() {
        return sound;
    }

    public float getDuration() {
        return duration;
    }

    public float getRelativeVolume() {
        return relativeVolume;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getPositionalVolume() {
        return positionalVolume;
    }

    public float getRawVolume() {
        return rawVolume;
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

    /** DO NOT CHANGE - VOLUME AND PAN WILL NOT BE UPDATED! */
    @Nullable
    public Vector2 getPosition() {
        return position;
    }
}
