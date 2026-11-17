package com.ray3nndev.vyron.nativebridge

/**
 * JNI entry points for the in-process Horizon Android bridge.
 *
 * The native library is packaged as libhorizon_android_bridge.so.
 * Keep this package and class name in sync with the Rust JNI exports.
 */
object HorizonBridge {
    init {
        System.loadLibrary("horizon_android_bridge")
    }

    @JvmStatic
    external fun nativeConfigure(
        gameDir: String,
        homeDir: String,
        resourcesDir: String
    ): Int

    @JvmStatic
    external fun nativePrepare(): Int
}
