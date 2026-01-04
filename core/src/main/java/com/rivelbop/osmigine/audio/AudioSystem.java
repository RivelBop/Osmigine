package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rivelbop.osmigine.assets.Asset;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;

/**
 * @param <S> The SoundAsset enum.
 * @param <M> The MusicAsset enum.
 */
public final class AudioSystem<S extends Enum<? extends SoundAsset>,
        M extends Enum<? extends MusicAsset>> implements Asset {
    public static final long INVALID_SOUND_ID = -1;
    public static final float[] FULL_VOLUME_RANGE = {0f, 1f};
    public static final float[] FULL_PITCH_RANGE = {0.5f, 2f};
    public static final float[] FULL_PAN_RANGE = {-1f, 1f};

    public static final float[] DEFAULT_PITCH_RANGE = {0.8f, 1.2f};
    public static final float DEFAULT_PITCH = 1f;
    public static final float DEFAULT_PAN = 0f;

    private static final String MASTER_VOLUME_PREF = "masterVolume";
    private static final String SOUND_VOLUME_PREF = "soundVolume";
    private static final String MUSIC_VOLUME_PREF = "musicVolume";
    private final Preferences preferences = Gdx.app.getPreferences(getClass().getCanonicalName());

    private final Vector2 soundListenerPosition = new Vector2();
    private float hearRange;

    private float masterVolume;
    private float soundVolume;
    private float musicVolume;

    // Cached Values
    private float currentSoundVolume;
    private float currentMusicVolume;

    // Used for loading to/from assets
    private final Class<S> soundClass;
    private final Class<M> musicClass;

    public AudioSystem(Class<S> soundClass, Class<M> musicClass, float hearRange) {
        this.soundClass = soundClass;
        this.musicClass = musicClass;

        setHearRange(hearRange);
        load();
    }

    public void save() {
        preferences.putFloat(MASTER_VOLUME_PREF, masterVolume);
        preferences.putFloat(SOUND_VOLUME_PREF, soundVolume);
        preferences.putFloat(MUSIC_VOLUME_PREF, musicVolume);
        preferences.flush();
    }

    public void load() {
        masterVolume = preferences.getFloat(MASTER_VOLUME_PREF, FULL_VOLUME_RANGE[1]);
        soundVolume = preferences.getFloat(SOUND_VOLUME_PREF, FULL_VOLUME_RANGE[1]);
        musicVolume = preferences.getFloat(MUSIC_VOLUME_PREF, FULL_VOLUME_RANGE[1]);

        currentSoundVolume = masterVolume * soundVolume;
        currentMusicVolume = masterVolume * musicVolume;
    }

    public long play(S sound) {
        return ((SoundAsset) sound).get().play(currentSoundVolume);
    }

    public long play(S sound, float volume) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        return ((SoundAsset) sound).get().play(currentSoundVolume * volume);
    }

    public long play(S sound, float volume, float pitch) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        return ((SoundAsset) sound).get().play(currentSoundVolume * volume, pitch, DEFAULT_PAN);
    }

    public long play(S sound, float volume, float pitch, float pan) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        pan = MathUtils.clamp(pan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);
        return ((SoundAsset) sound).get().play(currentSoundVolume * volume, pitch, pan);
    }

    public void setSoundListenerPosition(Vector2 position) {
        soundListenerPosition.set(position);
    }

    public void setHearRange(float hearRange) {
        this.hearRange = Math.abs(hearRange);
    }

    public long playAt(Vector2 position, S sound) {
        return playAt(position, sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH);
    }

    public long playAt(Vector2 position, S sound, float volume) {
        return playAt(position, sound, volume, DEFAULT_PITCH);
    }

    public long playAt(Vector2 position, S sound, float volume, float pitch) {
        float pos2 = soundListenerPosition.dst2(position);
        float hearRange2 = hearRange * hearRange;

        if (pos2 <= hearRange2) {
            float distance = (float) Math.sqrt(pos2);
            volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
            return play(sound, (FULL_VOLUME_RANGE[1] - (distance / hearRange)) * volume, pitch,
                    (position.x - soundListenerPosition.x) / (hearRange / 2f));
        }
        return INVALID_SOUND_ID;
    }

    public void pause(S sound) {
        ((SoundAsset) sound).get().pause();
    }

    public void pause(S sound, long soundId) {
        ((SoundAsset) sound).get().pause(soundId);
    }

    public void resume(S sound) {
        ((SoundAsset) sound).get().resume();
    }

    public void resume(S sound, long soundId) {
        ((SoundAsset) sound).get().resume(soundId);
    }

    public void stop(S sound) {
        ((SoundAsset) sound).get().stop();
    }

    public void stop(S sound, long soundId) {
        ((SoundAsset) sound).get().stop(soundId);
    }

    public void playMusic(M music, boolean loop) {
        Music track = ((MusicAsset) music).get();
        track.setLooping(loop);
        track.setVolume(currentMusicVolume);
        track.play();
    }

    public void setMasterVolume(float volume) {
        masterVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentSoundVolume = masterVolume * soundVolume;
        currentMusicVolume = masterVolume * musicVolume;
    }

    public void setSoundVolume(float volume) {
        soundVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentSoundVolume = masterVolume * soundVolume;
    }

    public void setMusicVolume(float volume) {
        musicVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentMusicVolume = masterVolume * musicVolume;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public float getHearRange() {
        return hearRange;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Get the duration of an audio file (wav, mp3, ogg, etc.) in seconds.
     *
     * @param audioFile The file handle to reference audio file data.
     * @return The track length of the audio file (in seconds).
     */
    public static float getDuration(FileHandle audioFile) {
        File tempFile = null;
        try {
            // Ensure Android mode is active if we are on Android
            boolean isAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
            if (isAndroid) {
                TagOptionSingleton.getInstance().setAndroid(true);
            }

            String extension = "." + audioFile.extension();
            File parentDir = isAndroid ? Gdx.files.local("").file() : null;
            tempFile = File.createTempFile("temp_audio", extension, parentDir);
            tempFile.deleteOnExit();

            audioFile.copyTo(new FileHandle(tempFile));

            AudioFile audioMetadata = AudioFileIO.read(tempFile);
            return (float) audioMetadata.getAudioHeader().getPreciseTrackLength();
        } catch (Exception e) {
            Gdx.app.error("AudioSystem", "Error reading metadata: " + e.getMessage());
            return 0f;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void loadToAssets(AssetManager assets) {
        for (S sound : soundClass.getEnumConstants()) {
            ((SoundAsset) sound).loadToAssets(assets);
        }
        for (M music : musicClass.getEnumConstants()) {
            ((MusicAsset) music).loadToAssets(assets);
        }
    }

    @Override
    public void loadFromAssets(AssetManager assets) {
        for (S sound : soundClass.getEnumConstants()) {
            ((SoundAsset) sound).loadFromAssets(assets);
        }
        for (M music : musicClass.getEnumConstants()) {
            ((MusicAsset) music).loadFromAssets(assets);
        }
    }
}
