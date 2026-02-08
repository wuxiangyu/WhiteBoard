package com.starry.whiteboard.wblib.widget

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.starry.whiteboard.wblib.R
import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.utils.MultiTouchListener
import com.starry.whiteboard.wblib.utils.OperationUtils

class DrawEmojiView(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) :
    RelativeLayout(context), View.OnClickListener {

    private var mContext: Context? = null
    private var mDrawPoint: DrawPoint? = null
    private var mCallBackListener: CallBackListener? = null
    private var mWidth = 0

    // Views
    private var mVOutside: View? = null
    private var mRlContent: RelativeLayout? = null
    private var mRlEmoji: RelativeLayout? = null
    private var mTvEmoji: TextView? = null
    private var mBtDelete: Button? = null
    private var mBtEdit: Button? = null

    private var mIvRotate: ImageView? = null
    private var mInitialRotation = 0f
    private var mStartAngle = 0.0

    init {
        init(context, drawPoint, callBackListener)
    }

    private fun init(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) {
        mContext = context
        mDrawPoint = DrawPoint.copyDrawPoint(drawPoint!!)
        mCallBackListener = callBackListener
        val display = (mContext as Activity?)!!.windowManager.defaultDisplay
        mWidth = display.width
        initUI()
        initEvent()
        switchView(mDrawPoint!!.drawEmoji!!.status)
    }

    private fun initUI() {
        LayoutInflater.from(mContext).inflate(R.layout.draw_emoji, this, true)
        mVOutside = findViewById(R.id.v_outside)
        mRlContent = findViewById(R.id.rl_content)
        mRlEmoji = findViewById(R.id.rl_emoji)
        mTvEmoji = findViewById(R.id.tv_emoji)
        mBtDelete = findViewById(R.id.bt_emoji_delete)
        mBtEdit = findViewById(R.id.bt_emoji_edit)
        mIvRotate = findViewById(R.id.iv_emoji_rotate)

        if (mDrawPoint?.drawEmoji != null) {
            mTvEmoji!!.text = mDrawPoint!!.drawEmoji!!.emojiUnicode
            mRlContent!!.rotation = mDrawPoint!!.drawEmoji!!.rotation
            mRlContent!!.scaleX = mDrawPoint!!.drawEmoji!!.scale
            mRlContent!!.scaleY = mDrawPoint!!.drawEmoji!!.scale
        }
        setLayoutParams()
    }

    private fun setLayoutParams() {
        val layParamsTxt = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        layParamsTxt.leftMargin = mDrawPoint!!.drawEmoji!!.x.toInt()
        layParamsTxt.topMargin = mDrawPoint!!.drawEmoji!!.y.toInt()
        mRlContent!!.layoutParams = layParamsTxt
    }

    private fun initEvent() {
        // mVOutside!!.setOnClickListener(this) // Removed to allow pass-through
        mBtDelete!!.setOnClickListener(this)
        mBtEdit!!.setOnClickListener(this)
        mTvEmoji!!.setOnClickListener(this)

        // Rotation handle touch listener
        mIvRotate!!.setOnTouchListener { v, event ->
            if (OperationUtils.DISABLE) {
                val action = event.action
                val rawX = event.rawX
                val rawY = event.rawY
                
                // Get center of view on screen based on PARENT location and child's relative position
                // This avoids the issue where child's rotation affects its own getLocationOnScreen results
                val parentLocation = IntArray(2)
                this.getLocationOnScreen(parentLocation)
                
                // Calculate center relative to screen
                // mRlContent.left/top are layout positions relative to parent (DrawEmojiView)
                // translationX/Y handles any movement done by drag
                val centerX = parentLocation[0] + mRlContent!!.left + mRlContent!!.translationX + mRlContent!!.width / 2.0
                val centerY = parentLocation[1] + mRlContent!!.top + mRlContent!!.translationY + mRlContent!!.height / 2.0

                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        mInitialRotation = mRlContent!!.rotation
                        mStartAngle = Math.toDegrees(Math.atan2(rawY - centerY, rawX - centerX))
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val currentAngle = Math.toDegrees(Math.atan2(rawY - centerY, rawX - centerX))
                        val angleDiff = (currentAngle - mStartAngle).toFloat()
                        mRlContent!!.rotation = mInitialRotation + angleDiff
                    }
                    MotionEvent.ACTION_UP -> {
                         if (mCallBackListener != null) {
                            val params = mRlContent!!.layoutParams as LayoutParams
                            mDrawPoint!!.drawEmoji!!.rotation = mRlContent!!.rotation
                            mCallBackListener!!.onUpdate(mDrawPoint)
                        }
                    }
                }
                true
            } else {
                false
            }
        }

        // Touch listener on TextView/Emoji container that delegates to MultiTouchListener on mRlContent
        mTvEmoji!!.setOnTouchListener(object : OnTouchListener {
            private val multiTouchListener = MultiTouchListener(context, mRlContent, true, object : MultiTouchListener.OnGestureControl {
                override fun onClick() {
                    if (OperationUtils.DISABLE) {
                        switchView(EMOJI_DETAIL)
                    }
                }

                override fun onLongClick() {}
            })

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (mDrawPoint!!.drawEmoji!!.status == EMOJI_DETAIL && OperationUtils.DISABLE) {
                    multiTouchListener.onTouch(view, event)
                    if (event.action == MotionEvent.ACTION_UP && mCallBackListener != null) {
                        // Update position
                        val params = mRlContent!!.layoutParams as LayoutParams
                        mDrawPoint!!.drawEmoji!!.x = (params.leftMargin + mRlContent!!.translationX)
                        mDrawPoint!!.drawEmoji!!.y = (params.topMargin + mRlContent!!.translationY)
                        mDrawPoint!!.drawEmoji!!.rotation = mRlContent!!.rotation
                        mDrawPoint!!.drawEmoji!!.scale = mRlContent!!.scaleX
                        mCallBackListener!!.onUpdate(mDrawPoint)
                    }
                    return true
                }
                return false
            }
        })
    }

    fun switchView(currentStatus: Int) {
        when (currentStatus) {
            EMOJI_VIEW -> {
                mVOutside!!.visibility = GONE
                mRlEmoji!!.setBackgroundResource(R.color.transparent)
                mBtEdit!!.visibility = GONE
                mBtDelete!!.visibility = GONE
                mIvRotate!!.visibility = GONE
            }
            EMOJI_DETAIL -> {
                mVOutside!!.setBackgroundResource(R.color.transparent)
                mVOutside!!.visibility = VISIBLE
                mRlEmoji!!.setBackgroundResource(R.drawable.draw_text_border)
                mBtEdit!!.visibility = VISIBLE
                mBtDelete!!.visibility = VISIBLE
                mIvRotate!!.visibility = VISIBLE
            }
            EMOJI_DELETE -> {
                // Handled by callback optionally logic, but mainly just sets status
            }
        }

        if (mDrawPoint!!.drawEmoji!!.status != currentStatus) {
            mDrawPoint!!.drawEmoji!!.status = currentStatus
            mCallBackListener?.onUpdate(mDrawPoint)
        }
    }

    override fun onClick(v: View) {
        val vId = v.id
        if (vId == R.id.v_outside) {
            if (mDrawPoint!!.drawEmoji!!.status == EMOJI_DETAIL && OperationUtils.DISABLE) {
                switchView(EMOJI_VIEW)
            }
        } else if (vId == R.id.tv_emoji) {
            if (OperationUtils.DISABLE) {
                switchView(EMOJI_DETAIL)
            }
        } else if (vId == R.id.bt_emoji_delete) {
            if (OperationUtils.DISABLE) {
                switchView(EMOJI_DELETE)
            }
        } else if (vId == R.id.bt_emoji_edit) {
            if (OperationUtils.DISABLE) {
                mCallBackListener?.onEdit(mDrawPoint)
            }
        }
    }

    interface CallBackListener {
        fun onUpdate(drawPoint: DrawPoint?)
        fun onEdit(drawPoint: DrawPoint?) // Added for re-opening picker
    }

    companion object {
        const val EMOJI_VIEW = 1
        const val EMOJI_DETAIL = 3 // Selected/Detail
        const val EMOJI_DELETE = 4
    }
}
