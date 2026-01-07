package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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

    public final SoundInstance<S> nullSoundInstance = new SoundInstance<>(null,
            FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[0], DEFAULT_PITCH, DEFAULT_PAN, false,
            INVALID_SOUND_ID, 0f);

    private final Preferences preferences = Gdx.app.getPreferences(getClass().getCanonicalName());

    // Used for loading to/from assets
    private final Class<S> soundClass;
    private final Class<M> musicClass;
    private final Map<S, Float> soundDurationMap;
    private final Map<M, Float> musicDurationMap;

    // Unordered with the default Array capacity (for faster removal performance)
    private final Array<SoundInstance<S>> activeSoundInstances = new Array<>(false, 16);
    private final Array<SoundInstance<S>> activePositionalSoundInstances = new Array<>(false, 16);

    private final Vector2 soundListenerPosition = new Vector2();
    private float hearRange;

    private float masterVolume = FULL_VOLUME_RANGE[1];
    private float soundVolume = FULL_VOLUME_RANGE[1];
    private float musicVolume = FULL_VOLUME_RANGE[1];

    // Cached Values
    private float currentSoundVolume = FULL_VOLUME_RANGE[1];
    private float currentMusicVolume = FULL_VOLUME_RANGE[1];

    public AudioSystem(Class<S> soundClass, Class<M> musicClass, float hearRange) {
        this.soundClass = soundClass;
        this.musicClass = musicClass;

        this.soundDurationMap = new EnumMap<>(soundClass);
        this.musicDurationMap = new EnumMap<>(musicClass);

        this.hearRange = Math.abs(hearRange);
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

        setSoundVolume(soundVolume);
        setMusicVolume(musicVolume);
    }

    /** MUST BE CALLED FOR PROPER SOUND HANDLING */
    public void postRender() {
        for (int i = activeSoundInstances.size - 1; i > -1; i--) {
            SoundInstance<S> instance = activeSoundInstances.get(i);
            instance.update();

            if (instance.isFinished()) {
                activeSoundInstances.removeIndex(i);

                // Handle positional sound (making sure to deal with unordered array removal)
                if (instance.positionArrayIndex != -1) {
                    activePositionalSoundInstances.get(activePositionalSoundInstances.size - 1)
                            .positionArrayIndex = instance.positionArrayIndex;
                    activePositionalSoundInstances.removeIndex(instance.positionArrayIndex);
                    instance.positionArrayIndex = -1;
                }
            }
        }

        for (SoundInstance<S> instance : activePositionalSoundInstances) {
            if (instance.positionUpdated) {
                updatePositionalSoundInstance(instance);
            }
        }
    }

    public SoundInstance<S> playSound(S sound) {
        return playSound(sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH, DEFAULT_PAN, false);
    }

    public SoundInstance<S> playSound(S sound, boolean loop) {
        return playSound(sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH, DEFAULT_PAN, loop);
    }

    public SoundInstance<S> playSound(S sound, float volume) {
        return playSound(sound, volume, DEFAULT_PITCH, DEFAULT_PAN, false);
    }

    public SoundInstance<S> playSound(S sound, float volume, boolean loop) {
        return playSound(sound, volume, DEFAULT_PITCH, DEFAULT_PAN, loop);
    }

    public SoundInstance<S> playSound(S sound, float volume, float pitch) {
        return playSound(sound, volume, pitch, DEFAULT_PAN, false);
    }

    public SoundInstance<S> playSound(S sound, float volume, float pitch, boolean loop) {
        return playSound(sound, volume, pitch, DEFAULT_PAN, loop);
    }

    public SoundInstance<S> playSound(S sound, float volume, float pitch, float pan) {
        return playSound(sound, volume, pitch, pan, false);
    }

    public SoundInstance<S> playSound(S sound, float volume, float pitch, float pan, boolean loop) {
        volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
        pan = MathUtils.clamp(pan, FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);

        long id = loop ? sound.get().loop(currentSoundVolume * volume, pitch, pan) :
                sound.get().play(currentSoundVolume * volume, pitch, pan);
        SoundInstance<S> instance =
                new SoundInstance<>(sound, volume, currentSoundVolume, pitch, pan, loop, id,
                        getSoundDuration(sound));
        activeSoundInstances.add(instance);

        return instance;
    }

    public void setSoundListenerPosition(Vector2 position) {
        if (position != null) {
            setSoundListenerPosition(position.x, position.y);
        }
    }

    public void setSoundListenerPosition(float x, float y) {
        if (!soundListenerPosition.epsilonEquals(x, y)) {
            soundListenerPosition.set(x, y);
            updateAllPositionalSoundInstances();
        }
    }

    public void setHearRange(float newRange) {
        newRange = Math.abs(newRange);

        if (hearRange != newRange) {
            hearRange = newRange;
            updateAllPositionalSoundInstances();
        }
    }

    /** AUTOMATICALLY CALLED IN UPDATE, BUT CAN CALL MANUALLY WHEN "NECESSARY" */
    public void updateAllPositionalSoundInstances() {
        for (SoundInstance<S> instance : activePositionalSoundInstances) {
            updatePositionalSoundInstance(instance);
        }
    }

    /** AUTOMATICALLY CALLED IN UPDATE, BUT CAN CALL MANUALLY WHEN "NECESSARY" */
    public void updatePositionalSoundInstance(SoundInstance<S> instance) {
        if (instance.positionArrayIndex == -1 || instance.getPosition() == null) {
            return;
        }

        Vector2 position = instance.getPosition();
        float pos2 = soundListenerPosition.dst2(position);
        float hearRange2 = hearRange * hearRange;
        if (pos2 <= hearRange2) {
            float distance = (float) Math.sqrt(pos2);
            float positionalVolume = FULL_VOLUME_RANGE[1] - (distance / hearRange);
            float pan = (position.x - soundListenerPosition.x) / (hearRange / 2f);
            instance.setPositionalVolume(positionalVolume, pan);
        } else {
            // Mute if outside of range
            instance.setPositionalVolume(FULL_VOLUME_RANGE[0]);
        }
        // The position has now been updated, get rid of flag
        instance.positionUpdated = false;
    }

    public SoundInstance<S> playSoundAt(Vector2 position, S sound) {
        return playSoundAt(position, sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH, false);
    }

    public SoundInstance<S> playSoundAt(Vector2 position, S sound, boolean loop) {
        return playSoundAt(position, sound, FULL_VOLUME_RANGE[1], DEFAULT_PITCH, loop);
    }

    public SoundInstance<S> playSoundAt(Vector2 position, S sound, float volume) {
        return playSoundAt(position, sound, volume, DEFAULT_PITCH, false);
    }

    public SoundInstance<S> playSoundAt(Vector2 position, S sound, float volume, boolean loop) {
        return playSoundAt(position, sound, volume, DEFAULT_PITCH, loop);
    }

    public SoundInstance<S> playSoundAt(Vector2 position, S sound, float volume, float pitch) {
        return playSoundAt(position, sound, volume, pitch, false);
    }

    /** CAN RETURN A NULL_SOUND_INSTANCE (IF NOT IN HEAR_RANGE). */
    public SoundInstance<S> playSoundAt(Vector2 position, S sound, float volume, float pitch,
                                        boolean loop) {
        float pos2 = soundListenerPosition.dst2(position);
        float hearRange2 = hearRange * hearRange;

        if (pos2 <= hearRange2) {
            volume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
            pitch = MathUtils.clamp(pitch, FULL_PITCH_RANGE[0], FULL_PITCH_RANGE[1]);
            float pan = MathUtils.clamp((position.x - soundListenerPosition.x) / (hearRange / 2f),
                    FULL_PAN_RANGE[0], FULL_PAN_RANGE[1]);

            float distance = (float) Math.sqrt(pos2);
            float positionalVolume = Math.max(FULL_VOLUME_RANGE[0],
                    (FULL_VOLUME_RANGE[1] - (distance / hearRange)));
            float totalVolume = volume * positionalVolume * currentSoundVolume;

            long id = loop ? sound.get().loop(totalVolume, pitch, pan) :
                    sound.get().play(totalVolume, pitch, pan);

            SoundInstance<S> instance =
                    new SoundInstance<>(sound, volume, currentSoundVolume, pitch, pan, loop, id,
                            getSoundDuration(sound));
            instance.setPositionalVolume(positionalVolume);
            instance.setPosition(position);
            instance.positionArrayIndex = activePositionalSoundInstances.size;

            activeSoundInstances.add(instance);
            activePositionalSoundInstances.add(instance);
            return instance;
        }

        // Avoids creating new "null" instances (good for GC)
        return nullSoundInstance;
    }

    public void pauseAllSounds() {
        for (SoundInstance<S> s : activeSoundInstances) {
            s.pause();
        }
    }

    public void resumeAllSounds() {
        for (SoundInstance<S> s : activeSoundInstances) {
            s.resume();
        }
    }

    public void stopAllSounds() {
        for (SoundInstance<S> s : activeSoundInstances) {
            s.stop();
            s.positionArrayIndex = -1;
        }
        activeSoundInstances.clear();
        activePositionalSoundInstances.clear();
    }

    // TODO: More robust music system
    public Music playMusic(M music, boolean loop) {
        Music track = music.get();
        track.setLooping(loop);
        track.setVolume(currentMusicVolume);
        track.play();
        return track;
    }

    public void setMasterVolume(float volume) {
        masterVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentSoundVolume = masterVolume * soundVolume;
        currentMusicVolume = masterVolume * musicVolume;

        for (SoundInstance<S> instance : activeSoundInstances) {
            instance.setMasterVolume(currentSoundVolume);
        }
        for (M asset : musicClass.getEnumConstants()) {
            asset.get().setVolume(currentMusicVolume);
        }
    }

    public void setSoundVolume(float volume) {
        soundVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentSoundVolume = masterVolume * soundVolume;
        for (SoundInstance<S> instance : activeSoundInstances) {
            instance.setMasterVolume(currentSoundVolume);
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = MathUtils.clamp(volume, FULL_VOLUME_RANGE[0], FULL_VOLUME_RANGE[1]);
        currentMusicVolume = masterVolume * musicVolume;
        for (M asset : musicClass.getEnumConstants()) {
            asset.get().setVolume(currentMusicVolume);
        }
    }

    /** DO NOT ALTER, GETTER ONLY */
    public Array<SoundInstance<S>> getActiveSoundInstances() {
        return activeSoundInstances;
    }

    /** DO NOT ALTER, GETTER ONLY */
    public Array<SoundInstance<S>> getActivePositionalSoundInstances() {
        return activePositionalSoundInstances;
    }

    public float getSoundDuration(S sound) {
        return soundDurationMap.get(sound);
    }

    /** DO NOT ALTER, GETTER ONLY */
    public Map<S, Float> getSoundDurationMap() {
        return soundDurationMap;
    }

    public float getMusicDuration(M music) {
        return musicDurationMap.get(music);
    }

    /** DO NOT ALTER, GETTER ONLY */
    public Map<M, Float> getMusicDurationMap() {
        return musicDurationMap;
    }

    /** DO NOT ALTER, GETTER ONLY */
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
