package com.youth.banner.util

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

interface BannerLifecycleObserver : LifecycleObserver {
    fun onStop(owner: LifecycleOwner?)
    fun onStart(owner: LifecycleOwner?)
    fun onDestroy(owner: LifecycleOwner?)
}