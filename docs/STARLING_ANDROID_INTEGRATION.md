# Vyron — Starling Android integration

## Current integration boundary

Vyron now packages the pinned Starling Android host sources and the ARM64
`libhorizon_android.so` into the same APK. The native entry point is
`starling_on_create`; Android loads it through `NativeActivity` metadata.
The host sources come from the pinned `engine/starling` submodule revision
`2d007302e0d2acf64c96b45618738d8d1cfa69b7`.

The launch flow starts `io.github.juanluci.starling.StarlingActivity`.
Starling owns Microsoft sign-in and first-run game-resource preparation.
It targets Minecraft Java Edition 26.3. The game itself executes in Rust,
without a persistent Java game runtime; Starling may fetch a temporary Java
runtime for resource preparation and remove it afterwards.

## Process isolation

`StarlingActivity` runs in the app's `:game` process. Starling intentionally
terminates its game process when its NativeActivity is destroyed because its
native event loop cannot be restarted in the same process. Vyron's launcher
stays in the default process and remains underneath the game. Starling's
`LauncherActivity` runs separately for imports and engine restarts.

## Touch controls

The Zalith `LayerController` source and editor remain in the project, but this
first native-engine integration does not yet route LayerController events into
Starling's winit input queue. Starling's own native touch controls are used by
the Rust game for now. Do not mark LayerController-to-Rust input bridging as
complete until pointer, key, joystick, pause/resume and editor changes are
tested end to end.

## Mod formats

The pinned Starling source includes its native/content mod pipeline. Verify
`.hmod` and `.hnmod` imports, compilation, trust warnings and ARM64 loading
on device before advertising both formats as supported by Vyron.

## Build

The ARM64 workflow checks out the submodule, builds the `horizon-android`
crate for `aarch64-linux-android`, stages the shared library under
`ZalithLauncher/src/main/jniLibs/arm64-v8a`, then assembles the APK.
