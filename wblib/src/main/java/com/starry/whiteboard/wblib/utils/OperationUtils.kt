package com.starry.whiteboard.wblib.utils

import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.bean.WhiteBoardPoint
import com.starry.whiteboard.wblib.bean.WhiteBoardPoints
import java.util.ArrayList

/**
 * 白板操作公共类
 *
 * @author gpy
 */
object OperationUtils {
    /**
     * 操作类型-画笔
     */
    const val DRAW_PEN = 1

    /**
     * 操作类型-文字
     */
    const val DRAW_TEXT = 2

    /**
     * 操作类型-橡皮擦
     */
    const val DRAW_ERASER = 3

    /**
     * 操作类型-Emoji
     */
    const val DRAW_EMOJI = 4

    /**
     * 当前是否禁止白板操作
     */
    @JvmField
    var DISABLE: Boolean = true

    /**
     * 当前所在白板位置
     */
    @JvmField
    var mCurrentIndex: Int = 0

    /**
     * 每次操作的唯一标识
     */
    private var markId: Long = 0

    /**
     * 当前按钮点击
     */
    @JvmField
    var mCurrentOPerationPen: Int = WhiteBoardVariable.Operation.PEN_CLICK

    /**
     * 当前按钮点击
     */
    @JvmField
    var mCurrentOPerationColor: Int = WhiteBoardVariable.Operation.COLOR_NORMAL

    /**
     * 当前按钮点击
     */
    @JvmField
    var mCurrentOPerationText: Int = WhiteBoardVariable.Operation.TEXT_NORMAL

    /**
     * 当前按钮点击
     */
    @JvmField
    var mCurrentOPerationEraser: Int = WhiteBoardVariable.Operation.ERASER_NORMAL

    /**
     * 当前绘画类型：笔或者文字等
     */
    @JvmField
    var mCurrentDrawType: Int = DRAW_PEN

    /**
     * 当前颜色
     */
    @JvmField
    var mCurrentColor: Int = WhiteBoardVariable.Color.ORANGE

    /**
     * 当前画笔大小
     */
    @JvmField
    var mCurrentPenSize: Int = WhiteBoardVariable.PenSize.MIDDLE

    /**
     * 当前橡皮擦大小
     */
    @JvmField
    var mCurrentEraserSize: Int = WhiteBoardVariable.EraserSize.MIDDLE

    /**
     * 白板操作集
     */
    private var mWhiteBoardPoints: WhiteBoardPoints? = null

    /**
     * 单例
     */
    @JvmStatic
    fun getInstance(): OperationUtils {
        return this
    }

    /**
     * 初始化操作集
     */
    fun init(keepPoints: Boolean = false) {
        DISABLE = true
        mCurrentIndex = 0
        markId = 0
        mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_NORMAL
        mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_NORMAL
        mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_NORMAL
        mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_NORMAL
        mCurrentDrawType = DRAW_PEN
        mCurrentColor = WhiteBoardVariable.Color.ORANGE
        mCurrentPenSize = WhiteBoardVariable.PenSize.MIDDLE
        mCurrentEraserSize = WhiteBoardVariable.EraserSize.MIDDLE
        if (!keepPoints) {
            initDrawPointList()
        }
    }

    /**
     * 返回指定白板的操作集
     */
    fun getDrawPointList(i: Int): WhiteBoardPoint {
        if (null != mWhiteBoardPoints) {
            return if (mWhiteBoardPoints!!.whiteBoardPoints!!.size <= i) {
                val drawPointList = WhiteBoardPoint()
                drawPointList.id = i
                mWhiteBoardPoints!!.whiteBoardPoints!!.add(drawPointList)
                getDrawPointList(i)
            } else {
                mWhiteBoardPoints!!.whiteBoardPoints!![i]
            }
        } else {
            mWhiteBoardPoints = WhiteBoardPoints()
            mWhiteBoardPoints!!.whiteBoardPoints = ArrayList()
            return getDrawPointList(i)
        }
    }

    /**
     * 返回指定白板的操作集大小
     */
    val drawPointSize: Int
        get() {
            return if (mWhiteBoardPoints != null && null != mWhiteBoardPoints!!.whiteBoardPoints) {
                mWhiteBoardPoints!!.whiteBoardPoints!!.size
            } else {
                getDrawPointList(mCurrentIndex)
                drawPointSize
            }
        }

    /**
     * 返回指定白板的操作集
     */
    fun getWhiteBoardPoints(): WhiteBoardPoints? {
        return mWhiteBoardPoints
    }

    /**
     * 返回指定白板的操作集
     */
    fun setWhiteBoardPoints(whiteBoardPoints: WhiteBoardPoints?) {
        mWhiteBoardPoints = whiteBoardPoints
    }

    /**
     * 初始化白板
     */
    fun initDrawPointList() {
        if (mWhiteBoardPoints != null && mWhiteBoardPoints!!.whiteBoardPoints != null) {
            mWhiteBoardPoints!!.whiteBoardPoints!!.clear()
        } else {
            getDrawPointList(mCurrentIndex)
        }
    }

    /**
     * 新建白板
     */
    fun newPage() {
        mCurrentIndex = drawPointSize
        getDrawPointList(mCurrentIndex)
    }


    /**
     * 获取每次操作的唯一标识
     */
    val newMarkId: Long
        get() = markId++

    /**
     * 获取当前白板操作集
     */
    val savePoints: MutableList<DrawPoint>
        get() = getDrawPointList(mCurrentIndex).savePoints

    /**
     * 获取当前白板删除操作集
     */
    val deletePoints: MutableList<DrawPoint>
        get() = getDrawPointList(mCurrentIndex).deletePoints
}
