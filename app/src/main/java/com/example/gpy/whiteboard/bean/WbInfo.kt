package com.example.gpy.whiteboard.bean

import java.io.File

/**
 * Created by gpy on 2016/4/22.
 */
data class WbInfo(
    /**
     * 文件名
     */
    var name: String? = null,
    /**
     * 文件
     */
    var file: File? = null
)
