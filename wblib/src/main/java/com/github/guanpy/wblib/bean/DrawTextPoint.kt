package com.github.guanpy.wblib.bean

import com.google.gson.annotations.SerializedName

/**
 * 绘画存储-文字
 * Created 2015-7-13 10:36:1
 *
 * @author gpy
 */
data class DrawTextPoint(
    /**
     * 唯一性标识
     */
    @SerializedName("mId")
    var id: Long = 0,

    /**
     * 文字x坐标
     */
    @SerializedName("mX")
    var x: Float = 0f,

    /**
     * 文字y坐标
     */
    @SerializedName("mY")
    var y: Float = 0f,

    /**
     * 文字
     */
    @SerializedName("mStr")
    var str: String? = null,

    /**
     * 是否有下划线
     */
    @SerializedName("mIsUnderline")
    var isUnderline: Boolean = false,

    /**
     * 是否斜体
     */
    @SerializedName("mIsItalics")
    var isItalics: Boolean = false,

    /**
     * 是否粗体
     */
    @SerializedName("mIsBold")
    var isBold: Boolean = false,

    /**
     * 文字颜色
     */
    @SerializedName("mColor")
    var color: Int = 0,

    /**
     * 当前文字状态
     */
    @SerializedName("mStatus")
    var status: Int = 0,

    /**
     * 是否显示
     */
    @SerializedName("mIsVisible")
    var isVisible: Boolean = false,

    /**
     * 缩放比例
     */
    @SerializedName("mScale")
    var scale: Float = 1.0f,

    /**
     * 旋转角度
     */
    @SerializedName("mRotation")
    var rotation: Float = 0f
)
