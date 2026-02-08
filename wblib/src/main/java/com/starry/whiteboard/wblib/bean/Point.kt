package com.starry.whiteboard.wblib.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by gpy on 2015/8/21.
 */
data class Point(
    /**
     * X坐标
     */
    @SerializedName("mX")
    var x: Float = 0f,

    /**
     * Y坐标
     */
    @SerializedName("mY")
    var y: Float = 0f
)
