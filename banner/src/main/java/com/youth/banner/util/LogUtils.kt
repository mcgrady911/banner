package com.youth.banner.util

import android.util.Log
import com.youth.banner.BuildConfig

internal object LogUtils {
    const val TAG = "banner_log"
    private val DEBUG: Boolean = BuildConfig.DEBUG
    @kotlin.jvm.JvmStatic
    fun d(msg: String?) {
        if (DEBUG) {
            Log.d(TAG, msg!!)
        }
    }

    fun e(msg: String?) {
        if (DEBUG) {
            Log.e(TAG, msg!!)
        }
    }

    fun i(msg: String?) {
        if (DEBUG) {
            Log.i(TAG, msg!!)
        }
    }

    fun v(msg: String?) {
        if (DEBUG) {
            Log.v(TAG, msg!!)
        }
    }

    fun w(msg: String?) {
        if (DEBUG) {
            Log.w(TAG, msg!!)
        }
    }
}