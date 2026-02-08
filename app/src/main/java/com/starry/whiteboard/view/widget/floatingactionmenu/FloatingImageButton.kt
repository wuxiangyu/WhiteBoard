package com.starry.whiteboard.view.widget.floatingactionmenu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageButton
import com.starry.whiteboard.wblib.utils.OperationUtils
import com.starry.whiteboard.wblib.utils.WhiteBoardVariable

/**
 * FloatingActionButton 不具备的功能
 */
open class FloatingImageButton : ImageButton {
    var mPaint: Paint? = null
    var mDrawType: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true //消除锯齿
    }

    /**
     * 在图片中间画圆环(按钮小圆环)
     * @param color  圆的颜色
     */
    fun drawMiniRing(color: Int) {
        drawRing(WhiteBoardVariable.RingSize.MINI, color)
    }

    /**
     * 在图片中间画圆环(按钮大圆环)
     * @param color  圆的颜色
     */
    fun drawLargeRing(color: Int) {
        drawRing(WhiteBoardVariable.RingSize.LARRGE, color)
    }

    /**
     * 在图片中间画圆环
     * @param size  圆的半径
     * @param color  圆的颜色
     */
    fun drawRing(size: Int, color: Int) {
        mPaint!!.color = color
        mPaint!!.strokeWidth = size.toFloat()
        mPaint!!.style = Paint.Style.STROKE //绘制空心圆
        mDrawType = DRAW_RING
        postInvalidate()
    }

    /**
     * 在图片中间画圆
     * @param size  圆的半径
     */
    fun drawCircle(size: Int) {
        drawCircle(size, OperationUtils.mCurrentColor)
    }

    /**
     * 在图片中间画圆
     * @param size  圆的半径
     * @param color  圆的颜色
     */
    fun drawCircle(size: Int, color: Int) {
        mPaint!!.color = color
        mPaint!!.strokeWidth = size.toFloat()
        mPaint!!.style = Paint.Style.FILL //绘制实心圆
        mDrawType = DRAW_CIRCLE
        postInvalidate()
    }

    /**
     * 在图片中间画圆和外圈圆环
     * @param size  圆的半径
     * @param color  圆的颜色
     */
    fun drawCircleAndRing(size: Int, color: Int) {
        mPaint!!.color = color
        mPaint!!.strokeWidth = size.toFloat()
        mPaint!!.style = Paint.Style.FILL //绘制实心圆
        mDrawType = DRAW_CIRCLE_AND_RING
        postInvalidate()
    }

    /**
     * 清除绘画
     */
    fun clearDraw() {
        mDrawType = DRAW_NULL
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = width / 2
        if (mDrawType == DRAW_RING) {
            canvas.drawCircle(center.toFloat(), center.toFloat(), center - mPaint!!.strokeWidth, mPaint!!)
        } else if (mDrawType == DRAW_CIRCLE) {
            canvas.drawCircle(center.toFloat(), center.toFloat(), mPaint!!.strokeWidth, mPaint!!)
        } else if (mDrawType == DRAW_CIRCLE_AND_RING) {
            canvas.drawCircle(center.toFloat(), center.toFloat(), mPaint!!.strokeWidth, mPaint!!)
            mPaint!!.strokeWidth = WhiteBoardVariable.RingSize.MINI.toFloat()
            mPaint!!.style = Paint.Style.STROKE //绘制空心圆
            canvas.drawCircle(center.toFloat(), center.toFloat(), center - WhiteBoardVariable.RingSize.MINI.toFloat(), mPaint!!)
        }
    }

    companion object {
        /**不做绘画操作 */
        const val DRAW_NULL = 0

        /**画圆环 */
        const val DRAW_RING = 1

        /**画圆 */
        const val DRAW_CIRCLE = 2

        /**画圆和圆环 */
        const val DRAW_CIRCLE_AND_RING = 3
    }
}
