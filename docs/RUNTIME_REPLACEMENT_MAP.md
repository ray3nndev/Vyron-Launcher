# Vyron Runtime Replacement Map

**Scope:** replace Minecraft's Java game execution backend with Horizon's native Rust engine while retaining the Zalith-derived Android launcher UI and touch-control editor.

**Source baseline:** ZalithLauncher2 commit `36b8ef87f3ce498c2865b483e3b3a0096acbefc7`, imported into this branch. This document is a source-level migration map, not an implementation claim.

## Keep as launcher infrastructure

| Source area | Decision | Reason |
|---|---|---|
| `game/launch/GameLaunchFlow.kt` | Keep, then adapt | Account checks, version state, downloads, manifest preparation and launch phases are launcher responsibilities. Do not delete the whole flow. |
| `viewmodel/LaunchGameViewModel.kt` | Keep, then adapt | Owns launch state and UI operations; route it through the engine adapter rather than directly to the JVM. |
| `ui/screens/main/MainScreen.kt` and navigation | Keep | Main launcher UI and navigation shell. |
| `ui/screens/game/` | Keep the UI shell; adapt lifecycle | Retain game overlay, logs, menu and screen transitions. Remove JVM-specific naming only after behavior is understood. |
| `LayerController/`, `InputMap/`, `game/control/` | Keep intact initially | Preserve the editor, control layouts, pointer tracking, key mapping and user configurations. |
| `game/account/`, version/download/asset preparation | Keep, audit per feature | Authentication and asset preparation are not the Minecraft execution engine. |

## Replace or retire after native parity exists

| Source area | Decision | Dependency / caution |
|---|---|---|
| `game/launch/JvmLauncher.kt` | Retire from Minecraft launch path | Builds Java command arguments and selects a JRE. Arbitrary JAR execution is a separate Java-runtime feature and should not silently remain in the native path. |
| `game/launch/GameLauncher.kt` | Split and replace execution portion | Currently selects a `Runtime`, builds Java arguments, calls `launchGame`, and loads renderer libraries. Preserve only launch orchestration that remains valid for Horizon. |
| `game/launch/handler/JVMHandler.kt` | Replace with engine lifecycle handler | It currently owns JVM-specific pause/resume/destroy and game input behavior. Preserve the UI contract, but route lifecycle and input to the Rust engine. |
| `game/launch/JvmLaunchInfo.kt` | Replace with engine launch configuration | Its `jreName` field is specific to selecting a Java runtime. |
| `components/jre/Jre.kt`, `UnpackJreTask.kt`, `UnpackJnaTask.kt` | Remove only after all consumers are migrated | These install and unpack Java/JRE components. Verify no resource-preparation or utility task still depends on them first. |
| `com/oracle/dalvik/VMLauncher.java` | Retire after JVM bootstrap is unused | It is part of the JVM startup path, not the Compose app UI. |
| `game/multirt/` and bundled `runtimes/jre-*` assets | Remove after dependency audit | Do not delete runtime directories while launcher tasks still reference their manifests or paths. |

## Adapt carefully; do not bulk-delete

- `game/renderer/` and the LWJGL modules currently configure/load Java-side renderer libraries. Horizon owns its native rendering path, so renderer selection and configuration need a deliberate mapping to Horizon capabilities. Do not assume the old renderer plugins can be reused.
- `ZLBridge`, `ZLNativeInvoker`, `NativeLibraryLoader` and JNI helpers may serve more than one native subsystem. Audit call sites before removing any bridge or loader.
- `GameLaunchFlow.checkMods()` and mod-loader/version metadata may still be useful for detecting unsupported installs, but Forge/Fabric Java mods must not be advertised as compatible unless Horizon implements and tests that compatibility.
- Keep Kotlin/Java Android application code, Gradle, AndroidX, Compose, and build tooling. “Remove Java” means remove the Java **Minecraft game runtime**, not the Android app's Kotlin/Java source language.

## Required migration order

1. Build the imported launcher unchanged apart from safe branding/signing changes; fix baseline build errors first.
2. Define and implement the Kotlin engine adapter and its Rust/JNI boundary against Horizon's real Android ABI.
3. Attach the Android rendering surface and verify create/pause/resume/resize/stop/release lifecycle.
4. Route LayerController touch, key and pointer events through a dedicated input adapter into Horizon; retain the editor and stored layouts.
5. Adapt version/resource preparation and clearly reject unsupported Java mod-loader configurations.
6. Switch the game launch route only after native launch, rendering, input, audio and lifecycle tests pass.
7. Remove JRE installers, Java bootstrap, LWJGL runtime assets and unused renderer code only after repository-wide references and tests confirm they are dead.

## Current status

The source is imported and the ARM64 debug build is under CI verification. No Horizon Android runtime is integrated, no Java game runtime has been removed, and no native game launch is claimed.
