package com.github.guanpy.wblib.bean

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 * 画板绘画路径存储
 *
 * @author gpy
 */
data class WhiteBoardPoint(
    /**
     * 画板id
     */
    @SerializedName("mId")
    var id: Int = 0,

    /**
     * 保存路径
     */
    @SerializedName("mSavePoints")
    var savePoints: MutableList<DrawPoint> = ArrayList(),

    /**
     * 撤销路径
     */
    @SerializedName("mDeletePoints")
    var deletePoints: MutableList<DrawPoint> = ArrayList()
)
