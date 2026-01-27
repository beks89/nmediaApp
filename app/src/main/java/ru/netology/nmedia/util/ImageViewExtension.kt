package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadAvatar(url: String) {
    Glide.with(this)
        .load(url)
        .circleCrop()
        .timeout(10_000)
        .into(this)
}