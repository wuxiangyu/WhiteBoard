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

        // Image touch listener - simple drag without scale interference
        mIvImage!!.setOnTouchListener(object : OnTouchListener {
            private var mPrevX = 0f
            private var mPrevY = 0f
            private var mActivePointerId = -1
            private val TAG = "DrawImageView"
            private var mIsPinching = false // Track if we're actually pinching
            
            private val mScaleDetector = android.view.ScaleGestureDetector(context, object : android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScaleBegin(detector: android.view.ScaleGestureDetector): Boolean {
                    // Only begin scaling if we have 2+ fingers
                    mIsPinching = true
                    android.util.Log.d(TAG, "onScaleBegin - mIsPinching=true")
                    return true
                }
                
                override fun onScale(detector: android.view.ScaleGestureDetector): Boolean {
                    android.util.Log.d(TAG, "onScale called - mIsPinching=$mIsPinching, scaleFactor: ${detector.scaleFactor}")
                    if (!mIsPinching) return true // Ignore if not actually pinching
                    
                    if (mDrawPoint!!.drawImage!!.status == IMAGE_DETAIL && OperationUtils.DISABLE) {
                        val scaleFactor = detector.scaleFactor
                        // Ignore very small changes that might be noise
                        if (kotlin.math.abs(scaleFactor - 1.0f) < 0.01f) return true
                        
                        val currentScale = mRlContent!!.scaleX * scaleFactor
                        val clampedScale = kotlin.math.max(0.5f, kotlin.math.min(currentScale, 5.0f))
                        mRlContent!!.scaleX = clampedScale
                        mRlContent!!.scaleY = clampedScale
                        android.util.Log.d(TAG, "Scale applied: $clampedScale")
                    }
                    return true
                }
                
                override fun onScaleEnd(detector: android.view.ScaleGestureDetector) {
                    mIsPinching = false
                    android.util.Log.d(TAG, "onScaleEnd - mIsPinching=false")
                }
            })
            
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val params = mRlContent!!.layoutParams as LayoutParams
                android.util.Log.d(TAG, "onTouch: action=${event.actionMasked}, pointerCount=${event.pointerCount}, scaleX=${mRlContent!!.scaleX}, width=${mRlContent!!.width}, height=${mRlContent!!.height}, lpWidth=${params.width}, lpHeight=${params.height}")
                
                if (mDrawPoint!!.drawImage!!.status != IMAGE_DETAIL || !OperationUtils.DISABLE) {
                    // For click to select
                    if (event.action == MotionEvent.ACTION_UP && OperationUtils.DISABLE) {
                        switchView(IMAGE_DETAIL)
                    }
                    return true
                }
                
                // Reset pinching flag when fingers are lifted
                val action = event.actionMasked
                if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) {
                    if (event.pointerCount <= 2) {
                        mIsPinching = false
                        android.util.Log.d(TAG, "Resetting mIsPinching to false due to pointer up")
                    }
                }
                
                // Scale with two fingers only - must check BEFORE passing event
                if (event.pointerCount >= 2 && action != MotionEvent.ACTION_UP) {
                    mScaleDetector.onTouchEvent(event)
                    return true
                }


                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        mPrevX = event.rawX
                        mPrevY = event.rawY
                        mActivePointerId = event.getPointerId(0)
                        mRlContent!!.bringToFront()
                        android.util.Log.d(TAG, "ACTION_DOWN: prevX=$mPrevX, prevY=$mPrevY")
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.pointerCount == 1) {
                            val currX = event.rawX
                            val currY = event.rawY
                            val deltaX = currX - mPrevX
                            val deltaY = currY - mPrevY
                            
                            mRlContent!!.translationX = mRlContent!!.translationX + deltaX
                            mRlContent!!.translationY = mRlContent!!.translationY + deltaY
                            
                            mPrevX = currX
                            mPrevY = currY
                            android.util.Log.d(TAG, "ACTION_MOVE: deltaX=$deltaX, deltaY=$deltaY, transX=${mRlContent!!.translationX}, transY=${mRlContent!!.translationY}")
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Keep translation values - DON'T update layoutParams as it causes remeasure
                        val params = mRlContent!!.layoutParams as LayoutParams
                        val newX = params.leftMargin + mRlContent!!.translationX
                        val newY = params.topMargin + mRlContent!!.translationY
                        
                        android.util.Log.d(TAG, "ACTION_UP: newX=$newX, newY=$newY, finalScale=${mRlContent!!.scaleX}, transX=${mRlContent!!.translationX}, transY=${mRlContent!!.translationY}")
                        
                        // Update point in-place - remember total position (margin + translation)
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
                        mActivePointerId = -1
                    }
                }
                return true
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
