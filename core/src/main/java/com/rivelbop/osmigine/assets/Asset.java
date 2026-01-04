package com.rivelbop.osmigine.assets;

import com.badlogic.gdx.assets.AssetManager;

public interface Asset {
    /** CALL WHEN LOADING INTO THE ASSET MANAGER */
    void loadToAssets(AssetManager assets);

    /** CALL ONCE ASSET MANAGER HAS FINISHED LOADING ALL ASSETS */
    void loadFromAssets(AssetManager assets);
}
