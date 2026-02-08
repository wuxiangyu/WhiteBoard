package com.starry.whiteboard.wblib.bean

import android.graphics.Paint
import android.graphics.Path

/**
 * 绘画存储-画笔路径
 * Created 2015-7-13 10:36:1
 *
 * @author gpy
 */
data class DrawPenPoint(
    /**
     * 绘画路径
     */
    var path: Path? = null,

    /**
     * 画笔
     */
    var paint: Paint? = null
)
