package com.github.guanpy.wblib.utils

import android.content.Context

/**
 * AppContextUtil
 *
 * @author yangz
 * @version 2014/11/4
 */
object AppContextUtil {

    private var sContext: Context? = null

    @JvmStatic
    fun init(ctx: Context) {
        sContext = ctx.applicationContext
    }

    @JvmStatic
    fun getContext(): Context? {
        return sContext
    }

    @JvmStatic
    fun getColor(resId: Int): Int {
        return sContext?.resources?.getColor(resId) ?: -1
    }

    @JvmStatic
    fun getString(resId: Int): String? {
        return sContext?.resources?.getString(resId)
    }

    @JvmStatic
    fun getString(resId: Int, vararg objs: Any): String? {
        return sContext?.getString(resId, *objs)
    }

    /**
     * dip转px
     */
    @JvmStatic
    fun dip2px(dpValue: Float): Int {
        val scale = sContext?.resources?.displayMetrics?.density ?: return 0
        return (dpValue * scale + 0.5f).toInt()
    }
}
