
package com.kenkenta.kosodatemamanavi

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this).apply {
            text = "子育てママナビへようこそ！"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
        }

        setContentView(text)
    }
}
