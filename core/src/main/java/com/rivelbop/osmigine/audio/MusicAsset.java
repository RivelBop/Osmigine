package com.rivelbop.osmigine.audio;

import com.badlogic.gdx.audio.Music;
import com.rivelbop.osmigine.assets.Asset;

public interface MusicAsset extends Asset {
    /** @return The stored music. */
    Music get();
}
