package com.pacom.lashinbang.common.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


fun ImageView.from(url: String?, placeholder: Int? = null) {
    val requestOption = RequestOptions()
    placeholder?.let {
        requestOption.placeholder(placeholder)
    }
    requestOption.dontAnimate()
    Glide.with(context).load(url).apply(requestOption).into(this)
}