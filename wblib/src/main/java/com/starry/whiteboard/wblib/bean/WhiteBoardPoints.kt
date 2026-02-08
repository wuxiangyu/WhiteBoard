package com.starry.whiteboard.wblib.bean

import com.google.gson.annotations.SerializedName

/**
 * 画板合集
 * Created by gpy on 2015/8/21.
 */
data class WhiteBoardPoints(
    /**唯一性id */
    @SerializedName("mId")
    var id: String? = null,

    /**画板合集 */
    @SerializedName("mWhiteBoardPoints")
    var whiteBoardPoints: MutableList<WhiteBoardPoint>? = null
)
