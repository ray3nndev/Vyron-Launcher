# Vyron Launcher

A native Android launcher project exploring a Rust-powered Minecraft game runtime while retaining the Android launcher experience and customizable touch controls from the Zalith Launcher 2 codebase.

## Project direction

- Android application layer: Kotlin/Java and Gradle.
- Game execution: integrate Horizon's native Rust engine for Android ARM64, subject to Android platform support.
- Touch controls: preserve the LayerController layout model, editor, customization and pointer handling through a dedicated input adapter.
- Data compatibility: evaluate worlds, resource packs, accounts and library migration independently.

## Important scope boundary

Vyron replaces the Minecraft **game runtime** path only. It does not remove Kotlin/Java Android application code or Gradle/JDK build tooling. Some resource-preparation tasks may still require a JDK.

No Horizon integration, runtime removal, input adapter, successful APK build or device test is claimed at this stage.

## Development status

- [x] Create isolated integration branch.
- [x] Audit Zalith Launcher 2 architecture and LayerController behavior.
- [x] Audit Horizon native-client Android readiness and define the engine boundary.
- [x] Import the pinned Zalith Android source snapshot into the integration branch, preserving its license and notices while excluding upstream signing material.
- [ ] Complete the first Android ARM64 debug build (tracked by the `Vyron Android ARM64 debug build` workflow).
- [ ] Validate the imported LayerController editor and touch runtime in the built app and on-device.
- [ ] Implement the Android ARM64 Horizon adapter.
- [ ] Verify world/resource-pack/account compatibility.
- [ ] Build and test on a physical Android ARM64 device.

See [the upstream integration audit](docs/UPSTREAM_INTEGRATION_AUDIT.md) and [the engine boundary design](docs/ENGINE_BOUNDARY.md).

## Upstream and licensing

The intended launcher UI and LayerController source comes from [ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2). Its inspected upstream revision is `36b8ef87f3ce498c2865b483e3b3a0096acbefc7`.

Before distributing imported code, preserve the full GPL-3.0 license, all copyright notices and source attribution, audit each bundled component's license, and comply with the upstream README's additional GPLv3 section 7 conditions, including distinct branding and a visible non-official-modification notice. This repository is not an official Zalith Launcher, Horizon, Mojang or Microsoft product.

Do not copy upstream signing keystores, credentials or release secrets.

## Current state

The pinned Zalith Android source snapshot is now imported on `integration/zalith-ui-shell` at Vyron commit `96a975a882237a2a2ce990c87bcfc7798cabc780`. The source import excludes upstream signing keystores and scrubs the checked-in signing password values. The Android app module and LayerController source are present. The first ARM64 debug build is currently running in GitHub Actions; no APK or device test is claimed until that run succeeds.
