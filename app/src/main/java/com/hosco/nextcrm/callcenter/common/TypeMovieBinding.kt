package com.hosco.nextcrm.callcenter.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hosco.nextcrm.callcenter.R

@BindingAdapter("setTypeText")
fun setText(
    textView: TextView,
    type: Int
) {
    if (type == Const.TYPE_TEXT_POPULAR)
        textView.text = textView.context.getString(R.string.txt_popular)
    else if (type == Const.TYPE_TEXT_NOWPLAYING)
        textView.text = textView.context.getString(R.string.txt_nowplaying)

}