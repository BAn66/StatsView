package ru.netology.statsview.ui

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var animationType = AnimationType.Pararllel

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
            animationType = AnimationType.create(
                getInt(
                    R.styleable.StatsView_animationType,
                    animationType.value
                )
            )
        }
    }

    var data: List<Pair<Float, Boolean>> = listOf(
        Pair(0.25F, true),
        Pair(0.25F, true),
        Pair(0.25F, true),
        Pair(0.25F, true)
    )
        set(value) {
            field = value
            update() //спровоцирует выполнение функции onDrow
        }

//    var data: List<Pair<Float, Boolean>> = emptyList()


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
    ) { //Метод при измении размера (наверное размера экрана)
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
        when (animationType) {
            AnimationType.Pararllel -> parralelDraw(canvas)
            AnimationType.Sequantial -> sequentialDraw(canvas)
            AnimationType.Bidirectional -> BidirectionalDraw(canvas)
        }
    }

    /**Двусторонняя анимация*/
    private fun BidirectionalDraw(
        canvas: Canvas
    ) {

        if (data.isEmpty()) {
            return
        }
        paint.color = Color.parseColor("#cccccccc")
        canvas.drawCircle(center.x, center.y, radius, paint) //отрисовка серого кольца

        //отрисовка точки сверху
        paintDot.color = colors[0]
        canvas.drawCircle(center.x, center.y - radius, lineWidth / 4, paintDot)

        val dataPercent = mutableListOf<Float>()
        data.forEach {
            if (it.second)
                dataPercent.add(it.first / sumOfData(data))
        }

        canvas.drawText( //Отрисовка текста
            "%.2f%%".format(dataPercent.sum() * 100), //сам текст
            center.x,//положение текста по центру по горизонтали
            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
            textPaint //Кисть
        )
        var startFrom = -90F
        for ((index, datum) in dataPercent.withIndex()) {
            val angle = 360F * datum
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            val fullProgressAngle = angle * progress
            val halfProgressAngle = fullProgressAngle / 2
            canvas.drawArc(oval, startFrom - halfProgressAngle, fullProgressAngle, false, paint)
            startFrom += angle
        }
    }

    /**Последовательная анимация*/
    private fun sequentialDraw(
        canvas: Canvas
    ) {
        if (data.isEmpty()) {
            return
        }
        paint.color = Color.parseColor("#cccccccc")
        canvas.drawCircle(center.x, center.y, radius, paint) //отрисовка серого кольца


        //отрисовка точки сверху
        paintDot.color = colors[0]
        canvas.drawCircle(center.x, center.y - radius, lineWidth / 4, paintDot)

        val dataPercent = mutableListOf<Float>()
        data.forEach {
            if (it.second)
                dataPercent.add(it.first / sumOfData(data))
        }

        canvas.drawText( //Отрисовка текста
            "%.2f%%".format(dataPercent.sum() * 100), //сам текст
            center.x,//положение текста по центру по горизонтали
            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
            textPaint //Кисть
        )

        var startFrom = -90F
        // Максимальный угол поворота с учётом начального отступа
        val maxAngle = 360 * progress + startFrom
        for ((index, datum) in dataPercent.withIndex()) {
            val angle = datum * 360
            // Для того, чтобы при указании меньше 100% последняя дуга не росла до 100%
            val rotationAngle = min(angle, maxAngle - startFrom)
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startFrom, rotationAngle, false, paint)
            startFrom += angle
            // Проверка не зашли ли мы слишком далеко
            if (startFrom > maxAngle) return
        }
    }

    /**Паралелльная анимация*/
    private fun parralelDraw(
        canvas: Canvas
    ) {
        if (data.isEmpty()) {
            return
        }
        paint.color = Color.parseColor("#cccccccc")
        canvas.drawCircle(center.x, center.y, radius, paint) //отрисовка серого кольца


        //отрисовка точки сверху
        paintDot.color = colors[0]
        canvas.drawCircle(center.x, center.y - radius, lineWidth / 4, paintDot)

        val dataPercent = mutableListOf<Float>()
        data.forEach {
            if (it.second)
                dataPercent.add(it.first / sumOfData(data))
        }

        canvas.drawText( //Отрисовка текста
            "%.2f%%".format(dataPercent.sum() * 100), //сам текст
            center.x,//положение текста по центру по горизонтали
            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
            textPaint //Кисть
        )

        // Анимация сразу всех
        var startAngle = -90F
        dataPercent.forEachIndexed { index, datum ->//отрисковка разноцветных частей круга
            val angle = 360F * datum
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress, false, paint)
            startAngle += angle * progress // как прогресс бар, 4 части постепенно все заполняют
            //startAngle += angle// сразу все из разных точек
        }
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        val drawArc = PropertyValuesHolder.ofFloat(ALPHA, 0F, 1F)
        valueAnimator = ValueAnimator.ofPropertyValuesHolder(drawArc).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2_500
            interpolator = LinearInterpolator()
        }
            .also {
                it.start()
            }


    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun sumOfData(list: List<Pair<Float, Boolean>>): Float {
        var summa = 0F
        list.forEach {
            summa = summa + it.first
        }
        return summa
    }

    //Выбор способа отрисовки из аттрибутов
    enum class AnimationType(val value: Int) {
        Pararllel(0),
        Sequantial(1),
        Bidirectional(2),
        ;

        companion object {
            fun create(value: Int): AnimationType = entries.find { it.value == value } ?: Pararllel
        }
    }
}

//д/з анимации 1 лекция
//package ru.netology.statsview.ui
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.PointF
//import android.graphics.RectF
//import android.util.AttributeSet
//import android.view.View
//import androidx.core.content.withStyledAttributes
//import ru.netology.statsview.R
//import ru.netology.statsview.utils.AndroidUtils
//import kotlin.math.min
//import kotlin.random.Random
//
//
//class StatsView @JvmOverloads constructor(
//    //Наше кастомное view
//    context: Context, //Контекст
//    attributeSet: AttributeSet? = null, //Набор аттрибутов, передаваемых через xml
//    defStyleAttr: Int = 0, //Стиль аттрибутов по умолчанию
//    defStyleRes: Int = 0, // Стиль по умолчанию
//) : View(
//    context,
//    attributeSet,
//    defStyleAttr,
//    defStyleRes
//) {
//
//    private var textSize = AndroidUtils.dp(context, 20F).toFloat() //размер текста
//    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat() //Толщина линии
//    private var colors = emptyList<Int>()
//
//    init { //использование своих аттрибутов
//        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
//            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
//            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
//            colors = listOf(
//                getColor(R.styleable.StatsView_color1, generateRandomColor()),
//                getColor(R.styleable.StatsView_color2, generateRandomColor()),
//                getColor(R.styleable.StatsView_color3, generateRandomColor()),
//                getColor(R.styleable.StatsView_color4, generateRandomColor()),
//            )
//        }
//    }
//
//    var data: List<Pair<Float, Boolean>> = emptyList()
//        set(value) {
//            field = value
//            invalidate() //спровоцирует выполнение функции onDrow
//        }
//
//    private var radius = 0F
//    private var center = PointF()
//    private var oval = RectF() //Область в которой будет отрисовка
//
//    private val paint = Paint(
//        Paint.ANTI_ALIAS_FLAG //сглаживание
//    ).apply {
//        strokeWidth = this@StatsView.lineWidth //ширина строки
//        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
//        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
//        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
//    }
//
//    private val paintDot = Paint(
//        Paint.ANTI_ALIAS_FLAG //сглаживание
//    ).apply {
//        strokeWidth = this@StatsView.lineWidth / 2 //ширина строки
//        style = Paint.Style.STROKE //Cтиль отрисовки по строкам
//        strokeJoin = Paint.Join.ROUND // Скругление углов при пересечении линий
//        strokeCap = Paint.Cap.ROUND // Скругление углов концов линий
//        paint.color = R.styleable.StatsView_color1
//    }
//
//    private val textPaint = Paint( //кисть для текста
//        Paint.ANTI_ALIAS_FLAG //сглаживание
//    ).apply {
//        textSize = this@StatsView.textSize
//        style = Paint.Style.FILL //Cтиль отрисовки заливка
//        textAlign = Paint.Align.CENTER  //Привязка к центру
//    }
//
//    override fun onSizeChanged(
//        w: Int,
//        h: Int,
//        oldw: Int,
//        oldh: Int
//    ) { //Метод при измении размера (наверное размера экрана)1
//        radius = min(w, h) / 2F - lineWidth
//        center = PointF(w / 2F, h / 2F)
//        oval = RectF(
//            center.x - radius,//левая грань
//            center.y - radius, //верхняя грань
//            center.x + radius, //правая грань
//            center.y + radius //нижняя грань
//        )
//    }
//
//    @SuppressLint("SuspiciousIndentation")
//    override fun onDraw(canvas: Canvas) { //Метод который рисует
//        if (data.isEmpty()) {
//            return
//        }
//
//        val dataPercent = mutableListOf<Float>()
//        data.forEach{
//            if(it.second)
//                dataPercent.add(it.first/sumOfData(data))
//        }
//
//        paint.setColor(Color.parseColor("#cccccccc"))
//        canvas.drawCircle(center.x, center.y, radius , paint) //отрисовка серого кольца
//
//        var startAngle = -90F
//        dataPercent.forEachIndexed { index, datum ->//отрисковка разноцветных частей круга
//            val angle = datum * 360
//            paint.color = colors.getOrElse(index){generateRandomColor()}
//            canvas.drawArc(oval, startAngle, angle, false, paint)
//            startAngle += angle
//        }
//        paintDot.setColor(colors[0])
//        canvas.drawCircle(center.x, center.y - radius, lineWidth / 4, paintDot) //отрисовка точки сверху
//
//
//        canvas.drawText(
//            "%.2f%%".format(dataPercent.sum() * 100), //сам текст
//            center.x,//положение текста по центру по горизонтали
//            center.y + textPaint.textSize / 4,//положение текста по центру по вертикали плюс чуть чуть отступ вниз
//            textPaint //Кисть
//        )
//
//
//    }
//
//    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFf.toInt())
//
//    private fun sumOfData(list: List<Pair<Float, Boolean>>): Float{
//        var summa = 0F
//        list.forEach {
//            summa = summa + it.first
//        }
//        return summa
//    }
//}