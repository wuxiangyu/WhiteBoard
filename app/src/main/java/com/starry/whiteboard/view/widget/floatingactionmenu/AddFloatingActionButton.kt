package com.starry.whiteboard.view.widget.floatingactionmenu

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.starry.whiteboard.R

open class AddFloatingActionButton : FloatingActionButton {
    var plusColor: Int = 0
        set(value) {
            if (field != value) {
                field = value
                updateBackground()
            }
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun init(context: Context, attributeSet: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attributeSet, R.styleable.AddFloatingActionButton, 0, 0)
        plusColor = attr.getColor(
            R.styleable.AddFloatingActionButton_fab_plusIconColor,
            getColor(android.R.color.white)
        )
        attr.recycle()

        super.init(context, attributeSet)
    }

    fun setPlusColorResId(@ColorRes plusColor: Int) {
        this.plusColor = getColor(plusColor)
    }

    override fun setIcon(@DrawableRes icon: Int) {
        throw UnsupportedOperationException("Use FloatingActionButton if you want to use custom icon")
    }

    override fun getIconDrawable(): Drawable {
        val iconSize = getDimension(R.dimen.fab_icon_size)
        val iconHalfSize = iconSize / 2f

        val plusSize = getDimension(R.dimen.fab_plus_icon_size)
        val plusHalfStroke = getDimension(R.dimen.fab_plus_icon_stroke) / 2f
        val plusOffset = (iconSize - plusSize) / 2f

        val shape = object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                canvas.drawRect(
                    plusOffset,
                    iconHalfSize - plusHalfStroke,
                    iconSize - plusOffset,
                    iconHalfSize + plusHalfStroke,
                    paint
                )
                canvas.drawRect(
                    iconHalfSize - plusHalfStroke,
                    plusOffset,
                    iconHalfSize + plusHalfStroke,
                    iconSize - plusOffset,
                    paint
                )
            }
        }

        val drawable = ShapeDrawable(shape)

        val paint = drawable.paint
        paint.color = plusColor
        paint.style = Style.FILL
        paint.isAntiAlias = true

        return drawable
    }
}
