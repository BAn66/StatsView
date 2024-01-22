package ru.netology.statsview.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
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

    private var textSize = AndroidUtils.dp(context, 20F).toFloat() //размер текста
    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat() //Толщина линии
    private var colors = emptyList<Int>()

    init { //использование своих аттрибутов
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )
        }
    }

    var data: List<Pair<Float, Boolean>> = emptyList()
        set(value) {
            field = value
            invalidate() //спровоцирует выполнение функции onDrow
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF() //Область в которой будет отрисовка

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        strokeWidth = this@StatsView.lineWidth //ширина строки
        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
    }

    private val paintDot = Paint(
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        strokeWidth = this@StatsView.lineWidth / 2 //ширина строки
        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
        paint.color = R.styleable.StatsView_color1
    }

    private val textPaint = Paint( //кисть для текста
        Paint.ANTI_ALIAS_FLAG //сглаживание
    ).apply {
        textSize = this@StatsView.textSize
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

    @SuppressLint("SuspiciousIndentation")
    override fun onDraw(canvas: Canvas) { //Метод который рисует
        if (data.isEmpty()) {
            return
        }

        val dataPercent = mutableListOf<Float>()
        data.forEach{
            if(it.second)
            dataPercent.add(it.first/sumOfData(data))
        }

        paint.setColor(Color.parseColor("#cccccccc"))
        canvas.drawCircle(center.x, center.y, radius , paint) //отрисовка серого кольца

        var startAngle = -90F
        dataPercent.forEachIndexed { index, datum ->//отрисковка разноцветных частей круга
            val angle = datum * 360
            paint.color = colors.getOrElse(index){generateRandomColor()}
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }
        paintDot.setColor(colors[0])
        canvas.drawCircle(center.x, center.y - radius, lineWidth / 4, paintDot) //отрисовка точки сверху


        canvas.drawText(
            "%.2f%%".format(dataPercent.sum() * 100), //сам текст
            center.x,//положение текста по центру по горизонтали
            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
            textPaint //Кисть
        )


    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFf.toInt())

    private fun sumOfData(list: List<Pair<Float, Boolean>>): Float{
        var summa = 0F
        list.forEach {
            summa = summa + it.first
        }
        return summa
    }
}