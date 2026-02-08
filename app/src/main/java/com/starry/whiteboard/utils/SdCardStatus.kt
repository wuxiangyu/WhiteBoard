package com.starry.whiteboard.utils

import com.starry.whiteboard.wblib.utils.AppContextUtil
import java.io.File

/**
 * Created by gpy on 2015/10/20.
 */
object SdCardStatus {
    private var CACHE_FOLDER_NAME: String? = null
    private const val NONE_SD_CARD_PROMPT = "您的手机中sd卡不存在"

    @JvmStatic
    fun init(cacheFolderName: String?) {
        CACHE_FOLDER_NAME = cacheFolderName
    }

    @JvmStatic
    @Throws(IllegalStateException::class)
    fun getDefaulstCacheDirInSdCard(): String {
        val sdCardPath = getSDPath() ?: throw IllegalStateException(NONE_SD_CARD_PROMPT)
        return sdCardPath + File.separator + CACHE_FOLDER_NAME
    }

    /**
     * when not exist sd card,return null.
     *
     * @return
     */
    @JvmStatic
    fun getSDPath(): String? {
        val context = AppContextUtil.getContext() ?: return null
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            return externalFilesDir.absolutePath
        }
        return null
    }
}
