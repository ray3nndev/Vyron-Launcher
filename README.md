# Vyron Launcher

A native Android launcher project exploring a Rust-powered Minecraft game runtime while retaining the useful Android launcher experience and customizable touch controls from the Zalith Launcher 2 codebase.

## Project direction

- Android application layer: Kotlin/Java and the existing Gradle/JDK build toolchain.
- Game execution: investigate and integrate Horizon's native Rust engine for Android ARM64.
- Touch controls: preserve/adapt the LayerController layout model, editor, customization, and pointer handling through a dedicated input adapter.
- Data compatibility: evaluate worlds, resource packs, accounts, and library migration individually.

## Important scope boundary

This project intends to replace the Minecraft **game runtime** path, not remove Kotlin/Java code used by the Android application or remove Gradle/JDK build tooling.

No Horizon integration, Java-runtime removal, input adapter, successful APK build, or device test is claimed at this stage.

## Development status

- [ ] Import and audit the licensed upstream Zalith Launcher 2 source.
- [ ] Audit Horizon's Android ARM64 engine APIs, build outputs, and runtime requirements.
- [ ] Define the native engine boundary and lifecycle.
- [ ] Implement LayerController-to-engine input translation.
- [ ] Verify world/resource-pack/account compatibility.
- [ ] Build and test on a physical Android ARM64 device.

## Upstream and licensing

The launcher UI and LayerController work is based on the intended reuse/adaptation of [ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2). Before importing or distributing upstream code, preserve its license, copyright notices, source attribution, and applicable GPL-3.0 obligations. Review all bundled components and their individual licenses as well.

This is an independent project and is not an official Zalith Launcher, Horizon, Mojang, or Microsoft product.

## Current state

This repository is a project bootstrap only. The upstream source has not yet been imported.
