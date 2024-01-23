package ru.netology.statsview.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import ru.netology.statsview.R

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


        val textView = findViewById<TextView>(R.id.label)

        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation).apply{
                setAnimationListener(object: Animation.AnimationListener{
                    @SuppressLint("SetTextI18n")
                    override fun onAnimationStart(p0: Animation?) {
                        textView.text = "onAnimationStart"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationEnd(p0: Animation?) {
                        textView.text ="onAnimationEnd"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationRepeat(p0: Animation?) {
                        textView.text ="onAnimationRepeat"
                    }
                })
            }
        )
    }
}
