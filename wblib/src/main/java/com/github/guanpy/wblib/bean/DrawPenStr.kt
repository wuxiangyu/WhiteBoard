package com.github.guanpy.wblib.bean

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 * 绘画存储-画笔路径(字符串格式，方便储存)
 * Created 2015-7-13 10:36:1
 *
 * @author gpy
 */
data class DrawPenStr(
    /**画笔颜色 */
    @SerializedName("mColor")
    var color: Int = 0,

    /**画笔粗细 */
    @SerializedName("mStrokeWidth")
    var strokeWidth: Float = 0f,

    /**是否橡皮擦 */
    @SerializedName("mIsEraser")
    var isEraser: Boolean = false,

    /**移动到初始点坐标 */
    @SerializedName("mMoveTo")
    var moveTo: Point? = null,

    /**移动中A集 */
    @SerializedName("mQuadToA")
    var quadToA: MutableList<Point> = ArrayList(),

    /**移动中B集 */
    @SerializedName("mQuadToB")
    var quadToB: MutableList<Point> = ArrayList(),

    /**移动到终点坐标 */
    @SerializedName("mLineTo")
    var lineTo: Point? = null,

    /**所在界面高距坐标 */
    @SerializedName("mOffset")
    var offset: Point? = null
)
