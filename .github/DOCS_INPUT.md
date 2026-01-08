# Input Documentation

The following is documentation for the `com.rivelbop.osmigine.input` package.

## Overview

The entire purpose of the `input` package was to create a simple and robust high-level overhead for
libGDX's `InputProcessor` and `ControllerListener`.

These tools allow for more precise input-handling compared to the typical `Gdx` input-polling (e.g. 
`Gdx.input.isKeyPressed(Input.Keys.A)`).

## Classes (Alphabetical Order)

### ControllerProcessor.java

This is the class that directly implements libGDX's `ControllerListener`. Its purpose is to get the
raw inputs from a controller and store it into a `ControllerState`. By doing this, we have much
easier access to each controller's input data (e.g. buttons pressed/just-pressed, axis). With this
input data we pass it into the `ControllerSystem` which handles the actual high-level code that the
user interacts with.  

You may use this class when creating your own custom system for controller input, or you may choose
to ignore it entirely and stick with the provided `ControllerSystem`. When creating your own system,
keep in mind that this listener acts as a "global" listener of sorts, it is meant to handle and
store the data for MULTIPLE controllers, NOT just ONE. It also features "auto-handling" for
resetting the justPressed booleans for each controller's buttons, which uses `Gdx.app.postRunnable`
and should generally be avoided in favor for explicit calls to the dedicated `postRender` method.

### ControllerState.java

This class is used to store the state of an individual controller. This includes things like the
`Controller` instance itself, an array of `InputEvent` objects each representing the state of each
button on the controller, and an array with the value of each axis on the controller (e.g. 
joysticks, triggers, etc.). With this we can allow the `ControllerProcessor` to store the raw input
data for each connected controller and eventually pass the data to the user.

### ControllerSystem.java

This serves as the high-level code the user should directly interact with. Its purpose is to act as
a simplified wrapper over the `ControllerProcessor` and provide easy access to the most vital
controller input methods for faster development. You can check the state of each button by using the
`isPressed`, `isJustPressed`, and `isJustPressedOnTick` methods, which allow the user to pass the
actual button index from their controller's mappings, or provide a simplified `Mapping` enum which 
libGDX likely has index mappings for. The values for each axis are accessible through the various
`getAxisValue` methods.

This is a very strict wrapper over the `ControllerProcessor` and should only really serve as a base
or example for a potential system you may want to make for yourself. If you are using the class
directly, MAKE SURE TO CALL THE `postRender` AND `postTick` (if necessary) METHODS SO JUST_PRESSED
BUTTON INPUT EVENTS ARE UPDATED! These methods should be called at the very end of your `render`
method. NOTE: If you are using the `SceneManager`, a `ControllerSystem` is already provided to you
and automatically calls the necessary `postRender` method for you.

### CursorProvider.java

This interface is currently used as a cross-platform bridge for providing precise cursor positions
from various platforms. At the moment it only provides it for Desktop/LWJGL3 platforms by using the
GLFW backend in the `GlfwCursorProvider` class. This is ideal for games where polling accurate
floating-point cursor positions is necessary, such as competitive FPS games or even point-and-click
games. The default libGDX cursor position getters are very lackluster and only provide the position
as a whole number.

If you are interested in cursor-precision for platforms other than Desktops, you will need to look
into the backend for those platforms and poll the cursor position through other means. Make sure to
have a look at the `lwjgl3` module's `Lwjgl3Launcher` and `GlfwCursorProvider`, along with the
`Osmigine` class for an example implementation of how to do so. For users simply interested in
the pre-built provider, the example in `GlfwCursorProvider` and `Osmigine` should cover your Desktop
needs.

### InputEvent.java

This is the common class used to store the input events for keys, mouse buttons, touches, and
controller buttons. When polling raw inputs from the `ControllerProcessor` or
`InputSystemProcessor`, each input's index is mapped to an instance of this class to allow easier
accessibility for high-level access.

Instances of this class should only serve as accessors for the data they store within them. The user
should never change the values, as the system itself will handle updating the state.

### InputMap.java

This class serves as an ObjectMap to map generic-type keys to `InputTrigger` instances, allowing the
user to resemble inputs as a key of their choice (e.g. `String`, `enum`, etc.), instead of ID
values. It uses both an `InputSystem` and `ControllerSystem` to pass into the mapped `InputTrigger`
instances, to check if they are activated for the remappable indices mapped to them.

Instead of directly using an `InputSystem` or `ControllerSystem`, users should use an `InputMap` to
make their games easier to maintain and more accessible to more platforms, as you can map an
`InputTrigger` to a key and have that activate on a key, button, touch, or controller input event.
You can then provide the player with the freedom to remap those keys when necessary.

### InputSystem.java

This serves as the high-level code the user should directly interact with. Its purpose is to act as
a simplified wrapper over the `InputSystemProcessor` and provide easy access to the most vital input
methods for faster development. You can check the state of each key, mouse button, or touch by using
the `isPressed`, `isJustPressed`, and `isJustPressedOnTick` methods. The screen and world positions
for the cursor and touches are accessible via their respective position accessors.

This is a very strict wrapper over the `InputSystemProcessor` and should only really serve as a base
or example for a potential system you may want to make for yourself. If you are using the class
directly, MAKE SURE TO CALL THE `postRender` AND `postTick` (if necessary) METHODS SO JUST_PRESSED
INPUT EVENTS ARE UPDATED! These methods should be called at the very end of your `render` method.
NOTE: If you are using the `SceneManager`, an `InputSystem` is already provided to you and 
automatically calls the necessary `postRender` method for you.

### InputSystemProcessor.java

This is the class that directly implements libGDX's `InputProcessor`. Its purpose is to get the raw
inputs from a keyboard, mouse, or touchscreen and store it into `InputEvents`. By doing this, we
have much easier access to each device's input data (e.g. pressed/just-pressed, positions). With
this input data we pass it into the `InputSystem` which handles the actual high-level code that the
user interacts with.

You may use this class when creating your own custom system for input, or you may choose to ignore 
it entirely and stick with the provided `InputSystem`. When creating your own system, it features
"auto-handling" for auto-resetting the justPressed booleans for each input event, which uses
`Gdx.app.postRunnable` and should generally be avoided in favor for explicit calls to the dedicated
`postRender` method.

### InputTrigger.java

This serves as the middle-man for the `InputMap`, as it actually checks whether the state of the key
you call for is actually active. It stores the IDs for each input event you are trying to map for
(e.g. keys, mouse buttons, controller buttons, axis') and allows the user to specify what triggers
activity from those "mapped" IDs (e.g. when they are pressed or justPressed, etc.). The IDs are not
final and can be remapped.

When making your own input system, this may feel quite lackluster as it doesn't feature different
"input-combos" (multiple different presses combined to trigger activity). If you plan to use the
class as it is, keep in mind that there are pre-defined `Pressed`, `JustPressed`, and
`JustPressedOnTick` classes that cover the simple use-case for you and can even be used as an
example/base for an `InputTrigger` implementation of your own.

### Mapping.java

This enum class simply maps the pre-defined controller mappings from libGDX into enums for
ease-of-use when calling high-level methods from the `ControllerSystem`. It takes in a `Controller`
to grab the mappings from and asks the user to provide one of the pre-defined enums, returning its
mapped ID/index.

This is useful for anyone that is making a system of their own and doesn't want to deal with the
libGDX controller mappings directly (which can be kind of tedious). For casual users that want to
use this class as it is, it should only really be passed through the `ControllerSystem` or the
`InputMap`.