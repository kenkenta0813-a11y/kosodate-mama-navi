package com.kenkenta.kosodatemamanavi

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.widget.*
import org.json.JSONArray

class MainActivity : Activity() {

    private val pink = Color.rgb(255, 245, 250)
    private val darkPink = Color.rgb(190, 60, 120)

    private val menus = listOf(
        "妊娠・出産",
        "子育て支援",
        "保育園・幼稚園",
        "医療・病院",
        "ひとり親支援",
        "相談窓口",
        "お気に入り"
    )

    private var municipalities = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val json = assets.open("municipalities.json")
                .bufferedReader()
                .use { it.readText() }
            municipalities = JSONArray(json)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "市区町村データを読み込めません",
                Toast.LENGTH_LONG
            ).show()
        }

        showHome()
    }

    private fun baseLayout(title: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(pink)
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 25f
            setTextColor(darkPink)
        }
        layout.addView(heading)

        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
        return layout
    }

    private fun addButton(
        layout: LinearLayout,
        title: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = title
            textSize = 17f
            setOnClickListener { action() }
        }
        layout.addView(button)
    }

    private fun addMessage(
        layout: LinearLayout,
        message: String
    ) {
        val text = TextView(this).apply {
            this.text = message
            textSize = 18f
            setPadding(8, 20, 8, 20)
        }
        layout.addView(text)
    }

    private fun showHome() {
        val layout = baseLayout("全国子育て支援ナビ")

        addButton(layout, "都道府県から探す") {
            showPrefectures()
        }

        addButton(layout, "市区町村から探す") {
            showPrefectures()
        }

        menus.forEach { menu ->
            addButton(layout, menu) {
                showPage(menu)
            }
        }
    }

    private fun showPrefectures() {
        val layout = baseLayout("都道府県を選択")

        val prefectures = mutableListOf<String>()

        for (i in 0 until municipalities.length()) {
            val item = municipalities.getJSONObject(i)
            val pref = item.optString("pref")

            if (pref.isNotBlank() &&
                !prefectures.contains(pref)) {
                prefectures.add(pref)
            }
        }

        prefectures.forEach { pref ->
            addButton(layout, pref) {
                showCities(pref)
            }
        }

        addButton(layout, "トップ画面に戻る") {
            showHome()
        }
    }

    private fun showCities(pref: String) {
        val layout = baseLayout("$pref の市区町村")

        for (i in 0 until municipalities.length()) {
            val item = municipalities.getJSONObject(i)

            if (item.optString("pref") == pref) {
                val city = item.optString("city")

                addButton(layout, city) {
                    showCityMenu(pref, city)
                }
            }
        }

        addButton(layout, "都道府県選択に戻る") {
            showPrefectures()
        }
    }

    private fun showCityMenu(
        pref: String,
        city: String
    ) {
        val layout = baseLayout("$pref $city")

        menus.forEach { menu ->
            addButton(layout, menu) {
                showPage(menu, pref, city)
            }
        }

        addButton(layout, "市区町村選択に戻る") {
            showCities(pref)
        }

        addButton(layout, "トップ画面に戻る") {
            showHome()
        }
    }

    private fun showPage(
        name: String,
        pref: String = "",
        city: String = ""
    ) {
        val title = if (city.isEmpty()) {
            name
        } else {
            "$pref $city\n$name"
        }

        val layout = baseLayout(title)

        addMessage(
            layout,
            "詳しい子育て支援情報は準備中です。"
        )

        if (city.isNotEmpty()) {
            addButton(layout, "市区町村メニューに戻る") {
                showCityMenu(pref, city)
            }
        }

        addButton(layout, "トップ画面に戻る") {
            showHome()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showHome()
    }
}
