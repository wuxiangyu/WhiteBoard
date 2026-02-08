package com.github.guanpy.wblib.bean

import com.google.gson.annotations.SerializedName

/**
 * 绘画存储-Emoji
 */
data class DrawEmojiPoint(
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
     * Emoji Unicode
     */
    @SerializedName("mEmojiUnicode")
    var emojiUnicode: String? = null,

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
     * 当前Emoji状态
     */
    @SerializedName("mStatus")
    var status: Int = 0,

    /**
     * 是否显示
     */
    @SerializedName("mIsVisible")
    var isVisible: Boolean = true
)
