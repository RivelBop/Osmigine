package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;

import static com.rivelbop.osmigine.audio.AudioSystem.FULL_PAN_RANGE;
import static com.rivelbop.osmigine.audio.AudioSystem.FULL_VOLUME_RANGE;

public final class MusicInstance <M extends Enum<M> & MusicAsset> implements Music.OnCompletionListener {
    public final M musicAsset;
    public final Music music;
    public final float duration;

    private float relativeVolume;
    private float masterVolume;
    private float rawVolume;

    private float pan;
    private boolean isLooping;

    private float position;
    private boolean isPaused;
    private boolean isFinished;

    private boolean isActive;

    public MusicInstance(M musicAsset, float relativeVolume, float masterVolume, float pan,
                         boolean loop, float duration) {
        this.musicAsset = musicAsset;
        this.music = musicAsset.get();

        this.relativeVolume = relativeVolume;
        this.masterVolume = masterVolume;
        this.rawVolume = relativeVolume * masterVolume;

        this.pan = pan;
        this.isLooping = loop;

        this.duration = duration;
    }

    public void update() {
        if (isPaused || isFinished || !isActive) {
            return;
        }
        position = music.getPosition();
    }

    public void pause() {
        if (isPaused || isFinished || !isActive) {
            return;
        }

        isPaused = true;
        position = music.getPosition();
        music.pause();
    }

    public void resume() {
        if (!isPaused || isFinished || !isActive) {
            return;
        }

        isPaused = false;
        music.play();
    }

    public void stop() {
        if (isFinished || !isActive) {
            return;
        }

        isFinished = true;
        music.stop();
    }

    public void setRelativeVolume(float newVolume) {
        if (isFinished || !isActive) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        relativeVolume = newVolume;
        rawVolume = relativeVolume * masterVolume;
        music.setVolume(rawVolume);
    }

    public void setMasterVolume(float newVolume) {
        if (isFinished || !isActive) {
            return;
        }

        newVolume = MathUtils.clamp(newVolume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        masterVolume = newVolume;
        rawVolume = relativeVolume * masterVolume;
        music.setVolume(rawVolume);
    }

    public void setPan(float newPan) {
        if (isFinished || !isActive) {
            return;
        }

        newPan = MathUtils.clamp(newPan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);
        pan = newPan;
        music.setPan(pan, rawVolume);
    }

    public void setLooping(boolean loop) {
        if (isFinished || !isActive) {
            return;
        }

        isLooping = loop;
        music.setLooping(isLooping);
    }

    public void setPosition(float position) {
        if (isFinished || !isActive) {
            return;
        }

        position = MathUtils.clamp(position, 0f, duration);
        music.setPosition(position);
        this.position = music.getPosition();
    }

    /** Default to only be accessible by the AudioSystem */
    void setActive(boolean active) {
        boolean wasChanged = (isActive != active);
        isActive = active;

        if (wasChanged && isActive) {
            music.setPosition(position);
            music.setPan(pan, rawVolume);
            music.setLooping(isLooping);

            music.setOnCompletionListener(this);
            if (!isFinished && !isPaused) {
                music.play();
            }
        } else if (wasChanged) {
            position = music.getPosition();
            music.pause();
            music.setOnCompletionListener(null);
        }
    }

    @Override
    public void onCompletion(Music music) {
        isFinished = !isLooping;
    }

    public float getRelativeVolume() {
        return relativeVolume;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getRawVolume() {
        return rawVolume;
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

    public float getPosition() {
        if (isFinished) {
            return duration;
        }

        if (isActive) {
            position = music.getPosition();
        }
        return position;
    }

    public boolean isActive() {
        return isActive;
    }
}
