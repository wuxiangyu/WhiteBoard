package com.github.guanpy.wblib.bean

import com.github.guanpy.wblib.utils.BeanUtil
import com.google.gson.annotations.SerializedName

class DrawPoint {
    /**绘画类型 */
    @SerializedName("mType")
    var type: Int = 0

    /**画笔路径 */
    @SerializedName("mDrawPen")
    var drawPen: DrawPenPoint? = null

    /**文字 */
    @SerializedName("mDrawText")
    var drawText: DrawTextPoint? = null

    /**画笔路径（字符形式） */
    @SerializedName("mDrawPenStr")
    var drawPenStr: DrawPenStr? = null

    companion object {
        /**
         * 拷贝数据，防止引用传递 <br></br>
         * 只在文字情况下使用
         * Created 2015-8-10 16:55:49
         *
         * @author : gpy
         */
        @JvmStatic
        fun copyDrawPoint(drawPoint: DrawPoint): DrawPoint {
            val dp = DrawPoint()
            try {
                dp.type = drawPoint.type
                if (drawPoint.drawText != null) {
                    dp.drawText = drawPoint.drawText!!.copy()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dp
        }
    }
}
