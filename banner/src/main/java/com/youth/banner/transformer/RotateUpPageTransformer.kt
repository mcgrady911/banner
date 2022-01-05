package com.youth.banner.transformer

import android.view.View


class RotateUpPageTransformer : BasePageTransformer {
    private var mMaxRotate = DEFAULT_MAX_ROTATE

    constructor() {}
    constructor(maxRotate: Float) {
        mMaxRotate = maxRotate
    }

    override fun transformPage(view: View, position: Float) {
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.rotation = mMaxRotate
                view.pivotX = view.width.toFloat()
                view.pivotY = 0f
            }
            position <= 1 -> {  // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
                // [-1,1]
                // Modify the default slide transition to shrink the page as well
                if (position < 0) { //[0，-1]
                    view.pivotX = view.width * (0.5f + 0.5f * -position)
                    view.pivotY = 0f
                    view.rotation = -mMaxRotate * position
                } else { //[1,0]
                    view.pivotX = view.width * 0.5f * (1 - position)
                    view.pivotY = 0f
                    view.rotation = -mMaxRotate * position
                }
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                // ViewHelper.setRotation(view, ROT_MAX);
                view.rotation = -mMaxRotate
                view.pivotX = 0f
                view.pivotY = 0f
            }
        }
    }

    companion object {
        private const val DEFAULT_MAX_ROTATE = 15.0f
    }
}