package com.rivelbop.osmigine.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.rivelbop.osmigine.Osmigine;

import org.jaudiotagger.tag.TagOptionSingleton;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONAL - CALLED BY DURATION METHOD ANYWAY
        TagOptionSingleton.getInstance().setAndroid(true);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new Osmigine(), configuration);
    }
}
