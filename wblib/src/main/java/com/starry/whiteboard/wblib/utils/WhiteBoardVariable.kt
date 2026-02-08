package com.starry.whiteboard.wblib.utils

import com.starry.whiteboard.wblib.R

/**
 * 白板属性值汇总
 * Created by gpy on 2015/8/17.
 */
object WhiteBoardVariable {
    /**白板工具栏操作 */
    object Operation {
        /**画笔-正常 */
        const val PEN_NORMAL = 1

        /**画笔-点击 */
        const val PEN_CLICK = 2

        /**画笔-展开 */
        const val PEN_EXPAND = 3

        /**颜色-正常 */
        const val COLOR_NORMAL = 4

        /**颜色-点击 */
        const val COLOR_CLICK = 5

        /**颜色-展开 */
        const val COLOR_EXPAND = 6

        /**文字-正常 */
        const val TEXT_NORMAL = 7

        /**文字-点击 */
        const val TEXT_CLICK = 8

        /**文字-展开 */
        const val TEXT_EXPAND = 9

        /**橡皮擦-正常 */
        const val ERASER_NORMAL = 10

        /**橡皮擦-点击 */
        const val ERASER_CLICK = 11

        /**橡皮擦-展开 */
        const val ERASER_EXPAND = 12

        /**点击外围 */
        const val OUTSIDE_CLICK = 13
    }


    /**颜色 */
    object Color {
        /**黑色 */
        @JvmField
        val BLACK = AppContextUtil.getColor(R.color.grey_3e)

        /**橙色 */
        @JvmField
        val ORANGE = AppContextUtil.getColor(R.color.orange)

        /**粉色 */
        @JvmField
        val PINK = AppContextUtil.getColor(R.color.light_red)

        /**紫色 */
        @JvmField
        val PURPLE = AppContextUtil.getColor(R.color.primary_purple)

        /**绿色 */
        @JvmField
        val GREEN = AppContextUtil.getColor(R.color.green)
    }

    /**画笔大小 */
    object PenSize {
        /**大 */
        @JvmField
        val LARRGE = AppContextUtil.dip2px(9f)

        /**中 */
        @JvmField
        val MIDDLE = AppContextUtil.dip2px(6f)

        /**小 */
        @JvmField
        val MINI = AppContextUtil.dip2px(4f)
    }

    /**橡皮擦大小 */
    object EraserSize {
        /**大 */
        @JvmField
        val LARRGE = AppContextUtil.dip2px(16f)

        /**中 */
        @JvmField
        val MIDDLE = AppContextUtil.dip2px(9f)

        /**小 */
        @JvmField
        val MINI = AppContextUtil.dip2px(4f)
    }

    /**圆环宽度大小 */
    object RingSize {
        /**大 */
        @JvmField
        val LARRGE = AppContextUtil.dip2px(3f)

        /**小 */
        @JvmField
        val MINI = AppContextUtil.dip2px(1f)
    }

    /**文字风格 */
    object TextStyle {
        /**设置下划线 */
        const val CHANGE_UNDERLINE = 1

        /**设置斜体 */
        const val CHANGE_ITALICS = 2

        /**设置粗体 */
        const val CHANGE_BOLD = 3
    }
}
