package com.starry.whiteboard.wblib.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.starry.whiteboard.wblib.R
import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.utils.OperationUtils
import com.starry.whiteboard.wblib.EventBus
import com.starry.whiteboard.wblib.utils.Events
import com.starry.whiteboard.wblib.utils.AppContextUtil

class DrawImageLayout : FrameLayout {
    private var mContext: Context? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {
        mContext = context
    }

    fun init() {
        this.setBackgroundColor(AppContextUtil.getColor(R.color.transparent))
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
            if (dp.type == OperationUtils.DRAW_IMAGE && dp.drawImage!!.isVisible && dp.drawImage!!.status != DrawImageView.IMAGE_DELETE) {
                val di = DrawImageView(mContext!!, dp, mCallBackListener)
                di.tag = i
                this.addView(di)
            }
        }
    }

    private val mCallBackListener: DrawImageView.CallBackListener =
        object : DrawImageView.CallBackListener {
            override fun onUpdate(drawPoint: DrawPoint?) {
                updatePoint(drawPoint!!)
                showPoints()
            }
        }

    private fun updatePoint(drawPoint: DrawPoint) {
        // Deselect all other items before adding/updating this one
        if (drawPoint.drawImage!!.status == DrawImageView.IMAGE_DETAIL) {
             OperationUtils.deselectAllItems()
        }

        val size = OperationUtils.savePoints.size
        for (i in size - 1 downTo 0) {
            val temp = OperationUtils.savePoints[i]
            if (temp.type == OperationUtils.DRAW_IMAGE && temp.drawImage!!.id == drawPoint.drawImage!!.id) {
                // Hide the previous version of this image
                OperationUtils.savePoints[i].drawImage!!.isVisible = false
                break
            }
        }
        OperationUtils.savePoints.add(drawPoint)
        OperationUtils.deletePoints.clear()
        EventBus.postEvent(Events.WHITE_BOARD_UNDO_REDO)
        // Trigger global refresh to update other items' selection state
        EventBus.postEvent(Events.WHITE_BOARD_REFRESH)
    }

    fun undo() {
        if (OperationUtils.deletePoints.isEmpty()) return
        val drawPoint = OperationUtils.deletePoints[OperationUtils.deletePoints.size - 1]
        val size = OperationUtils.savePoints.size
        if (size > 0) {
            for (i in size - 1 downTo 0) {
                val temp = OperationUtils.savePoints[i]
                if (temp.type == OperationUtils.DRAW_IMAGE && temp.drawImage!!.id == drawPoint.drawImage!!.id) {
                    // Restore visibility of previous version
                    OperationUtils.savePoints[i].drawImage!!.isVisible = true
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
                if (temp.type == OperationUtils.DRAW_IMAGE && temp.drawImage!!.id == drawPoint.drawImage!!.id) {
                    // Hide previous version
                    OperationUtils.savePoints[i].drawImage!!.isVisible = false
                    break
                }
            }
        }
        showPoints()
    }
}
