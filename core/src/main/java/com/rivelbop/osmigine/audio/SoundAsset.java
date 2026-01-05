package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Sound;
import com.rivelbop.osmigine.assets.Asset;

public interface SoundAsset extends Asset {
    /** @return The duration of the sound effect. */
    float duration();

    /** @return The stored sound. */
    Sound get();
}
