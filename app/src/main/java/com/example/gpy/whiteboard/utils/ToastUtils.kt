package com.example.gpy.whiteboard.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by gpy on 2016/4/11.
 */
object ToastUtils {

    @JvmStatic
    fun showToast(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showToast(context: Context, intStr: Int) {
        Toast.makeText(context, intStr, Toast.LENGTH_SHORT).show()
    }
}
