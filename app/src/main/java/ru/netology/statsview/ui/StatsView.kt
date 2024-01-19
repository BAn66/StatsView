package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min


class StatsView @JvmOverloads constructor(
    //Наше кастомное view
    context: Context, //Контекст
    attributeSet: AttributeSet? = null, //Набор аттрибутов, передаваемых через xml
    defStyleAttr: Int = 0, //Стиль аттрибутов по умолчанию
    defStyleRes: Int = 0, // Стиль по умолчанию
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private var radius = 0F
    private var center = PointF()
    private val lineWidth = AndroidUtils.dp(context, 5F)
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        strokeWidth = lineWidth.toFloat() //ширина строки
        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) { //Метод при измении размера (наверное размера экрана)1
        radius = min(w, h) / 2F - AndroidUtils.dp(context, 5F)
        center = PointF(w / 2F, h / 2F)
    }

    override fun onDraw(canvas: Canvas) { //Метод который рисует
        canvas.drawCircle(center.x, center.y, radius, paint)

    }
}