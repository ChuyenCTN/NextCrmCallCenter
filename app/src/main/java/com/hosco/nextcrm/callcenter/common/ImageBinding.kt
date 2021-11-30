package com.hosco.nextcrm.ui.main.adapter

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.hosco.nextcrm.callcenter.R

@BindingAdapter("loadingImage")
fun setImageRadius(
    image: ImageView,
    input: String?
) {
    Glide.with(image.context!!)
        .asBitmap()
        .load(input)
        .centerCrop()
        .placeholder(ContextCompat.getDrawable(image.context, R.drawable.placeholder_image))
        .into(object : BitmapImageViewTarget(image) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(image.context.resources, resource)
//                        circularBitmapDrawable.isCircular = true
                circularBitmapDrawable.cornerRadius =
                    image.context.resources.getDimension(R.dimen.dimen10)
                image.setImageDrawable(circularBitmapDrawable)
            }
        })


}
