# Vyron engine boundary

Status: design contract only; not implemented or compiled.

## Goal

Keep the Android launcher application in Kotlin/Java and Gradle. Replace the Minecraft game execution backend with Horizon's native Rust client when an Android ARM64 runtime is available.

## Proposed boundary

The launcher must not call Horizon internals from UI composables or from LayerController widgets. Route all game lifecycle and input through one application-owned adapter.

Conceptual Kotlin contract (not yet added to source):

```kotlin
interface EngineAdapter {
    val state: StateFlow<EngineState>

    suspend fun prepare(request: EngineLaunchRequest)
    fun attachSurface(surface: Surface)
    fun onTouch(event: EngineTouchEvent)
    fun onKey(event: EngineKeyEvent)
    fun pause()
    fun resume()
    suspend fun stop()
    fun release()
}
```

The eventual implementation may use JNI, UniFFI, or another stable FFI boundary; choose only after confirming Horizon's Android build and exported ABI. Do not lock a binding technology before inspecting the native crate's actual entry points and threading model.

## Responsibilities

- Android/Kotlin: account UI, settings, library, file permissions, lifecycle, foreground service policy, dialogs, progress and error presentation.
- LayerController: existing control layout model, editor, layers, widgets, pointer tracking and customization.
- Input adapter: translate LayerController semantic events into engine input events; preserve pointer identity, press/release symmetry, analog axes, pointer cancellation and multi-touch.
- Rust engine: world/game simulation, rendering, audio, networking and save operations, subject to Horizon's supported platform/API.
- Resource preparation: a separately managed preparation workflow. Horizon currently documents a JDK requirement for some resource exporters, so do not remove all JDK preparation support prematurely.

## Lifecycle requirements

1. Confirm the Android native library can be loaded on ARM64.
2. Establish a valid Android window/surface and graphics context.
3. Handle surface create/change/destroy and Activity pause/resume without leaking native threads.
4. Ensure stop/release is idempotent and joins worker threads.
5. Keep long-running preparation and launch work off the main thread.
6. Report structured progress/errors to the launcher UI.
7. Test process death, rotation/configuration changes, background/foreground transitions and low-memory behavior.

## Input requirements

The adapter must preserve:
- multi-pointer IDs and concurrent touches;
- down/move/up/cancel semantics;
- joystick axes and dead zones;
- keyboard/gamepad events where supported;
- pointer capture and release when a touch leaves a widget;
- consistent mapping for configurable controls.

Do not forward raw Compose pointer objects across JNI. Convert to stable, platform-neutral event records.

## Compatibility constraints

- Existing Java Forge/Fabric mod JARs are not automatically compatible with a native Rust client.
- World, resource-pack, account and server compatibility each need independent tests.
- Horizon currently documents a native client for Minecraft Java Edition 26.3 and experimental older-server adapters; this does not establish broad version parity.
- Native Android rendering, audio, touch UI and packaging remain unproven until tested on device.

## Acceptance gates

- [ ] Horizon Android ARM64 build exists and links.
- [ ] Native library loads from a Vyron debug APK.
- [ ] Surface lifecycle is stable across pause/resume.
- [ ] Empty scene renders on a physical ARM64 device.
- [ ] Touch input reaches Rust and is observable in a test harness.
- [ ] LayerController controls work with multi-touch and release/cancel paths.
- [ ] A world loads, saves and reloads on device.
- [ ] No claim of Java-mod compatibility without a dedicated compatibility implementation and tests.
