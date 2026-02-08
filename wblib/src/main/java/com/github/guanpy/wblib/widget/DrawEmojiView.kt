package com.github.guanpy.wblib.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.guanpy.wblib.R
import com.github.guanpy.wblib.bean.DrawPoint
import com.github.guanpy.wblib.utils.MultiTouchListener
import com.github.guanpy.wblib.utils.OperationUtils

class DrawEmojiView : RelativeLayout {

    private var mContext: Context? = null
    private var mDrawPoint: DrawPoint? = null
    private var mCallBackListener: CallBackListener? = null

    private lateinit var mTvEmoji: TextView
    private lateinit var mIvDelete: ImageView

    constructor(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) : super(context) {
        init(context, drawPoint, callBackListener)
    }

    private fun init(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) {
        mContext = context
        mDrawPoint = DrawPoint.copyDrawPoint(drawPoint!!)
        mCallBackListener = callBackListener
        initUI()
        initEvent()
    }

    private fun initUI() {
        LayoutInflater.from(mContext).inflate(R.layout.draw_emoji, this, true)
        mTvEmoji = findViewById(R.id.tv_emoji)
        mIvDelete = findViewById(R.id.iv_delete)

        if (mDrawPoint?.drawEmoji != null) {
            mTvEmoji.text = mDrawPoint!!.drawEmoji!!.emojiUnicode
            this.x = mDrawPoint!!.drawEmoji!!.x
            this.y = mDrawPoint!!.drawEmoji!!.y
            this.scaleX = mDrawPoint!!.drawEmoji!!.scale
            this.scaleY = mDrawPoint!!.drawEmoji!!.scale
            this.rotation = mDrawPoint!!.drawEmoji!!.rotation
        }
    }

    private fun initEvent() {
        mIvDelete.setOnClickListener {
            mDrawPoint!!.drawEmoji!!.isVisible = false
            mCallBackListener?.onUpdate(mDrawPoint)
        }

        val multiTouchListener = MultiTouchListener(context, this, true, object : MultiTouchListener.OnGestureControl {
            override fun onClick() {
                // Toggle delete button visibility or selection state
                if (OperationUtils.DISABLE) {
                    if (mIvDelete.visibility == View.VISIBLE) {
                        mIvDelete.visibility = View.GONE
                        this@DrawEmojiView.setBackgroundResource(0)
                    } else {
                        mIvDelete.visibility = View.VISIBLE
                        this@DrawEmojiView.setBackgroundResource(R.drawable.draw_text_border)
                    }
                }
            }

            override fun onLongClick() {}
        })

        this.setOnTouchListener { view, event ->
            if (OperationUtils.DISABLE) {
                val ret = multiTouchListener.onTouch(view, event)
                if (event.action == MotionEvent.ACTION_UP) {
                    // Update point data
                    mDrawPoint!!.drawEmoji!!.x = this.translationX + this.left
                    mDrawPoint!!.drawEmoji!!.y = this.translationY + this.top
                    mDrawPoint!!.drawEmoji!!.scale = this.scaleX
                    mDrawPoint!!.drawEmoji!!.rotation = this.rotation
                    mCallBackListener?.onUpdate(mDrawPoint)
                }
                ret
            } else {
                false
            }
        }
    }

    interface CallBackListener {
        fun onUpdate(drawPoint: DrawPoint?)
    }
}
