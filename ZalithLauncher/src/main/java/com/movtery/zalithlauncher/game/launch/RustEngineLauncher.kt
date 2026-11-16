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
 * Starts the bundled Starling/Horizon native Rust game through Starling's Android
 * launcher trampoline. LauncherActivity is the supported entry point: it handles
 * file hand-offs/restarts and then opens GameActivity. Starting StarlingActivity
 * directly bypasses that host flow.
 *
 * Note: this does not yet pass a Vyron-selected Minecraft version/account. Starling's
 * own first-run resource preparation remains necessary until the version/launch
 * contract is implemented.
 */
object RustEngineLauncher {
    private const val STARLING_LAUNCHER_ACTIVITY =
        "io.github.juanluci.starling.LauncherActivity"
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

        val intent = Intent().setClassName(context, STARLING_LAUNCHER_ACTIVITY).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(packageManager) == null) {
            throw IllegalStateException(
                "The Starling Rust launcher is not packaged in this Vyron build."
            )
        }
        context.startActivity(intent)
    }
}
