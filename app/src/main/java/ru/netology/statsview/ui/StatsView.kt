package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random


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

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate() //спровоцирует выполнение функции onDrow
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF() //Область в которой будет отрисовка
    private val lineWidth = AndroidUtils.dp(context, 5F)
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        strokeWidth = lineWidth.toFloat() //ширина строки
        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
    }

    private val textPaint = Paint( //кисть для текста
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        textSize = AndroidUtils.dp(context, 20F).toFloat() //размер текста
        style = Paint.Style.FILL //Cтиль отрисовки заливка
        textAlign = Paint.Align.CENTER  //Привязка к центру
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) { //Метод при измении размера (наверное размера экрана)1
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,//левая грань
            center.y - radius, //верхняя грань
            center.x + radius, //правая грань
            center.y + radius //нижняя грань
        )
    }

    override fun onDraw(canvas: Canvas) { //Метод который рисует
        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F
        data.forEach { //отрисковка разноцветных частей круга
            val angle = it * 360
            paint.color = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFf.toInt())
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }

        canvas.drawText(
            "%.2f%%".format(data.sum()*100), //сам текст
            center.x,//положение текста по центру по горизонтали
            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
            textPaint //Кисть
        )
//        canvas.drawCircle(center.x, center.y, radius, paint) //просто отрисовка круга

    }
}