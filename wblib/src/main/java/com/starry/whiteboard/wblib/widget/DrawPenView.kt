package com.starry.whiteboard.wblib.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.starry.whiteboard.wblib.EventBus
import com.starry.whiteboard.wblib.R
import com.starry.whiteboard.wblib.bean.DrawPenPoint
import com.starry.whiteboard.wblib.bean.DrawPenStr
import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.bean.Point
import com.starry.whiteboard.wblib.utils.Events
import com.starry.whiteboard.wblib.utils.OperationUtils
import com.starry.whiteboard.wblib.utils.AppContextUtil
import kotlin.math.abs

class DrawPenView : View {
    /**
     * 用于画线
     */
    private var mPaint: Paint? = null

    /**
     * 用于画图
     */
    private var mBitmapPaint: Paint? = null

    /**
     * 画图路径
     */
    private var mPath: Path? = null

    /**
     * 步骤存储bean
     */
    private var mDrawPath: DrawPoint? = null

    /**
     * 绘画存储-画笔路径(字符串格式，方便储存)
     */
    private var mDrawPenStr: DrawPenStr? = null

    /**
     * 画图
     */
    private var mBottomBitmap: Bitmap? = null
    private var mBottomBitmapDrawHeight = 0
    var posX: Float = 0f
    var posY: Float = 0f
    private val TOUCH_TOLERANCE = 4f
    private var mContext: Context? = null
    private var mCanvas: Canvas? = null

    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(
        context,
        attr,
        defStyle
    ) {
        initView(context)
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {
        Log.e("DrawPenView", "initView")
        mContext = context
        initPaint()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
        isClickable = true // Ensure clickable
    }

    // ...

    fun init(paint: Paint?) {
        if (paint == null) {
            initPaint()
        } else {
            mPaint = paint
        }
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true //是否使用抗锯齿功能,会消耗较大资源，绘制图形速度会变慢
        mPaint!!.isDither = true // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint!!.color = OperationUtils.mCurrentColor //设置绘制的颜色
        mPaint!!.style = Paint.Style.STROKE //设置画笔的样式
        mPaint!!.strokeJoin = Paint.Join.ROUND //设置绘制时各图形的结合方式，如平滑效果等
        mPaint!!.strokeCap =
            Paint.Cap.ROUND //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式    Cap.ROUND,或方形样式Cap.SQUARE
        mPaint!!.strokeWidth =
            (2 * OperationUtils.mCurrentPenSize).toFloat() //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
    }

    /**
     * 初始化画笔
     */
    fun changeEraser() {
        mPaint!!.color = AppContextUtil.getColor(R.color.transparent)
        mPaint!!.strokeWidth = (2 * OperationUtils.mCurrentEraserSize).toFloat()
        mPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) //擦除模式
        postInvalidate()
    }

// ... (skipping some lines if I can target precisely or use multiple chunks)

// Wait, replace_file_content is single contiguous block. I'll use separate calls or try to encompass them.
// The !! assertions are way down in onTouchEvent (line 212). The changeEraser is line 109.
// Too far apart. I should use multi_replace. Or separate calls.
// I'll use separate calls. This one for changeEraser.

    /**
     * 修改橡皮擦尺寸
     */
    fun setEraserSize() {
        mPaint!!.strokeWidth = (2 * OperationUtils.mCurrentEraserSize).toFloat() //size为圆半径
        postInvalidate()
    }

    /**
     * 修改画笔尺寸
     */
    fun setPenSize() {
        mPaint!!.strokeWidth = (2 * OperationUtils.mCurrentPenSize).toFloat() //size为圆半径
        postInvalidate()
    }

    /**
     * 修改画笔颜色
     */
    fun setPenColor() {
        mPaint!!.color = OperationUtils.mCurrentColor
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("DrawPenView", "onSizeChanged: $w x $h")
        if (w > 0 && h > 0) {
            mBottomBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBottomBitmap!!)
            showPoints()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mBottomBitmap == null) {
            if (width > 0 && height > 0) {
                mBottomBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mCanvas = Canvas(mBottomBitmap!!)
                showPoints()
            } else {
                return
            }
        }
        canvas.drawColor(Color.TRANSPARENT)
        val nCanvasHeight = canvas.height
        val nBitmapHeight = mBottomBitmap!!.height
        mBottomBitmapDrawHeight = (nCanvasHeight - nBitmapHeight) / 2
        canvas.drawBitmap(mBottomBitmap!!, 0f, mBottomBitmapDrawHeight.toFloat(), mBitmapPaint)
        if (mPath != null) {
            if (mPaint == null) {
                initPaint()
            }
            canvas.drawPath(mPath!!, mPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (OperationUtils.DISABLE && (OperationUtils.mCurrentDrawType == OperationUtils.DRAW_PEN || OperationUtils.mCurrentDrawType == OperationUtils.DRAW_ERASER)) {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Deselect any selected items (Emoji, Text) when starting to draw or clicking background
                    OperationUtils.deselectAllItems()
                    EventBus.postEvent(Events.WHITE_BOARD_REFRESH)

                    mPath = Path()
                    mDrawPath = DrawPoint()
                    mPath!!.moveTo(x, y)
                    val dpp = DrawPenPoint()
                    dpp.paint = Paint(mPaint)
                    dpp.path = mPath
                    //绘画时存储字符形式
                    mDrawPenStr = DrawPenStr()
                    mDrawPenStr!!.color = mPaint!!.color
                    mDrawPenStr!!.strokeWidth = mPaint!!.strokeWidth
                    Log.e("DrawPenView", "ACTION_DOWN: PenSize=${OperationUtils.mCurrentPenSize}, Color=${mPaint!!.color}, StrokeWidth=${mPaint!!.strokeWidth}")
                    mDrawPenStr!!.moveTo = Point(x, y)
                    if (OperationUtils.mCurrentDrawType == OperationUtils.DRAW_ERASER) {
                        mDrawPenStr!!.isEraser = true
                    } else {
                        mDrawPenStr!!.isEraser = false
                    }
                    mDrawPath!!.drawPen = dpp
                    mDrawPath!!.type = OperationUtils.DRAW_PEN
                    posX = x
                    posY = y
                    postInvalidate()
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(x - posX)
                    val dy = abs(y - posY)
                    if (dx >= TOUCH_TOLERANCE || dy > TOUCH_TOLERANCE) {
                        mDrawPenStr!!.quadToA.add(Point(posX, posY))
                        mDrawPenStr!!.quadToB.add(Point((x + posX) / 2, (y + posY) / 2))
                        mPath!!.quadTo(posX, posY, (x + posX) / 2, (y + posY) / 2)
                        posX = x
                        posY = y
                    }
                    postInvalidate()
                }

                MotionEvent.ACTION_UP -> {
                    mDrawPenStr!!.lineTo = Point(posX, posY)
                    mPath!!.lineTo(posX, posY)
                    mDrawPenStr!!.offset = Point(0f, -mBottomBitmapDrawHeight.toFloat())
                    mPath!!.offset(0f, -mBottomBitmapDrawHeight.toFloat())
                    if (mCanvas != null) {
                        mCanvas!!.drawPath(mPath!!, mPaint!!)
                    }
                    mDrawPath!!.drawPenStr = mDrawPenStr
                    OperationUtils.savePoints.add(mDrawPath!!)
                    OperationUtils.deletePoints.clear()
                    EventBus.postEvent(Events.WHITE_BOARD_UNDO_REDO)
                    mPath = null
                    postInvalidate()
                }
            }
        }
        return true
    }

    fun setPaint(paint: Paint?) {
        if (paint == null) {
            initPaint()
            postInvalidate()
            return
        }
        mPaint = paint
        postInvalidate()
    }


    fun clearImage() {
        OperationUtils.savePoints.clear()
        OperationUtils.deletePoints.clear()
        val width = mCanvas!!.width
        val height = mCanvas!!.height
        mBottomBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas!!.setBitmap(mBottomBitmap!!)
        postInvalidate()
    }

    fun undo() {
        showPoints()
    }


    fun redo() {
        showPoints()
    }

    /**
     * 重绘一遍路径
     */
    fun showPoints() {
        if (width <= 0 || height <= 0) {
            return
        }
        if (mCanvas == null) {
            mBottomBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBottomBitmap!!)
        }
        val cvWidth = mCanvas!!.width
        val cvHeight = mCanvas!!.height
        mBottomBitmap = Bitmap.createBitmap(cvWidth, cvHeight, Bitmap.Config.ARGB_8888)
        mCanvas!!.setBitmap(mBottomBitmap!!)

        val iter = OperationUtils.savePoints.iterator()
        var temp: DrawPoint
        while (iter.hasNext()) {
            temp = iter.next()
            if (temp.type == OperationUtils.DRAW_PEN) {
                mCanvas!!.drawPath(temp.drawPen!!.path!!, temp.drawPen!!.paint!!)
            }
        }
        postInvalidate()
    }

}
