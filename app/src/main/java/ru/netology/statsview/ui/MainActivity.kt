package ru.netology.statsview.ui


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf<Pair<Float, Boolean>>(
            Pair(500F, true),
            Pair(500F, true),
            Pair(500F, true),
            Pair(500F, true)
        )

//        view.data = listOf(
//            0.25F,
//            0.25F,
//            0.25F,
//            0.25F,
//        )

        val textPecent = findViewById<TextView>(R.id.textPecent) // Или сделать textView,
        // или сделать отдельное ui на текст с процентами, и задать аналогичную анимацию как для StatsView

        val dataPercent = mutableListOf<Float>()
        view.data.forEach {
            if (it.second)
                dataPercent.add(it.first / sumOfData(view.data))
        }

       val percent = "%.2f%%".format( dataPercent.sum() * 100)
        val textSize = AndroidUtils.dp(view.context, 20F).toFloat()
        val textView = findViewById<TextView>(R.id.label)
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation)
                .apply{
                setAnimationListener(object: Animation.AnimationListener{
                    @SuppressLint("SetTextI18n")
                    override fun onAnimationStart(p0: Animation?) {
                        textView.text = "onAnimationStart"
                        textPecent.textSize = textSize
                        textPecent.text = percent
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationEnd(p0: Animation?) {
                        textView.text ="onAnimationEnd"
                        textPecent.textSize = textSize
                        textPecent.text = percent
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationRepeat(p0: Animation?) {
                        textView.text ="onAnimationRepeat"
                        textPecent.textSize = textSize
                        textPecent.text = percent
                    }
                })
            }
        )
    }
}

private fun sumOfData(list: List<Pair<Float, Boolean>>): Float {
    var summa = 0F
    list.forEach {
        summa = summa + it.first
    }
    return summa
}

// д/з 1 анимации
//package ru.netology.statsview.ui
//
//import android.annotation.SuppressLint
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.animation.Animation
//import android.view.animation.AnimationUtils
//import android.widget.TextView
//import ru.netology.statsview.R
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val view = findViewById<StatsView>(R.id.statsView)
//        view.data = listOf<Pair<Float, Boolean>>(
//            Pair(500F, true),
//            Pair(500F, true),
//            Pair(500F, true),
//            Pair(500F, true)
//        )
//
//
//        val textView = findViewById<TextView>(R.id.label)
//
//        view.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.animation).apply{
//                setAnimationListener(object: Animation.AnimationListener{
//                    @SuppressLint("SetTextI18n")
//                    override fun onAnimationStart(p0: Animation?) {
//                        textView.text = "onAnimationStart"
//                    }
//
//                    @SuppressLint("SetTextI18n")
//                    override fun onAnimationEnd(p0: Animation?) {
//                        textView.text ="onAnimationEnd"
//                    }
//
//                    @SuppressLint("SetTextI18n")
//                    override fun onAnimationRepeat(p0: Animation?) {
//                        textView.text ="onAnimationRepeat"
//                    }
//                })
//            }
//        )
//    }
//}
