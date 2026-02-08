package com.starry.whiteboard

import android.app.Application
import com.starry.whiteboard.utils.SdCardStatus
import com.starry.whiteboard.utils.StoreUtil
import com.starry.whiteboard.wblib.utils.AppContextUtil
import com.starry.whiteboard.wblib.utils.OperationUtils

/**
 * Created by gpy on 2015/8/17.
 */
class SelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextUtil.init(this)
        SdCardStatus.init(StoreUtil.CACHE_DIR)
        OperationUtils.init()
    }
}
