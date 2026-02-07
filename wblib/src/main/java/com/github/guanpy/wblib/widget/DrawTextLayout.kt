package com.github.guanpy.wblib.widget

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.guanpy.library.EventBus
import com.github.guanpy.wblib.R
import com.github.guanpy.wblib.bean.DrawPoint
import com.github.guanpy.wblib.bean.DrawTextPoint
import com.github.guanpy.wblib.utils.Events
import com.github.guanpy.wblib.utils.OperationUtils
import com.github.guanpy.wblib.utils.WhiteBoardVariable

/**
 * 白板--文字层
 * @author  gpy
 */
class DrawTextLayout : FrameLayout {
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

    fun init(activity: Activity) {
        val params = this.layoutParams
        if (activity.windowManager != null) {
            val display = activity.windowManager.defaultDisplay
            params.width = display.width
            params.height = display.width
        }
        this.layoutParams = params
        this.setBackgroundColor(resources.getColor(R.color.transparent))
        showPoints()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.e("DrawTextLayout", "dispatchTouchEvent: " + ev.action)
        val result = super.dispatchTouchEvent(ev)
        Log.e("DrawTextLayout", "dispatchTouchEvent result: " + result)
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e("DrawTextLayout", "onTouchEvent: action=${event.action}, Type=${OperationUtils.mCurrentDrawType}")
        if (OperationUtils.mCurrentDrawType == OperationUtils.DRAW_TEXT && OperationUtils.DISABLE) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var moveX = event.x
                    var moveY = event.y
                    if (height - moveY < dip2px(MARGIN_BOTTOM.toFloat())) {
                        moveY = moveY - dip2px(MARGIN_BOTTOM.toFloat())
                    }
                    if (width - moveX < dip2px(MARGIN_RIGHT.toFloat())) {
                        moveX = moveX - dip2px(MARGIN_RIGHT.toFloat())
                    }
                    Log.d("添加文字", "-$moveX,$moveY")
                    val ip = DrawTextPoint()
                    ip.x = moveX
                    ip.y = moveY
                    ip.color = OperationUtils.mCurrentColor
                    ip.status = DrawTextView.TEXT_EDIT
                    ip.isVisible = true
                    ip.id = OperationUtils.newMarkId
                    val drawPoint = DrawPoint()
                    drawPoint.type = OperationUtils.DRAW_TEXT
                    drawPoint.drawText = ip
                    //                    OperationUtils.getInstance().getSavePoints().add(drawPoint);
                    showPoints()
                    showNewPoint(drawPoint)
                }
            }
        }
        return false
    }

    fun showPoints() {
        val size = OperationUtils.savePoints.size
        Log.d("gpy", "显示文字列表--$size")
        this.removeAllViews()
        if (size == 0) {
            return
        }
        for (i in 0 until size) {
            val dp = OperationUtils.savePoints[i]
            if (dp.type == OperationUtils.DRAW_TEXT && dp.drawText!!.isVisible && dp.drawText!!.status != DrawTextView.TEXT_DELETE) {
                val dw = DrawTextView(
                    mContext!!,
                    dp, mCallBackListener
                )
                dw.tag = i
                this.addView(dw)
                //                }
            }
        }
    }

    /**
     * 文字编辑之后
     */
    fun afterEdit(isSave: Boolean) {
        (getChildAt(childCount - 1) as DrawTextView).afterEdit(isSave)
    }

    private fun showNewPoint(dp: DrawPoint) {
        Log.d("gpy", "显示新建文字")
        if (dp.type == OperationUtils.DRAW_TEXT && dp.drawText!!.isVisible && dp.drawText!!.status != DrawTextView.TEXT_DELETE) {
            val dw = DrawTextView(
                mContext!!,
                dp, mCallBackListener
            )
            this.addView(dw)
        }
    }

    private val mCallBackListener: DrawTextView.CallBackListener =
        object : DrawTextView.CallBackListener {
            override fun onUpdate(drawPoint: DrawPoint?) {
                updatePoint(drawPoint!!)
                showPoints()
            }
        }

    private fun updatePoint(drawPoint: DrawPoint) {
        Log.d(
            "修改标注",
            "-" + drawPoint.drawText!!.x + "," + drawPoint.drawText!!.y
        )
        val size = OperationUtils.savePoints.size
        for (i in size - 1 downTo 0) {
            val temp = OperationUtils.savePoints[i]
            if (temp.type == OperationUtils.DRAW_TEXT && temp.drawText!!.id == drawPoint.drawText!!.id) { //如果文字组件是之前已存在，则隐藏之前的
                OperationUtils.savePoints[i].drawText!!.isVisible = false
                break
            }
        }
        if (!TextUtils.isEmpty(drawPoint.drawText!!.str)) {
            OperationUtils.savePoints.add(drawPoint)
            EventBus.postEvent(Events.WHITE_BOARD_UNDO_REDO)
        }
        OperationUtils.deletePoints.clear()
    }

    /**设置文字风格：下划线、粗体、斜体等 */
    fun setTextStyle(changeStyle: Int) {
        val size = OperationUtils.savePoints.size
        if (size == 0) {
            return
        }
        val dp = OperationUtils.savePoints[size - 1]
        if (dp.type == OperationUtils.DRAW_TEXT) {
            val temp = DrawPoint.copyDrawPoint(dp)
            when (changeStyle) {
                WhiteBoardVariable.TextStyle.CHANGE_UNDERLINE -> temp.drawText!!.isUnderline =
                    !temp.drawText!!.isUnderline
                WhiteBoardVariable.TextStyle.CHANGE_ITALICS -> temp.drawText!!.isItalics =
                    !temp.drawText!!.isItalics
                WhiteBoardVariable.TextStyle.CHANGE_BOLD -> temp.drawText!!.isBold =
                    !temp.drawText!!.isBold
            }
            updatePoint(temp)
            showPoints()
        }
    }

    /**设置文字颜色 */
    fun setTextColor() {
        val size = OperationUtils.savePoints.size
        if (size == 0) {
            return
        }
        val dp = OperationUtils.savePoints[size - 1]
        if (dp.type == OperationUtils.DRAW_TEXT && dp.drawText!!.status == DrawTextView.TEXT_DETAIL) {
            val temp = DrawPoint.copyDrawPoint(dp)
            temp.drawText!!.color = OperationUtils.mCurrentColor
            updatePoint(temp)
            showPoints()
        }
    }

    /**撤销 */
    fun undo() {
        if (OperationUtils.deletePoints.isEmpty()) return
        val drawPoint = OperationUtils.deletePoints[OperationUtils.deletePoints.size - 1]
        val size = OperationUtils.savePoints.size
        if (size > 0) {
            for (i in size - 1 downTo 0) {
                val temp = OperationUtils.savePoints[i]
                if (temp.type == OperationUtils.DRAW_TEXT && temp.drawText!!.id == drawPoint.drawText!!.id) { //如果文字组件是之前已存在，则显示最近的
                    OperationUtils.savePoints[i].drawText!!.isVisible = true
                    break
                }
            }
        }
        showPoints()
    }

    /**重做 */
    fun redo() {
        val size = OperationUtils.savePoints.size
        val drawPoint = OperationUtils.savePoints[size - 1]
        if (size > 1) {
            for (i in size - 2 downTo 0) {
                val temp = OperationUtils.savePoints[i]
                if (temp.type == OperationUtils.DRAW_TEXT && temp.drawText!!.id == drawPoint.drawText!!.id) { //如果文字组件是之前已存在，则隐藏之前的
                    OperationUtils.savePoints[i].drawText!!.isVisible = false
                    break
                }
            }
        }
        showPoints()
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(dpValue: Float): Int {
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private const val MARGIN_RIGHT = 100
        private const val MARGIN_BOTTOM = 75
    }
}
