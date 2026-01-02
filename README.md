# Osmigine
 A game engine built using [libGDX](https://github.com/libgdx/libgdx).

## Generate New Project
The following is to be done with the [gdx-liftoff](https://github.com/libgdx/gdx-liftoff) tool.  
NOTE: On Linux run gdx-liftoff by calling `__GL_THREADED_OPTIMIZATIONS=0 java -jar gdx-liftoff.jar`!  

### ADD-ONS

#### PLATFORMS
* CORE
* DESKTOP
* ANDROID
* SERVER
* SHARED

#### EXTENSIONS
* Controllers
* Freetype

### THIRD-PARTY
* KryoNet
* ScreenManager
* ShapeDrawer
* SteamWorks4J (EXTERNAL)

### SETTINGS
* LIBGDX VERSION: 1.14.0 or latest
* JAVA VERSION: 11

## Initial Project Setup
Once the new project is generated, make sure to do the following:
* Make sure to use JDK-17 (Eclipse Temurin, etc.) with language level 11.
* For SteamWorks4J:
  1. Add `steamworks4jVersion=1.10.0` to `gradle.properties`.
  2. Inside the lwjgl3 `build.gradle`, add the following dependencies:
     ```
     implementation "com.code-disaster.steamworks4j:steamworks4j:$steamworks4jVersion"
     implementation "com.code-disaster.steamworks4j:steamworks4j-gdx:$steamworks4jVersion"
     implementation "com.code-disaster.steamworks4j:steamworks4j-server:$steamworks4jVersion"
     ```
  3. Inside the server `build.gradle`, add the following dependencies:  
     ```
     implementation "com.code-disaster.steamworks4j:steamworks4j:$steamworks4jVersion"
     implementation "com.code-disaster.steamworks4j:steamworks4j-server:$steamworks4jVersion"
     ```
* For KryoNet:
  1. Cut `api "com.github.crykn:kryonet:$kryoNetVersion"` from the core `build.gradle` dependencies.
  2. Paste `api "com.github.crykn:kryonet:$kryoNetVersion"` into the shared `build.gradle`.
* To fix the `Error: Module java.base/sun.nio.ch not found` error when using Construo to package 
your game, make sure to:
  1. At the top of `build.gradle` in lwjgl3, under the `buildscript` block, inside the `plugins`
  block, add `id "io.github.fourlastor.construo" version "YOUR VERSION HERE"`.
  2. Scroll down to the construo block, inside it, at the very top add:
     ```
     jlink {
         // add arbitrary modules to be included when running jlink
         modules.addAll("jdk.zipfs", "jdk.unsupported", "java.management", "java.logging")
         // guess the modules from the jar using jdeps, defaults to true
         guessModulesFromJar.set(false)
         // include default crypto modules, defaults to true
         includeDefaultCryptoModules.set(true)
     }
     ```
* Remove `configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);`
  from `Lwjgl3Launcher.java` to prevent `EGL: Failed to clear current context` error on Linux.