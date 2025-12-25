package com.example.navigationdemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LegacyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legacy)

        findViewById<TextView>(R.id.titleText).text = "Legacy Activity"

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
