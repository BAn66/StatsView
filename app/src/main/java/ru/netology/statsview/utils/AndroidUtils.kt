package ru.netology.statsview.utils

import android.content.Context
import kotlin.math.ceil

object AndroidUtils {
    /** Функция чтобы получить из dp пиксели */
    fun dp(context: Context, dp: Float): Int =
        ceil(context.resources.displayMetrics.density * dp).toInt()
}