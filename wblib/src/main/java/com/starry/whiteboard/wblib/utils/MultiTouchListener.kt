package com.starry.whiteboard.wblib.utils

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.max
import kotlin.math.min

/**
 * Adapted from PhotoEditor MultiTouchListener
 */
class MultiTouchListener(
    context: Context,
    private val parentView: View? = null,
    private val isPinchScalable: Boolean = true,
    private val onGestureControl: OnGestureControl? = null
) : OnTouchListener {

    private val mGestureListener: GestureDetector
    private val mScaleGestureDetector: ScaleGestureDetector
    
    private val isRotateEnabled = true
    private val isTranslateEnabled = true
    private val isScaleEnabled = true
    private val minimumScale = 0.5f
    private val maximumScale = 10.0f
    
    private var mActivePointerId = INVALID_POINTER_ID
    private var mPrevX = 0f
    private var mPrevY = 0f
    
    // Track reference to the view being touched
    private var viewReference: View? = null

    interface OnGestureControl {
        fun onClick()
        fun onLongClick()
    }

    private inner class ScaleGestureListener : SimpleOnScaleGestureListener() {
        private var mPivotX = 0f
        private var mPivotY = 0f
        private val mPrevSpanVector = Vector2D()

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mPivotX = detector.focusX
            mPivotY = detector.focusY
            mPrevSpanVector.set(detector.currentSpanVector)
            return isPinchScalable
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val info = TransformInfo()
            info.deltaScale = if (isScaleEnabled) detector.scaleFactor else 1.0f
            info.deltaAngle = if (isRotateEnabled) Vector2D.getAngle(
                mPrevSpanVector,
                detector.currentSpanVector
            ) else 0.0f
            info.deltaX = if (isTranslateEnabled) detector.focusX - mPivotX else 0.0f
            info.deltaY = if (isTranslateEnabled) detector.focusY - mPivotY else 0.0f
            info.pivotX = mPivotX
            info.pivotY = mPivotY
            info.minimumScale = minimumScale
            info.maximumScale = maximumScale
            
            viewReference?.let { move(it, info) }
            return !isPinchScalable
        }
    }
    
    private val ScaleGestureDetector.currentSpanVector: Vector2D
        get() = Vector2D(currentSpanX, currentSpanY)

    private class TransformInfo {
        var deltaX = 0f
        var deltaY = 0f
        var deltaScale = 0f
        var deltaAngle = 0f
        var pivotX = 0f
        var pivotY = 0f
        var minimumScale = 0f
        var maximumScale = 0f
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onGestureControl?.onClick()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            onGestureControl?.onLongClick()
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1

        private fun adjustAngle(degrees: Float): Float {
            return when {
                degrees > 180.0f -> degrees - 360.0f
                degrees < -180.0f -> degrees + 360.0f
                else -> degrees
            }
        }

        private fun move(view: View, info: TransformInfo) {
            computeRenderOffset(view, info.pivotX, info.pivotY)
            adjustTranslation(view, info.deltaX, info.deltaY)
            
            var scale = view.scaleX * info.deltaScale
            scale = max(info.minimumScale, min(info.maximumScale, scale))
            view.scaleX = scale
            view.scaleY = scale
            
            val rotation = adjustAngle(view.rotation + info.deltaAngle)
            view.rotation = rotation
        }

        private fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
            val deltaVector = floatArrayOf(deltaX, deltaY)
            view.matrix.mapVectors(deltaVector)
            view.translationX = view.translationX + deltaVector[0]
            view.translationY = view.translationY + deltaVector[1]
        }

        private fun computeRenderOffset(view: View, pivotX: Float, pivotY: Float) {
            if (view.pivotX == pivotX && view.pivotY == pivotY) {
                return
            }
            val prevPoint = floatArrayOf(0.0f, 0.0f)
            view.matrix.mapPoints(prevPoint)
            view.pivotX = pivotX
            view.pivotY = pivotY
            val currPoint = floatArrayOf(0.0f, 0.0f)
            view.matrix.mapPoints(currPoint)
            val offsetX = currPoint[0] - prevPoint[0]
            val offsetY = currPoint[1] - prevPoint[1]
            view.translationX = view.translationX - offsetX
            view.translationY = view.translationY - offsetY
        }
    }

    init {
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
        mGestureListener = GestureDetector(context, GestureListener())
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // Use parentView if provided, otherwise the touched view
        viewReference = parentView ?: view
        
        mScaleGestureDetector.onTouchEvent(event)
        mGestureListener.onTouchEvent(event)

        if (!isTranslateEnabled) {
            return true
        }

        val action = event.action
        
        when (action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.x
                mPrevY = event.y
                mActivePointerId = event.getPointerId(0)
                viewReference?.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndexMove = event.findPointerIndex(mActivePointerId)
                if (pointerIndexMove != -1) {
                    val currX = event.getX(pointerIndexMove)
                    val currY = event.getY(pointerIndexMove)
                    if (!mScaleGestureDetector.isInProgress) {
                        viewReference?.let { 
                            adjustTranslation(it, currX - mPrevX, currY - mPrevY) 
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> mActivePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_UP -> mActivePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndexPointerUp =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndexPointerUp)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndexPointerUp == 0) 1 else 0
                    mPrevX = event.getX(newPointerIndex)
                    mPrevY = event.getY(newPointerIndex)
                    mActivePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }
}
