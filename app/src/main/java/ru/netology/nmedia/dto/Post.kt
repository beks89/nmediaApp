package ru.netology.nmedia.dto

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Post (
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likesCount: Int = 189,
    val sharesCount: Int = 8,
    val viewsCount: Int = 619658,
    val likedByMe: Boolean = false,
)

fun countFormat(likesCount: Int): String {
    return when (likesCount) {
        in 1000..1099 ->"${roundNoDecimal(likesCount.toDouble()/1_000.0)}K"
        in 1100..9_999 ->"${roundWithDecimal(likesCount.toDouble()/1_000.0)}K"
        in 10_000..999_999 ->"${roundNoDecimal(likesCount.toDouble()/1_000.0)}K"
        in 1_000_000..1_099_999 ->"${roundNoDecimal(likesCount.toDouble()/1_000_000.0)}M"
        in 1_100_000..Int.MAX_VALUE ->"${roundWithDecimal(likesCount.toDouble()/1_000_000.0)}M"

        else-> likesCount.toString()
    }
}


fun roundWithDecimal(number: Double): Double? {
    val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.US))
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}
fun roundNoDecimal(number: Double): Int? {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toInt()
}