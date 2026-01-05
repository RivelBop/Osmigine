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
import java.util.EnumMap;
import java.util.Map;

/**
 * @param <S> The SoundAsset enum.
 * @param <M> The MusicAsset enum.
 */
public final class AudioSystem<S extends Enum<S> & SoundAsset,
        M extends Enum<M> & MusicAsset> implements Asset {
    public static final long INVALID_SOUND_ID = -1;
    public static final float[] FULL_VOLUME_RANGE = {0f, 1f};
    public static final float[] FULL_PITCH_RANGE = {0.5f, 2f};
    public static final float[] FULL_PAN_RANGE = {-1f, 1f};

    public static final float[] DEFAULT_PITCH_RANGE = {0.8f, 1.2f};
    public static final float DEFAULT_PITCH = 1f;
    public static final float DEFAULT_PAN = 0f;

    public static final String MASTER_VOLUME_PREF = "masterVolume";
    public static final String SOUND_VOLUME_PREF = "soundVolume";
    public static final String MUSIC_VOLUME_PREF = "musicVolume";
    private final Preferences preferences = Gdx.app.getPreferences(getClass().getCanonicalName());

    // Used for loading to/from assets
    private final Class<S> soundClass;
    private final Class<M> musicClass;
    private final Map<S, Float> soundDurationMap;
    private final Map<M, Float> musicDurationMap;

    private final Vector2 soundListenerPosition = new Vector2();
    private float hearRange;

    private float masterVolume;
    private float soundVolume;
    private float musicVolume;

    // Cached Values
    private float currentSoundVolume;
    private float currentMusicVolume;

    public AudioSystem(Class<S> soundClass, Class<M> musicClass, float hearRange) {
        this.soundClass = soundClass;
        this.musicClass = musicClass;

        soundDurationMap = new EnumMap<>(soundClass);
        musicDurationMap = new EnumMap<>(musicClass);

        setHearRange(hearRange);
        loadPreferences();
    }

    public void savePreferences() {
        preferences.putFloat(MASTER_VOLUME_PREF, masterVolume);
        preferences.putFloat(SOUND_VOLUME_PREF, soundVolume);
        preferences.putFloat(MUSIC_VOLUME_PREF, musicVolume);
        preferences.flush();
    }

    public void loadPreferences() {
        masterVolume = preferences.getFloat(MASTER_VOLUME_PREF, FULL_VOLUME_RANGE[1]);
        soundVolume = preferences.getFloat(SOUND_VOLUME_PREF, FULL_VOLUME_RANGE[1]);
        musicVolume = preferences.getFloat(MUSIC_VOLUME_PREF, FULL_VOLUME_RANGE[1]);

        currentSoundVolume = masterVolume * soundVolume;
        currentMusicVolume = masterVolume * musicVolume;
    }

    public long playSound(S sound) {
        return sound.get().play(currentSoundVolume);
    }

    public long playSound(S sound, float volume) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        return sound.get().play(currentSoundVolume * volume);
    }

    public long playSound(S sound, float volume, float pitch) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        return sound.get().play(currentSoundVolume * volume, pitch, DEFAULT_PAN);
    }

    public long playSound(S sound, float volume, float pitch, float pan) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        pan = MathUtils.clamp(pan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);
        return sound.get().play(currentSoundVolume * volume, pitch, pan);
    }

    public void setSoundListenerPosition(Vector2 position) {
        soundListenerPosition.set(position);
    }

    public void setHearRange(float hearRange) {
        this.hearRange = Math.abs(hearRange);
    }

    public long playSoundAt(Vector2 position, S sound) {
        return playSoundAt(position, sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH);
    }

    public long playSoundAt(Vector2 position, S sound, float volume) {
        return playSoundAt(position, sound, volume, DEFAULT_PITCH);
    }

    public long playSoundAt(Vector2 position, S sound, float volume, float pitch) {
        float pos2 = soundListenerPosition.dst2(position);
        float hearRange2 = hearRange * hearRange;

        if (pos2 <= hearRange2) {
            float distance = (float) Math.sqrt(pos2);
            volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
            return playSound(sound, (FULL_VOLUME_RANGE[1] - (distance / hearRange)) * volume, pitch,
                    (position.x - soundListenerPosition.x) / (hearRange / 2f));
        }
        return INVALID_SOUND_ID;
    }

    public void pauseSound(S sound) {
        sound.get().pause();
    }

    public void pauseSound(S sound, long soundId) {
        sound.get().pause(soundId);
    }

    public void resumeSound(S sound) {
        sound.get().resume();
    }

    public void resumeSound(S sound, long soundId) {
        sound.get().resume(soundId);
    }

    public void stopSound(S sound) {
        sound.get().stop();
    }

    public void stopSound(S sound, long soundId) {
        sound.get().stop(soundId);
    }

    public void playMusic(M music, boolean loop) {
        Music track = music.get();
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

    public float getSoundDuration(S sound) {
        return soundDurationMap.get(sound);
    }

    public Map<S, Float> getSoundDurationMap() {
        return soundDurationMap;
    }

    public float getMusicDuration(M music) {
        return musicDurationMap.get(music);
    }

    public Map<M, Float> getMusicDurationMap() {
        return musicDurationMap;
    }

    public Vector2 getSoundListenerPosition() {
        return soundListenerPosition;
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

    public Preferences getPreferences() {
        return preferences;
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
            sound.loadToAssets(assets);
            soundDurationMap.put(sound, sound.duration());
        }
        for (M music : musicClass.getEnumConstants()) {
            music.loadToAssets(assets);
            musicDurationMap.put(music, music.duration());
        }
    }

    @Override
    public void loadFromAssets(AssetManager assets) {
        for (S sound : soundClass.getEnumConstants()) {
            sound.loadFromAssets(assets);
        }
        for (M music : musicClass.getEnumConstants()) {
            music.loadFromAssets(assets);
        }
    }
}
