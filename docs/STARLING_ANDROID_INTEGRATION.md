# Vyron — Horizon/Starling Android integration

## Product direction

Vyron owns the Android launcher experience: versions, installation state, selected build,
account flow, and the Play action. Zalith's launcher UI and LayerController editor remain
the front end. The Starling/Horizon Rust client is the game execution backend.

The current supported game target in the pinned Starling integration is Minecraft Java
Edition 26.3. Do not advertise other Minecraft versions until the Android engine supports
and has been tested with them.

## Important distinction: PC Horizon vs Android Starling

Horizon PC's launcher manages Horizon game builds for desktop platforms. Its published
release assets are Linux, macOS, and Windows binaries; they are not Android ARM64 engine
downloads. Starling publishes Android APKs, which package the Android host bridge and
the Rust `libhorizon_android.so` together.

Therefore Vyron must not point an Android version manager at the PC Horizon release assets.
An Android engine build needs to be built for Android ARM64 and distributed with a matching
Starling Android host/JNI bridge.

## Current integration boundary

The APK packages the pinned Starling Android host sources and ARM64
`libhorizon_android.so`. The native entry point is `starling_on_create`, loaded through
Android `NativeActivity` metadata. The host sources come from the pinned
`engine/starling` submodule revision
`2d007302e0d2acf64c96b45618738d8d1cfa69b7`.

The current Play flow starts `io.github.juanluci.starling.StarlingActivity` directly.
Starling currently owns first-run account/resource preparation. This is not yet the
requested Vyron-managed install/select/play workflow, and the setup experience has not
been removed.

Starling's `NativeMods` loader can select an external `libhorizon_android.so` from
app-private storage instead of the APK's bundled library. Its current compatibility marker
is tied to the APK `versionName`, however, not to a Minecraft version or a Vyron engine
build identifier. Do not repurpose it as a user-facing version manager without adding
explicit build metadata, integrity verification, rollback, and compatibility checks.

## Version-management target

The intended Vyron flow is:

1. Show the available Android Horizon/Rust engine build for Minecraft 26.3.
2. Download and verify an Android ARM64 engine build from a Vyron-controlled distribution
   source. Do not download a desktop Horizon binary or an unrelated Starling APK.
3. Install it atomically into app-private storage, retain build metadata, and allow the user
   to select the installed build.
4. Play launches the selected installed build directly, without Starling's setup/onboarding
   flow. Account authentication and Minecraft resource preparation must be handled by
   Vyron or a reusable non-UI backend API before the native game starts.
5. Preserve a known-good bundled engine as recovery fallback until the external build has
   passed its first-frame startup check.

The standalone engine artifact produced by CI is an engineering/build artifact only. It is
not yet an in-app download channel, and the Play flow must not claim that installation or
selection is implemented until the manager and native launch hand-off are complete.

## Touch controls

The Zalith `LayerController` source and editor remain in the project, but this integration
does not yet route LayerController events into Starling's winit input queue. Do not mark
LayerController-to-Rust input bridging complete until pointer, key, joystick, pause/resume,
and editor changes are tested end to end.

## Native mods and add-on platform

Do not create a separate "Native Mods" page. The existing Mods / Shader Pack / Resource Pack
Target Platform selector should retain Modrinth and CurseForge and add Native as the default
target only after native package search/install semantics are defined. Ordinary Java
`.jar` mods must not be presented as compatible native modules. Verify `.hmod` and
`.hnmod` imports, compilation, trust warnings, and ARM64 loading on device before
advertising support for both.

## CI

The ARM64 workflow builds the `horizon-android` crate, stages
`libhorizon_android.so` into the APK, and now also uploads that library as a separate
30-day Actions artifact for inspection and future Android-engine distribution work.
The APK artifact remains separate.
