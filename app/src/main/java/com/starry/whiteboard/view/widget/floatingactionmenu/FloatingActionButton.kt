package com.starry.whiteboard.view.widget.floatingactionmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.*
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import com.starry.whiteboard.R

open class FloatingActionButton : FloatingImageButton {
    var colorNormal: Int = 0
    var colorPressed: Int = 0
    var colorDisabled: Int = 0
    private var mTitle: String? = null
    @DrawableRes
    private var mIcon: Int = 0
    private var mIconDrawable: Drawable? = null
    var size: Int = SIZE_NORMAL
        set(value) {
            if (value != SIZE_MINI && value != SIZE_NORMAL) {
                throw IllegalArgumentException("Use @FAB_SIZE constants only!")
            }
            if (field != value) {
                field = value
                updateCircleSize()
                updateDrawableSize()
                updateBackground()
            }
        }

    private var mCircleSize: Float = 0f
    private var mShadowRadius: Float = 0f
    private var mShadowOffset: Float = 0f
    private var mDrawableSize: Int = 0
    var isStrokeVisible: Boolean = true
        set(visible) {
            if (field != visible) {
                field = visible
                updateBackground()
            }
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    @SuppressLint("ResourceType")
    open fun init(context: Context, attributeSet: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, 0, 0)
        colorNormal = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorNormal,
            getColor(android.R.color.holo_blue_dark)
        )
        colorPressed = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorPressed,
            getColor(android.R.color.holo_blue_light)
        )
        colorDisabled = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorDisabled,
            getColor(android.R.color.darker_gray)
        )
        size = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL)
        mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0)
        mTitle = attr.getString(R.styleable.FloatingActionButton_fab_title)
        isStrokeVisible = attr.getBoolean(R.styleable.FloatingActionButton_fab_stroke_visible, true)
        attr.recycle()

        updateCircleSize()
        mShadowRadius = getDimension(R.dimen.fab_shadow_radius)
        mShadowOffset = getDimension(R.dimen.fab_shadow_offset)
        updateDrawableSize()

        updateBackground()
    }

    private fun updateDrawableSize() {
        mDrawableSize = (mCircleSize + 2 * mShadowRadius).toInt()
    }

    private fun updateCircleSize() {
        mCircleSize = getDimension(if (size == SIZE_NORMAL) R.dimen.fab_size_normal else R.dimen.fab_size_mini)
    }

    open fun setIcon(@DrawableRes icon: Int) {
        if (mIcon != icon) {
            mIcon = icon
            mIconDrawable = null
            updateBackground()
        }
    }

    open fun setIconDrawable(iconDrawable: Drawable) {
        if (mIconDrawable !== iconDrawable) {
            mIcon = 0
            mIconDrawable = iconDrawable
            updateBackground()
        }
    }

    fun setColorNormalResId(@ColorRes colorNormal: Int) {
        this.colorNormal = getColor(colorNormal)
    }

    fun setColorPressedResId(@ColorRes colorPressed: Int) {
        this.colorPressed = getColor(colorPressed)
    }

    fun setColorDisabledResId(@ColorRes colorDisabled: Int) {
        this.colorDisabled = getColor(colorDisabled)
    }

    fun getColor(@ColorRes id: Int): Int {
        return resources.getColor(id)
    }

    fun getDimension(@DimenRes id: Int): Float {
        return resources.getDimension(id)
    }

    fun setTitle(title: String) {
        mTitle = title
        val label = getLabelView()
        label?.text = title
    }

    fun getLabelView(): TextView? {
        return getTag(R.id.fab_label) as? TextView
    }

    fun getTitle(): String? {
        return mTitle
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mDrawableSize, mDrawableSize)
    }

    open fun updateBackground() {
        val strokeWidth = getDimension(R.dimen.fab_stroke_width)
        val halfStrokeWidth = strokeWidth / 2f

        val layerDrawable = LayerDrawable(
            arrayOf(
                resources.getDrawable(if (size == SIZE_NORMAL) R.drawable.fab_bg_normal else R.drawable.fab_bg_mini),
                createFillDrawable(strokeWidth),
                createOuterStrokeDrawable(strokeWidth),
                getIconDrawable()
            )
        )

        val iconOffset = ((mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2).toInt()

        val circleInsetHorizontal = mShadowRadius.toInt()
        val circleInsetTop = (mShadowRadius - mShadowOffset).toInt()
        val circleInsetBottom = (mShadowRadius + mShadowOffset).toInt()

        layerDrawable.setLayerInset(
            1,
            circleInsetHorizontal,
            circleInsetTop,
            circleInsetHorizontal,
            circleInsetBottom
        )

        layerDrawable.setLayerInset(
            2,
            (circleInsetHorizontal - halfStrokeWidth).toInt(),
            (circleInsetTop - halfStrokeWidth).toInt(),
            (circleInsetHorizontal - halfStrokeWidth).toInt(),
            (circleInsetBottom - halfStrokeWidth).toInt()
        )

        layerDrawable.setLayerInset(
            3,
            circleInsetHorizontal + iconOffset,
            circleInsetTop + iconOffset,
            circleInsetHorizontal + iconOffset,
            circleInsetBottom + iconOffset
        )

        setBackgroundCompat(layerDrawable)
    }

    open fun getIconDrawable(): Drawable {
        if (mIconDrawable != null) {
            return mIconDrawable!!
        } else if (mIcon != 0) {
            return resources.getDrawable(mIcon)
        } else {
            return ColorDrawable(Color.TRANSPARENT)
        }
    }

    private fun createFillDrawable(strokeWidth: Float): StateListDrawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), createCircleDrawable(colorDisabled, strokeWidth))
        drawable.addState(intArrayOf(android.R.attr.state_pressed), createCircleDrawable(colorPressed, strokeWidth))
        drawable.addState(intArrayOf(), createCircleDrawable(colorNormal, strokeWidth))
        return drawable
    }

    private fun createCircleDrawable(color: Int, strokeWidth: Float): Drawable {
        val alpha = Color.alpha(color)
        val opaqueColor = opaque(color)

        val fillDrawable = ShapeDrawable(OvalShape())

        val paint = fillDrawable.paint
        paint.isAntiAlias = true
        paint.color = opaqueColor

        val layers = arrayOf(fillDrawable, createInnerStrokesDrawable(opaqueColor, strokeWidth))

        val drawable = if (alpha == 255 || !isStrokeVisible)
            LayerDrawable(layers)
        else
            TranslucentLayerDrawable(alpha, *layers)

        val halfStrokeWidth = (strokeWidth / 2f).toInt()
        drawable.setLayerInset(1, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth)

        return drawable
    }

    private class TranslucentLayerDrawable(private val mAlpha: Int, vararg layers: Drawable) : LayerDrawable(layers) {

        override fun draw(canvas: Canvas) {
            val bounds = bounds
            canvas.saveLayerAlpha(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                mAlpha,
                Canvas.ALL_SAVE_FLAG
            )
            super.draw(canvas)
            canvas.restore()
        }
    }

    private fun createOuterStrokeDrawable(strokeWidth: Float): Drawable {
        val shapeDrawable = ShapeDrawable(OvalShape())

        val paint = shapeDrawable.paint
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.alpha = opacityToAlpha(0.02f)

        return shapeDrawable
    }

    private fun opacityToAlpha(opacity: Float): Int {
        return (255f * opacity).toInt()
    }

    private fun darkenColor(argb: Int): Int {
        return adjustColorBrightness(argb, 0.9f)
    }

    private fun lightenColor(argb: Int): Int {
        return adjustColorBrightness(argb, 1.1f)
    }

    private fun adjustColorBrightness(argb: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(argb, hsv)

        hsv[2] = Math.min(hsv[2] * factor, 1f)

        return Color.HSVToColor(Color.alpha(argb), hsv)
    }

    private fun halfTransparent(argb: Int): Int {
        return Color.argb(
            Color.alpha(argb) / 2,
            Color.red(argb),
            Color.green(argb),
            Color.blue(argb)
        )
    }

    private fun opaque(argb: Int): Int {
        return Color.rgb(
            Color.red(argb),
            Color.green(argb),
            Color.blue(argb)
        )
    }

    private fun createInnerStrokesDrawable(color: Int, strokeWidth: Float): Drawable {
        if (!isStrokeVisible) {
            return ColorDrawable(Color.TRANSPARENT)
        }

        val shapeDrawable = ShapeDrawable(OvalShape())

        val bottomStrokeColor = darkenColor(color)
        val bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor)
        val topStrokeColor = lightenColor(color)
        val topStrokeColorHalfTransparent = halfTransparent(topStrokeColor)

        val paint = shapeDrawable.paint
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        shapeDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    (width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(),
                    intArrayOf(topStrokeColor, topStrokeColorHalfTransparent, color, bottomStrokeColorHalfTransparent, bottomStrokeColor),
                    floatArrayOf(0f, 0.2f, 0.5f, 0.8f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        }

        return shapeDrawable
    }

    @SuppressLint("NewApi")
    private fun setBackgroundCompat(drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = drawable
        } else {
            setBackgroundDrawable(drawable)
        }
    }

    override fun setVisibility(visibility: Int) {
        val label = getLabelView()
        label?.visibility = visibility

        super.setVisibility(visibility)
    }

    companion object {
        const val SIZE_NORMAL = 0
        const val SIZE_MINI = 1

        @IntDef(SIZE_NORMAL, SIZE_MINI)
        @Retention(AnnotationRetention.SOURCE)
        annotation class FAB_SIZE
    }
}
