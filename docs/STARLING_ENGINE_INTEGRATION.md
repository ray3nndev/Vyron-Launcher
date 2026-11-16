# Starling engine integration

## Pinned engine source

Vyron tracks the Android-native Starling port as a Git submodule at
`engine/starling`. The gitlink pins an exact upstream commit; the
`.gitmodules` branch setting is only the update target, not a floating build
dependency. Update the pin deliberately after reviewing upstream changes.

Starling is the Android port of Horizon. Its current `starling` branch documents
both Horizon content mods (`.hmod`) and source-native mods (`.hnmod`).
The older `android` branch is historical and must not be used as the engine
source.

## First integration milestone

The `Vyron Starling Android engine` workflow compiles the upstream
`horizon-android` Rust crate for Android ARM64 and publishes
`libhorizon_android.so` as a short-lived CI artifact. This verifies that
Vyron can build the actual Android game engine independently of the legacy
Zalith Java-game backend.

This milestone is deliberately separate from the launcher APK. The native
library is **not yet wired into Vyron's Android manifest, Activity lifecycle,
resource preparation, native-mod build service, or Play button**. Do not
describe the game as launchable from Vyron until those pieces are integrated
and an end-to-end device test succeeds.

## Runtime integration still required

1. Add Starling's Android Java bridge/Activity and NativeActivity manifest
   declarations without replacing Vyron's launcher entry Activity.
2. Package the ARM64 `libhorizon_android.so`, Starling's Java launcher helpers,
   C++ runtime dependencies and required assets into Vyron's APK.
3. Connect Vyron's Rust Version / Play action to Starling's resource preparation
   and game Activity lifecycle.
4. Preserve Zalith's LayerController as a separate input adapter only after
   mapping its key/touch events to Starling's input API; Starling currently has
   its own editable touch-control system.
5. Port the `.hmod` import/enable/disable flow and `.hnmod` compile/download/
   library-swap flow, including native-code trust prompts and permission
   handling. Starling's native-mod pipeline uses a remote build service; it is
   not simply an on-device Rust compiler.
6. Remove the legacy Java Minecraft runtime and LWJGL game backend only after
   the Starling path launches and the remaining Android launcher dependencies
   are proven independent.

## Build environment

The engine workflow follows Starling's documented Android toolchain:
Rust 1.95.0, Android NDK 28.2.13676358, Android platform 36, build-tools
35.0.0, JDK 25 and cargo-ndk 3.5.4. The output is an ARM64 shared library,
not a complete APK.
