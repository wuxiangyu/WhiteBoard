package com.starry.whiteboard.wblib.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.starry.whiteboard.wblib.R
import com.starry.whiteboard.wblib.bean.DrawPoint
import com.starry.whiteboard.wblib.utils.MultiTouchListener
import com.starry.whiteboard.wblib.utils.OperationUtils
import java.io.File

class DrawImageView(context: Context, drawPoint: DrawPoint?, callBackListener: CallBackListener?) :
    RelativeLayout(context), View.OnClickListener {

    private var mContext: Context? = null
    private var mDrawPoint: DrawPoint? = null
    private var mCallBackListener: CallBackListener? = null
    private var mWidth = 0

    // Views
    private var mVOutside: View? = null
    private var mRlContent: RelativeLayout? = null
    private var mRlImage: RelativeLayout? = null
    private var mIvImage: ImageView? = null
    private var mBtDelete: Button? = null
    
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
        mWidth = mContext!!.resources.displayMetrics.widthPixels
        initUI()
        initEvent()
        switchView(mDrawPoint!!.drawImage!!.status)
    }

    private fun initUI() {
        LayoutInflater.from(mContext).inflate(R.layout.draw_image, this, true)
        mVOutside = findViewById(R.id.v_outside)
        mRlContent = findViewById(R.id.rl_content)
        mRlImage = findViewById(R.id.rl_image)
        mIvImage = findViewById(R.id.iv_image)
        mBtDelete = findViewById(R.id.bt_image_delete)
        mIvRotate = findViewById(R.id.iv_image_rotate)

        if (mDrawPoint?.drawImage != null) {
            val path = mDrawPoint!!.drawImage!!.imagePath
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                     val bitmap = BitmapFactory.decodeFile(path)
                     mIvImage!!.setImageBitmap(bitmap)
                }
            }
            
            mRlContent!!.rotation = mDrawPoint!!.drawImage!!.rotation
            mRlContent!!.scaleX = mDrawPoint!!.drawImage!!.scale
            mRlContent!!.scaleY = mDrawPoint!!.drawImage!!.scale
            
            // Set initial dimensions if available, or let layout handle it?
            // Usually we might want to set layout params based on width/height if stored
            val lp = mRlContent!!.layoutParams
            if (lp != null) {
                 lp.width = mDrawPoint!!.drawImage!!.width
                 lp.height = mDrawPoint!!.drawImage!!.height
                 mRlContent!!.layoutParams = lp
            }
        }
        setLayoutParams()
    }

    private fun setLayoutParams() {
        // Use fixed width/height to prevent layout changes during touch
        val width = if (mDrawPoint!!.drawImage!!.width > 0) mDrawPoint!!.drawImage!!.width else LayoutParams.WRAP_CONTENT
        val height = if (mDrawPoint!!.drawImage!!.height > 0) mDrawPoint!!.drawImage!!.height else LayoutParams.WRAP_CONTENT
        val layParamsTxt = LayoutParams(width, height)
        layParamsTxt.leftMargin = mDrawPoint!!.drawImage!!.x.toInt()
        layParamsTxt.topMargin = mDrawPoint!!.drawImage!!.y.toInt()
        mRlContent!!.layoutParams = layParamsTxt
    }

    private fun initEvent() {
        mBtDelete!!.setOnClickListener(this)
        mIvImage!!.setOnClickListener(this) // Added click listener

        // Rotation handle touch listener
        mIvRotate!!.setOnTouchListener { _, event ->
            if (OperationUtils.DISABLE) {
                val action = event.action
                val rawX = event.rawX
                val rawY = event.rawY
                
                val parentLocation = IntArray(2)
                this.getLocationOnScreen(parentLocation)
                
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
                        // Update rotation in-place without triggering view recreation
                        mDrawPoint!!.drawImage!!.rotation = mRlContent!!.rotation
                        // Also update in savePoints
                        val size = OperationUtils.savePoints.size
                        for (i in size - 1 downTo 0) {
                            val temp = OperationUtils.savePoints[i]
                            if (temp.type == OperationUtils.DRAW_IMAGE && 
                                temp.drawImage!!.id == mDrawPoint!!.drawImage!!.id &&
                                temp.drawImage!!.isVisible) {
                                temp.drawImage!!.rotation = mRlContent!!.rotation
                                break
                            }
                        }
                    }
                }
                true
            } else {
                false
            }
        }

        // Image touch listener - use MultiTouchListener like emoji for smooth pinch-to-zoom
        mIvImage!!.setOnTouchListener(object : OnTouchListener {
            private val multiTouchListener = MultiTouchListener(context, mRlContent, true, object : MultiTouchListener.OnGestureControl {
                override fun onClick() {
                    if (OperationUtils.DISABLE) {
                        switchView(IMAGE_DETAIL)
                    }
                }

                override fun onLongClick() {}
            })

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (mDrawPoint!!.drawImage!!.status == IMAGE_DETAIL && OperationUtils.DISABLE) {
                    multiTouchListener.onTouch(view, event)
                    if (event.action == MotionEvent.ACTION_UP) {
                        // Update position and scale
                        val params = mRlContent!!.layoutParams as LayoutParams
                        val newX = params.leftMargin + mRlContent!!.translationX
                        val newY = params.topMargin + mRlContent!!.translationY
                        
                        // Update in savePoints
                        val size = OperationUtils.savePoints.size
                        for (i in size - 1 downTo 0) {
                            val temp = OperationUtils.savePoints[i]
                            if (temp.type == OperationUtils.DRAW_IMAGE && 
                                temp.drawImage!!.id == mDrawPoint!!.drawImage!!.id &&
                                temp.drawImage!!.isVisible) {
                                temp.drawImage!!.x = newX
                                temp.drawImage!!.y = newY
                                temp.drawImage!!.rotation = mRlContent!!.rotation
                                temp.drawImage!!.scale = mRlContent!!.scaleX
                                break
                            }
                        }
                    }
                    return true
                }
                return false
            }
        })
    }

    fun switchView(currentStatus: Int) {
        when (currentStatus) {
            IMAGE_VIEW -> {
                mVOutside!!.visibility = GONE
                mRlImage!!.setBackgroundResource(R.color.transparent)
                mBtDelete!!.visibility = GONE
                mIvRotate!!.visibility = GONE
            }
            IMAGE_DETAIL -> {
                mVOutside!!.setBackgroundResource(R.color.transparent)
                mVOutside!!.visibility = VISIBLE
                mRlImage!!.setBackgroundResource(R.drawable.draw_text_border)
                mBtDelete!!.visibility = VISIBLE
                mIvRotate!!.visibility = VISIBLE
            }
            IMAGE_DELETE -> {
                // Only DELETE needs to trigger callback and view recreation
                mDrawPoint!!.drawImage!!.status = currentStatus
                mCallBackListener?.onUpdate(mDrawPoint)
                return // Early return, status already updated
            }
        }

        // For IMAGE_VIEW and IMAGE_DETAIL, just update status locally without callback
        // This prevents view recreation which causes size changes
        mDrawPoint!!.drawImage!!.status = currentStatus
    }

    override fun onClick(v: View) {
        val vId = v.id
        if (vId == R.id.bt_image_delete) {
            if (OperationUtils.DISABLE) {
                switchView(IMAGE_DELETE)
            }
        } else if (vId == R.id.v_outside) { // Click outside to deselect
             if (mDrawPoint!!.drawImage!!.status == IMAGE_DETAIL && OperationUtils.DISABLE) {
                switchView(IMAGE_VIEW)
            }
        } else if (vId == R.id.iv_image) {
            if (OperationUtils.DISABLE) {
                switchView(IMAGE_DETAIL)
            }
        }
    }

    interface CallBackListener {
        fun onUpdate(drawPoint: DrawPoint?)
    }

    companion object {
        const val IMAGE_VIEW = 1
        const val IMAGE_DETAIL = 3 // Selected/Detail
        const val IMAGE_DELETE = 4
    }
}
