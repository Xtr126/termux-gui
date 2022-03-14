package com.termux.gui.protocol.shared.v0

import android.content.Context
import android.graphics.Bitmap
import android.os.SharedMemory
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.RemoteViews
import com.termux.gui.GUIActivity
import com.termux.gui.R
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class DataClasses {
    data class SharedBuffer(val btm: Bitmap, val shm: SharedMemory?, val buff: ByteBuffer, val fd: Int?)
    data class WidgetRepresentation(val usedIds: TreeSet<Int> = TreeSet(), var root: RemoteViews?, var theme: GUIActivity.GUITheme?)
    data class ActivityState(var a: GUIActivity?, @Volatile var saved: Boolean = false, val queued: LinkedBlockingQueue<(activity: GUIActivity) -> Unit> = LinkedBlockingQueue<(activity: GUIActivity) -> Unit>(100))
    data class Overlay(val context: Context) {
        val usedIds: TreeSet<Int> = TreeSet()
        var theme: GUIActivity.GUITheme? = null
        var sendTouch = false
        val root = OverlayView(context)
        init {
            usedIds.add(R.id.root)
            root.id = R.id.root
        }
        inner class OverlayView(c: Context) : FrameLayout(c) {
            var interceptListener : ((MotionEvent) -> Unit)? = null
            override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
                val int = interceptListener
                if (int != null && ev != null) {
                    int(ev)
                }
                return false
            }
            fun inside(ev: MotionEvent) : Boolean {
                val loc = IntArray(2)
                getLocationOnScreen(loc)
                val x = ev.rawX
                val y = ev.rawY
                if (x < loc[0] || x > loc[0]+width || y < loc[1] || y > loc[1]+height) {
                    return false
                }
                return true
            }
            
            @Suppress("UNCHECKED_CAST")
            fun <T> findViewReimplemented(id: Int) : T? {
                return findViewById(id)
            }
        }
    }


}