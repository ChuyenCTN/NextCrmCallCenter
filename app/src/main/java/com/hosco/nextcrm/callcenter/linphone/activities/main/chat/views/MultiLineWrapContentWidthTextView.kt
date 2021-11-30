package org.linphone.activities.main.chat.views

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.ceil

/**
 * The purpose of this class is to have a TextView declared with wrap_content as width that won't
 * fill it's parent if it is multi line.
 */
class MultiLineWrapContentWidthTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var wSpec = widthSpec
        val widthMode = MeasureSpec.getMode(wSpec)

        if (widthMode == MeasureSpec.AT_MOST) {
            val layout = layout
            if (layout != null) {
                val maxWidth = (
                    ceil(getMaxLineWidth(layout).toDouble()).toInt() +
                        totalPaddingLeft +
                        totalPaddingRight
                    )
                wSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST)
            }
        }

        super.onMeasure(wSpec, heightSpec)
    }

    private fun getMaxLineWidth(layout: Layout): Float {
        var maxWidth = 0.0f
        val lines = layout.lineCount
        for (i in 0 until lines) {
            if (layout.getLineWidth(i) > maxWidth) {
                maxWidth = layout.getLineWidth(i)
            }
        }
        return maxWidth
    }
}
