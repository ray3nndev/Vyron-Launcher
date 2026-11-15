# Upstream integration audit

Audit date: 2026-11-15

## Repositories inspected

- Zalith Launcher 2: https://github.com/ZalithLauncher/ZalithLauncher2
- Horizon: https://github.com/horizon-rust/horizon

## Exact Zalith revision inspected

- Branch: `main`
- Commit: `36b8ef87f3ce498c2865b483e3b3a0096acbefc7`

This is a point-in-time audit. Re-check upstream before importing because the branch can move.

## Confirmed architecture

Zalith Launcher 2 is an Android application built around Jetpack Compose and Material 3. Its Gradle project includes the main `ZalithLauncher` app and reusable modules including `LayerController`, `InputMap`, `CardGrid`, `ColorPicker`, `Terracotta`, `Guide`, and `PetLib`. Its current launcher/game pipeline is coupled to Java Edition installation, version manifests, libraries, mods, and Java game launch.

The LayerController module is an Android library using Compose. The inspected build file sets minSdk 26 and JVM target 11, and depends on the separate InputMap module.

## LayerController findings

Inspected:
- `LayerController/src/main/java/com/movtery/layer_controller/ControlEditor.kt`
- `LayerController/src/main/java/com/movtery/layer_controller/observable/TouchProcessor.kt`

The editor is not merely a static overlay. It renders editable control layouts and layers, observes layout/style state, and supports selection, snapping, sizing and positioning behavior. TouchProcessor performs per-frame pointer hit-testing, tracks active widgets per pointer, routes events to controls, and handles out-of-bounds release and swipe chains.

**Migration rule:** import the LayerController and its full dependency closure intact first. Do not rewrite it or replace its event model during the UI port. Add a separate engine-facing input adapter after the original behavior is running in Vyron.

## Game runtime separation

Inspected `GameLaunchFlow.kt`. The existing launch flow is not a single JVM call: it coordinates account validation/refresh, installed version state, mod scanning, manifest construction, library downloads and launch phases.

Therefore, do not delete the whole launch package or all Java-related code. Preserve account, networking, storage, UI task/progress and reusable download infrastructure where compatible. Replace only the Java game execution path and components whose sole purpose is preparing/running that JVM game process. Each dependency must be classified before removal.

Horizon resource preparation currently has a separate JDK requirement for Mojang-code exporters. Removing the Android in-process game JVM does not imply that every JDK-related preparation step can be deleted.

## Horizon Android readiness

The inspected native-client documentation describes a playable Rust client, but the current supported run/build instructions are desktop-oriented. Horizon's roadmap states that an official Android build is not yet available. Android input helper state in the Rust source is not, by itself, an Android application runtime, surface, renderer, packaging or touch-control integration.

**Blocker:** Vyron must first prove an Android ARM64 native build and lifecycle/surface integration. Do not claim a working Android engine until an APK loads the native library and a device test renders a world.

## Licensing and branding gate

Zalith Launcher 2 is GPL-3.0 and its README specifies additional conditions under GPLv3 section 7, including distinct naming/versioning, a clear non-official-modification notice in the startup or main screen, and retention of copyright notices. Vyron is a distinct name. Before distribution, include the complete upstream license and notices, preserve per-file notices, audit all bundled modules and dependencies, and review the additional terms with qualified counsel if needed.

Do not copy upstream signing keystores, secrets, OAuth credentials or release configuration.

## Import plan

1. Import the complete upstream source snapshot at the audited commit into this branch, preserving its license and notices.
2. Exclude signing keys, local secrets, generated build output and private resource files.
3. Rebrand application identifiers, display name, icons and user-facing official-project references for Vyron; add the required non-official modification notice.
4. Make the unmodified upstream project build before changing its runtime path.
5. Build and test the LayerController editor and overlay unchanged.
6. Trace launch dependencies and draw a keep/replace/remove matrix before deleting runtime code.
7. Introduce a narrow `EngineAdapter` boundary and an Android ARM64 Horizon proof of concept.
8. Connect LayerController events to the engine only after native lifecycle and input semantics are verified.

## Current implementation status

This branch currently contains the integration work plan only. The upstream Android source has **not** yet been imported, no app module exists in this repository, and no Vyron APK has been built or tested.
