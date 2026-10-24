package com.kenkenta.kosodatemamanavi

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.ScrollView
import android.graphics.Color
import android.content.Intent
import android.net.Uri

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMenu()
    }

    private fun showMenu() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 20)
            setBackgroundColor(Color.rgb(255, 245, 250))
        }

        val title = TextView(this).apply {
            text = "全国子育て支援ナビ"
            textSize = 26f
            setTextColor(Color.rgb(190, 60, 120))
        }
        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "あなたの街の子育て情報を探そう"
            textSize = 17f
            setPadding(0, 24, 0, 24)
        }
        layout.addView(subtitle)

        val items = listOf(
            "都道府県から探す",
            "市区町村から探す",
            "保育園・幼稚園",
            "子育て支援センター",
            "医療・病院",
            "助成金・手当",
            "ひとり親支援",
            "相談窓口",
            "お気に入り"
        )

        val scroll = ScrollView(this)
        val menu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        items.forEach { item ->
            val button = Button(this).apply {
                text = item
                textSize = 17f
                isAllCaps = false

                setOnClickListener {
                    showPage(item)
                }
            }
            menu.addView(button)
        }

        scroll.addView(menu)
        layout.addView(scroll)
        setContentView(layout)
    }

    private fun showPage(item: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 20)
            setBackgroundColor(Color.rgb(255, 245, 250))
        }

        val title = TextView(this).apply {
            text = item
            textSize = 26f
            setTextColor(Color.rgb(190, 60, 120))
        }
        layout.addView(title)

        val message = TextView(this).apply {
            text = "「$item」の画面です。\n現在、機能を準備しています。"
            textSize = 18f
            setPadding(0, 40, 0, 40)
        }
        layout.addView(message)

        val backButton = Button(this).apply {
            text = "トップ画面に戻る"
            setOnClickListener {
                showMenu()
            }
        }
        layout.addView(backButton)

        setContentView(layout)
    }

    override fun onBackPressed() {
        showMenu()
    }
}
