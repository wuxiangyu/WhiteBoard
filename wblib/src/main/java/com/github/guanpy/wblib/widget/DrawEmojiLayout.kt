package com.github.guanpy.wblib.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.guanpy.wblib.R
import com.github.guanpy.wblib.bean.DrawEmojiPoint
import com.github.guanpy.wblib.bean.DrawPoint
import com.github.guanpy.wblib.utils.OperationUtils

import com.github.guanpy.library.EventBus
import com.github.guanpy.wblib.utils.Events

class DrawEmojiLayout : FrameLayout {
    private var mContext: Context? = null

    // ... constructors ... (keep them or assume they are unchanged if I target correctly)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {
        mContext = context
    }

    fun init(activity: Activity) {
        this.setBackgroundColor(resources.getColor(R.color.transparent))
        showPoints()
    }

    fun showPoints() {
        val size = OperationUtils.savePoints.size
        this.removeAllViews()
        if (size == 0) {
            return
        }
        for (i in 0 until size) {
            val dp = OperationUtils.savePoints[i]
            if (dp.type == OperationUtils.DRAW_EMOJI && dp.drawEmoji!!.isVisible) {
                val de = DrawEmojiView(mContext!!, dp, mCallBackListener)
                de.tag = i
                this.addView(de)
            }
        }
    }

    private val mCallBackListener: DrawEmojiView.CallBackListener =
        object : DrawEmojiView.CallBackListener {
            override fun onUpdate(drawPoint: DrawPoint?) {
                updatePoint(drawPoint!!)
                showPoints()
            }
        }

    private fun updatePoint(drawPoint: DrawPoint) {
        val size = OperationUtils.savePoints.size
        for (i in size - 1 downTo 0) {
            val temp = OperationUtils.savePoints[i]
            if (temp.type == OperationUtils.DRAW_EMOJI && temp.drawEmoji!!.id == drawPoint.drawEmoji!!.id) {
                // Hide the previous version of this emoji
                OperationUtils.savePoints[i].drawEmoji!!.isVisible = false
                break
            }
        }
        OperationUtils.savePoints.add(drawPoint)
        OperationUtils.deletePoints.clear()
        EventBus.postEvent(Events.WHITE_BOARD_UNDO_REDO)
    }

    fun undo() {
        if (OperationUtils.deletePoints.isEmpty()) return
        val drawPoint = OperationUtils.deletePoints[OperationUtils.deletePoints.size - 1]
        val size = OperationUtils.savePoints.size
        if (size > 0) {
            for (i in size - 1 downTo 0) {
                val temp = OperationUtils.savePoints[i]
                if (temp.type == OperationUtils.DRAW_EMOJI && temp.drawEmoji!!.id == drawPoint.drawEmoji!!.id) {
                    // Restore visibility of previous version
                    OperationUtils.savePoints[i].drawEmoji!!.isVisible = true
                    break
                }
            }
        }
        showPoints()
    }

    fun redo() {
        val size = OperationUtils.savePoints.size
        if (size == 0) return
        val drawPoint = OperationUtils.savePoints[size - 1]
        if (size > 1) {
            for (i in size - 2 downTo 0) {
                val temp = OperationUtils.savePoints[i]
                if (temp.type == OperationUtils.DRAW_EMOJI && temp.drawEmoji!!.id == drawPoint.drawEmoji!!.id) {
                    // Hide previous version
                    OperationUtils.savePoints[i].drawEmoji!!.isVisible = false
                    break
                }
            }
        }
        showPoints()
    }
}
