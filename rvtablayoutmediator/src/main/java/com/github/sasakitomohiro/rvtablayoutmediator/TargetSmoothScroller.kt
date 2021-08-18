package com.github.sasakitomohiro.rvtablayoutmediator

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class TargetSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}
