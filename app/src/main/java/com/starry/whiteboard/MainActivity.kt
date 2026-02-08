package com.starry.whiteboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.starry.whiteboard.utils.FileUtil
import com.starry.whiteboard.utils.StoreUtil
import com.starry.whiteboard.utils.ToastUtils
import com.starry.whiteboard.view.WhiteBoardActivity
import com.starry.whiteboard.view.base.BaseActivity
import com.starry.whiteboard.wblib.utils.OperationUtils
import java.io.File
import java.util.ArrayList

class MainActivity : BaseActivity() {

    private lateinit var mLv: ListView
    private lateinit var mIvAdd: ImageView
    private lateinit var mWbAdapter: WbAdapter
    private var filenames: ArrayList<String>? = null
    private var filepaths: ArrayList<String>? = null
    private var mBackPressedTime: Long = 0

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun afterCreate(savedInstanceState: Bundle?) {
        initView()
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mCurrentTime = System.currentTimeMillis()
                if (mCurrentTime - mBackPressedTime > 1000) {
                    ToastUtils.showToast(this@MainActivity, R.string.app_logout)
                    mBackPressedTime = mCurrentTime
                    return
                }
                finish()
                System.exit(0)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val folder = File(StoreUtil.wbPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val files = folder.listFiles()
        if (files != null && files.isNotEmpty()) {
            filenames = ArrayList()
            filepaths = ArrayList()
            for (f in files) {
                filenames!!.add(FileUtil.getFileName(f))
                filepaths!!.add(f.absolutePath)
            }
        }
        if (::mWbAdapter.isInitialized) {
            mWbAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        mLv = findViewById(R.id.lv_wb)
        mIvAdd = findViewById(R.id.iv_wb_add)
        mWbAdapter = WbAdapter()
        mLv.adapter = mWbAdapter
        mIvAdd.setOnClickListener {
            OperationUtils.initDrawPointList()
            navi2Page(WhiteBoardActivity::class.java)
        }
    }

    private inner class WbAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return if (filenames != null) filenames!!.size else 0
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            var holder: WbViewHolder? = null
            if (view != null) {
                holder = view.tag as WbViewHolder
            } else {
                view = LayoutInflater.from(this@MainActivity).inflate(R.layout.wb_item, null)
                if (view != null) {
                    holder = WbViewHolder(view)
                    view.tag = holder
                }
            }
            if (holder != null) {
                holder.nWbName.text = filenames!![position]
                    view!!.setOnClickListener {
                        StoreUtil.readWhiteBoardPoints(filepaths!![position])
                        val bundle = Bundle()
                        bundle.putBoolean("KEEP_POINTS", true)
                        navi2Page(WhiteBoardActivity::class.java, bundle, false)
                    }
            }
            return view!!
        }
    }

    private class WbViewHolder(view: View) {
        val nWbName: TextView = view.findViewById(R.id.tv_wb_name)
    }
}
