package com.starry.whiteboard.wblib.widget

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.Spannable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.starry.whiteboard.wblib.EventBus
import com.starry.whiteboard.wblib.R
import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.utils.Events
import com.starry.whiteboard.wblib.utils.MultiTouchListener
import com.starry.whiteboard.wblib.utils.OperationUtils

class DrawTextView(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) :
    RelativeLayout(context), View.OnClickListener {
    /** */
    private var mVOutside: View? = null

    /** */
    private var mRlContent: RelativeLayout? = null

    /** */
    private var mRlText: RelativeLayout? = null

    /** */
    private var mEtTextEdit: EditText? = null

    /** */
    private var mTvTextEdit: TextView? = null

    /** */
    private var mBtTextDelete: Button? = null

    /** */
    private var mBtTextEdit: Button? = null
    private var mContext: Context? = null
    private var mCallBackListener: CallBackListener? = null
    private var mDrawPoint: DrawPoint? = null
    private var mWidth = 0

    /**
     * 特殊字符所需
     */
    private val mSpannable: Spannable? = null

    init {
        init(context, drawPoint, callBackListener)
    }

    private fun init(
        context: Context,
        drawPoint: DrawPoint?,
        callBackListener: CallBackListener?
    ) {
        mContext = context
        mDrawPoint = DrawPoint.copyDrawPoint(drawPoint!!)
        mCallBackListener = callBackListener
        val display = (mContext as Activity?)!!.windowManager
            .defaultDisplay
        mWidth = display.width
        initUI()
        initEvent()
        switchView(mDrawPoint!!.drawText!!.status)
    }

    /**
     * 初始化界面控件 <br></br>
     * Created 2015-8-10 16:55:49
     *
     * @author : gpy
     */
    private fun initUI() {
        LayoutInflater.from(mContext).inflate(R.layout.draw_text, this, true)
        mVOutside = findViewById<View>(R.id.v_outside)
        mRlContent = findViewById<RelativeLayout>(R.id.rl_content)
        mRlText = findViewById<RelativeLayout>(R.id.rl_text)
        mEtTextEdit = findViewById<EditText>(R.id.et_text_edit)
        mTvTextEdit = findViewById<TextView>(R.id.tv_text_edit)
        mBtTextDelete = findViewById<Button>(R.id.bt_text_delete)
        mBtTextEdit = findViewById<Button>(R.id.bt_text_edit)
        if (null != mDrawPoint) {
            setText(mDrawPoint!!.drawText!!.str)
            mRlContent!!.rotation = mDrawPoint!!.drawText!!.rotation
            mRlContent!!.scaleX = mDrawPoint!!.drawText!!.scale
            mRlContent!!.scaleY = mDrawPoint!!.drawText!!.scale
        }
        setLayoutParams()
    }

    /**
     * 初始化监听 <br></br>
     * Created 2015-8-10 16:55:49
     *
     * @author : gpy
     */
    private fun initEvent() {
        // mVOutside!!.setOnClickListener(this) // Removed global listener, managed in switchView
        // mRlText!!.setOnClickListener(this) // Removed to prevent blocking touches
        mEtTextEdit!!.setOnClickListener(this)
        mBtTextDelete!!.setOnClickListener(this)
        mBtTextEdit!!.setOnClickListener(this)
        mTvTextEdit!!.setOnClickListener(this)
        mTvTextEdit!!.setOnTouchListener(object : OnTouchListener {
            private val multiTouchListener = MultiTouchListener(context, mRlContent, true, object : MultiTouchListener.OnGestureControl {
                override fun onClick() {
                    if (OperationUtils.DISABLE) {
                        switchView(TEXT_DETAIL)
                    }
                }

                override fun onLongClick() {}
            })

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (mDrawPoint!!.drawText!!.status == TEXT_DETAIL && OperationUtils.DISABLE) {
                    multiTouchListener.onTouch(view, event)
                    if (event.action == MotionEvent.ACTION_UP && mCallBackListener != null) {
                        // Update position in DrawPoint
                        val params = mRlContent!!.layoutParams as LayoutParams
                        mDrawPoint!!.drawText!!.x = params.leftMargin + mRlContent!!.translationX
                        mDrawPoint!!.drawText!!.y = params.topMargin + mRlContent!!.translationY
                        mDrawPoint!!.drawText!!.rotation = mRlContent!!.rotation
                        mDrawPoint!!.drawText!!.scale = mRlContent!!.scaleX
                        mCallBackListener!!.onUpdate(mDrawPoint)
                    }
                    return true
                }
               return false
            }
        })
    }

    private fun setText(strText: String?) {
        if (!TextUtils.isEmpty(strText)) {
            mEtTextEdit!!.setText(strText)
            mTvTextEdit!!.text = strText
        }
        mEtTextEdit!!.setTextColor(mDrawPoint!!.drawText!!.color)
        mTvTextEdit!!.setTextColor(mDrawPoint!!.drawText!!.color)
        if (mDrawPoint!!.drawText!!.isUnderline) {
            mTvTextEdit!!.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            mEtTextEdit!!.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        }
        if (mDrawPoint!!.drawText!!.isBold) {
            mTvTextEdit!!.paint.isFakeBoldText = true
            mEtTextEdit!!.paint.isFakeBoldText = true
        }
    }

    private fun setLayoutParams() {
        val layParamsTxt = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        layParamsTxt.leftMargin = mDrawPoint!!.drawText!!.x.toInt()
        layParamsTxt.topMargin = mDrawPoint!!.drawText!!.y.toInt()
        mRlContent!!.layoutParams = layParamsTxt
    }

    fun switchView(currentStatus: Int) {
        when (currentStatus) {
            TEXT_VIEW -> {
                mVOutside!!.visibility = GONE
                mEtTextEdit!!.visibility = GONE
                mTvTextEdit!!.visibility = VISIBLE
                mRlText!!.setBackgroundResource(
                    R.color.transparent
                )
                mBtTextEdit!!.visibility = GONE
                mBtTextDelete!!.visibility = GONE
            }

            TEXT_EDIT -> {
                mVOutside!!.setBackgroundResource(R.color.white)
                mVOutside!!.visibility = VISIBLE
                mVOutside!!.setOnClickListener(this) // Enable blocking for Edit mode
                mVOutside!!.isClickable = true
                mEtTextEdit!!.visibility = VISIBLE
                mTvTextEdit!!.visibility = GONE
                mRlText!!.setBackgroundResource(R.drawable.draw_text_border)
                mBtTextEdit!!.visibility = GONE
                mEtTextEdit!!.setSelection(mEtTextEdit!!.text.length)
                mBtTextDelete!!.visibility = GONE
                EventBus.postEvent(Events.WHITE_BOARD_TEXT_EDIT)
                showSoftKeyBoard(mEtTextEdit)
            }

            TEXT_DETAIL -> {
                mVOutside!!.setBackgroundResource(R.color.transparent)
                mVOutside!!.visibility = VISIBLE
                mVOutside!!.setOnClickListener(null) // Disable blocking for Detail mode
                mVOutside!!.isClickable = false
                mEtTextEdit!!.visibility = GONE
                mTvTextEdit!!.visibility = VISIBLE
                mRlText!!.setBackgroundResource(R.drawable.draw_text_border)
                mBtTextEdit!!.visibility = VISIBLE
                mBtTextDelete!!.visibility = VISIBLE
            }

            TEXT_DELETE -> {
            }

            else -> {
            }
        }
        Log.d("gpy", "文字宽：" + mRlText!!.height)
        if (mDrawPoint!!.drawText!!.status != currentStatus) {
            mDrawPoint!!.drawText!!.status = currentStatus
            if (null != mCallBackListener && currentStatus != TEXT_EDIT) {
                mCallBackListener!!.onUpdate(mDrawPoint)
            }
        }
    }

    /**
     * 文字编辑完成
     *
     * @param isSave 是否保存
     */
    fun afterEdit(isSave: Boolean) {
        Log.d("gpy", "要保存的文字：" + mEtTextEdit!!.text.toString())
        if (isSave) {
            mDrawPoint!!.drawText!!.str = mEtTextEdit!!.text.toString()
            setText(mDrawPoint!!.drawText!!.str)

            // Sanity check coordinates to prevent text disappearing off-screen
            val display = (mContext as Activity?)!!.windowManager.defaultDisplay
            val params = mRlContent!!.layoutParams as LayoutParams
            var newX = params.leftMargin + mRlContent!!.translationX
            var newY = params.topMargin + mRlContent!!.translationY

            if (newX < 0 || newX > display.width) {
                 newX = 100f // Reset to visible area
                 mRlContent!!.translationX = 0f
                 params.leftMargin = 100
                 mRlContent!!.layoutParams = params
            }
             if (newY < 0 || newY > display.height) {
                 newY = 100f
                 mRlContent!!.translationY = 0f
                 params.topMargin = 100
                 mRlContent!!.layoutParams = params
            }
            
            mDrawPoint!!.drawText!!.x = newX
            mDrawPoint!!.drawText!!.y = newY
            
            // Do NOT call onUpdate here to avoid sending TEXT_EDIT status.
            // switchView(TEXT_VIEW) will update status and trigger onUpdate.
        }
        
        val needUpdate = mDrawPoint!!.drawText!!.status == TEXT_VIEW && isSave
        switchView(TEXT_VIEW)
        
        // If status didn't change (was already TEXT_VIEW), switchView won't trigger update.
        // We must ensure the new text/position is saved.
        if (needUpdate && mCallBackListener != null) {
            mCallBackListener!!.onUpdate(mDrawPoint)
        }
        
        hideSoftInput()
    }

    override fun onClick(v: View) {
        val vId = v.id
        if (vId == R.id.v_outside) {
            if (mDrawPoint!!.drawText!!.status == TEXT_DETAIL && OperationUtils.DISABLE) {
                switchView(TEXT_VIEW)
            }
            hideSoftInput()
        } else if (vId == R.id.tv_text_edit) {
            if (OperationUtils.DISABLE) {
                switchView(TEXT_DETAIL)
            }
        } else if (vId == R.id.bt_text_delete) {
            if (OperationUtils.DISABLE) {
                switchView(TEXT_DELETE)
            }
        } else if (vId == R.id.bt_text_edit) {
            if (OperationUtils.DISABLE) {
                switchView(TEXT_EDIT)
            }
        }
    }

    interface CallBackListener {
        /**
         * 更新文字属性
         */
        fun onUpdate(drawPoint: DrawPoint?)
    }

    private fun showSoftKeyBoard(et: EditText?) {
        et!!.requestFocus()
        et.post { // 弹出输入法
            val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }

    private fun hideSoftInput() {
        if (this == null || mContext == null || mEtTextEdit == null) {
            return
        }
        // 隐藏输入法
        (mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(mEtTextEdit!!.windowToken, 0)
    }

    companion object {
        /**
         * 显示状态
         */
        const val TEXT_VIEW = 1

        /**
         * 编辑（文字编辑）状态
         */
        const val TEXT_EDIT = 2

        /**
         * 详情（显示删除、编辑按钮）状态
         */
        const val TEXT_DETAIL = 3

        /**
         * 被删除状态
         */
        const val TEXT_DELETE = 4
    }
}
