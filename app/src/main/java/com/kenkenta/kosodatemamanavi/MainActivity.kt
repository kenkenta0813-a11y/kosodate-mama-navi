package com.kenkenta.kosodatemamanavi

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private lateinit var layout: LinearLayout

    private val prefectures = listOf(
        "北海道", "青森県", "岩手県", "宮城県", "秋田県",
        "山形県", "福島県", "茨城県", "栃木県", "群馬県",
        "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県",
        "長野県", "岐阜県", "静岡県", "愛知県", "三重県",
        "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県",
        "和歌山県", "鳥取県", "島根県", "岡山県",
        "広島県", "山口県", "徳島県", "香川県",
        "愛媛県", "高知県", "福岡県", "佐賀県",
        "長崎県", "熊本県", "大分県", "宮崎県",
        "鹿児島県", "沖縄県"
    )

    private val cities = mapOf(
        "宮崎県" to listOf(
            "宮崎市", "都城市", "延岡市", "日南市",
            "小林市", "日向市", "串間市", "西都市",
            "えびの市", "三股町", "高原町"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun prepare(title: String) {
        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(255, 245, 250))
        }

        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)

        val heading = TextView(this).apply {
            text = title
            textSize = 25f
            setTextColor(Color.rgb(190, 60, 120))
        }
        layout.addView(heading)
    }

    private fun addButton(
        name: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = name
            setOnClickListener { action() }
        }
        layout.addView(button)
    }

    private fun showHome() {
        prepare("全国子育て支援ナビ")

        addButton("都道府県・市町村から探す") {
            showPrefectures()
        }

        val menus = listOf(
            "子育て支援", "医療・病院",
            "保育園・幼稚園", "学校・教育",
            "相談窓口", "公園・遊び場",
            "行政サービス"
        )

        menus.forEach { menu ->
            addButton(menu) {
                prepare(menu)
                val message = TextView(this).apply {
                    text = "現在、機能を準備しています。"
                    textSize = 20f
                }
                layout.addView(message)
                addButton("トップ画面に戻る") {
                    showHome()
                }
            }
        }
    }

    private fun showPrefectures() {
        prepare("都道府県を選択")

        prefectures.forEach { pref ->
            addButton(pref) {
                showCities(pref)
            }
        }

        addButton("トップ画面に戻る") {
            showHome()
        }
    }

    private fun showCities(pref: String) {
        prepare("$pref の市町村")

        val cityList = cities[pref]

        if (cityList == null) {
            val notice = TextView(this).apply {
                text = "市町村データを準備しています。"
                textSize = 20f
            }
            layout.addView(notice)
        } else {
            cityList.forEach { city ->
                addButton(city) {
                    prepare("$pref $city")
                    val notice = TextView(this).apply {
                        text = "子育て支援情報を準備しています。"
                        textSize = 20f
                    }
                    layout.addView(notice)
                    addButton("市町村選択に戻る") {
                        showCities(pref)
                    }
                    addButton("トップ画面に戻る") {
                        showHome()
                    }
                }
            }
        }

        addButton("都道府県選択に戻る") {
            showPrefectures()
        }
    }
}
