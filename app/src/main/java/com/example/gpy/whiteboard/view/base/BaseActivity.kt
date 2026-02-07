package com.example.gpy.whiteboard.view.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.github.guanpy.library.EventBus

/**
 * Created by gpy on 2016/2/17.
 */
abstract class BaseActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(this.layoutId)
        this.afterCreate(savedInstanceState)
    }

    protected abstract val layoutId: Int
    protected abstract fun afterCreate(savedInstanceState: Bundle?)
    protected fun showMessage(msg: CharSequence?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        EventBus.registerAnnotatedReceiver(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.unregisterAnnotatedReceiver(this)
    }

    fun navi2Page(page: Class<*>) {
        this.navi2Page(page, false)
    }

    fun navi2Page(page: Class<*>, shut: Boolean) {
        this.navi2Page(page, null, shut)
    }

    fun navi2Page(page: Class<*>, data: Bundle?, shut: Boolean) {
        val intent = Intent(this, page)
        if (null != data) {
            intent.putExtras(data)
        }
        this.startActivity(intent)
        if (shut) {
            this.finish()
        }
    }
}
