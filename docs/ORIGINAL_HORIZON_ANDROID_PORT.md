# Original Horizon Android Port

## Source pin

Vyron tracks the unmodified upstream Horizon repository at `engine/horizon-upstream`.
The current upstream revision pinned by this commit is:

- Repository: https://github.com/horizon-rust/horizon
- Commit: `90e97a1fc2fa4aac23766e418f55cbce94e97618`

The existing `engine/starling` submodule is retained temporarily as a reference for
Android integration patterns. It is not the intended final engine source.

## Why this is a port, not a dependency swap

The upstream Horizon desktop launcher is GPUI-based and its backend starts desktop
processes. It is not an Android launcher. The shared Horizon client contains Android-
related rendering/runtime code, but upstream does not currently provide Starling's
complete Android NativeActivity packaging and host glue.

The Android port therefore needs a Vyron-owned platform layer around the upstream
Horizon client:

1. Build the upstream Rust client for `aarch64-linux-android` as a `cdylib`.
2. Implement NativeActivity/JNI lifecycle glue and a stable C ABI owned by Vyron.
3. Attach Android's native window/surface and route pause, resume, resize and destroy.
4. Bridge Vyron's selected game directory, account/session, version metadata and
   launch configuration into Horizon without opening a separate first-run launcher.
5. Route touch, keyboard, controller, audio and storage access through Android APIs.
6. Verify actual game startup and lifecycle on ARM64 hardware before switching the
   launcher route.

## Runtime-removal guardrails

Do not delete the bundled JREs, JVM bootstrap, LWJGL assets or Java preparation code
as a blanket cleanup. Horizon's current setup/preparation flow includes a Java
preparation stage even though the target Minecraft game execution backend is Rust.
The Android app's Kotlin/Java code and JNI remain required as well.

Remove the Java Minecraft execution path only after the native launch path supports
version preparation, authentication, assets, rendering, input, audio, pause/resume
and clean shutdown. Then remove only runtime assets and code proven to have no
remaining consumers.

## Current implementation status

- [x] Pin the original upstream Horizon source independently from Starling.
- [x] Preserve Starling source as a temporary Android integration reference.
- [ ] Port upstream client entry points to a Vyron Android `cdylib`.
- [ ] Implement Vyron-owned Android host/JNI bridge.
- [ ] Implement selected-version/account launch handoff.
- [ ] Implement a real Rust-compatible version catalog and preparation flow.
- [ ] Verify native gameplay on an ARM64 Android device.
- [ ] Retire Java game execution and its now-unused runtime assets.

No native gameplay or device validation is claimed by this source-tracking change.
