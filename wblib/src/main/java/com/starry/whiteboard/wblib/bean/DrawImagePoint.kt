package com.starry.whiteboard.wblib.bean

import com.google.gson.annotations.SerializedName

/**
 * 绘画存储-图片
 */
data class DrawImagePoint(
    /**
     * 唯一性标识
     */
    @SerializedName("mId")
    var id: Long = 0,

    /**
     * x坐标
     */
    @SerializedName("mX")
    var x: Float = 0f,

    /**
     * y坐标
     */
    @SerializedName("mY")
    var y: Float = 0f,

    /**
     * 图片宽度
     */
    @SerializedName("mWidth")
    var width: Int = 0,

    /**
     * 图片高度
     */
    @SerializedName("mHeight")
    var height: Int = 0,

    /**
     * 图片路径
     */
    @SerializedName("mImagePath")
    var imagePath: String? = null,

    /**
     * 缩放比例
     */
    @SerializedName("mScale")
    var scale: Float = 1.0f,

    /**
     * 旋转角度
     */
    @SerializedName("mRotation")
    var rotation: Float = 0f,

    /**
     * 当前图片状态
     * 1: View (Normal)
     * 3: Detail (Selected)
     * 4: Delete
     */
    @SerializedName("mStatus")
    var status: Int = 0,

    /**
     * 是否显示
     */
    @SerializedName("mIsVisible")
    var isVisible: Boolean = true
)
