package ru.netology.statsview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<StatsView>(R.id.statsView).data = listOf<Pair<Float, Boolean>>(
            Pair(500F, true),
            Pair(500F, true),
            Pair(500F, true),
            Pair(500F, false),
            Pair(250F, true),
            Pair(350F, true)
        )
    }
}
