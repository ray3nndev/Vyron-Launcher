/*
 * Vyron Launcher
 *
 * GPL-3.0-or-later. This file is part of an unofficial modification of Zalith Launcher 2.
 */

package com.movtery.zalithlauncher.game.launch

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

/**
 * Starts the actual Starling/Horizon NativeActivity game host directly.
 *
 * Do not use LauncherActivity here: it is a file-import/setup trampoline running in
 * a separate process. GameActivity is the NativeActivity whose manifest metadata loads
 * libhorizon_android.so and calls starling_on_create(), which starts the Rust game loop.
 *
 * This is only the native-host launch step. Vyron still needs to provide authenticated
 * account state and prepared Minecraft 26.3 resources before a clean first-run launch
 * can be considered complete.
 */
object RustEngineLauncher {
    private const val GAME_ACTIVITY =
        "io.github.juanluci.starling.GameActivity"
    private const val REQUIRED_VULKAN_VERSION = 0x00400003 // Vulkan 1.3

    fun launch(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            throw IllegalStateException("The Rust engine requires Android 7.0 or newer.")
        }

        val packageManager = context.packageManager
        val supportsVulkan13 = packageManager.hasSystemFeature(
            PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,
            REQUIRED_VULKAN_VERSION
        )
        if (!supportsVulkan13) {
            throw IllegalStateException(
                "This device does not report Vulkan 1.3 support, which the Rust engine requires."
            )
        }

        val intent = Intent().setClassName(context, GAME_ACTIVITY).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(packageManager) == null) {
            throw IllegalStateException(
                "The Horizon Rust GameActivity is not packaged in this Vyron build."
            )
        }
        context.startActivity(intent)
    }
}
