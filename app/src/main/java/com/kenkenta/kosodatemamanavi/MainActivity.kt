
package com.kenkenta.kosodatemamanavi

import android.app.Activity
import android.os.Bundle
import android.content.Intentrt
import android.net.Uri
import android.graphics.Color
import android.widget.*
import org.json.JSONArray

class MainActivity : Activity() {
    private val bg = Color.rgb(255, 245, 250)
    private val accent = Color.rgb(174, 45, 110)
    private val categories = listOf("妊娠・出産", "子育て支援", "保育園・幼稚園", "医療・病院", "学校・教育", "ひとり親支援", "相談窓口", "公園・遊び場", "行政サービス")
    private val descriptions = mapOf(
        "妊娠・出産" to "母子健康手帳、妊婦健診、出産育児一時金、妊婦のための支援給付などを確認できます。対象・申請方法は自治体や加入する健康保険によって異なります。",
        "子育て支援" to "児童手当、乳幼児健診、予防接種、子どもの医療費助成、子育て相談などを確認できます。自治体独自の助成にもご注意ください。",
        "保育園・幼稚園" to "保育所・認定こども園・幼稚園、入園申請、保育料、空き状況、延長保育などを確認できます。募集日程は自治体にお問い合わせください。",
        "医療・病院" to "小児科、産婦人科、休日・夜間診療、予防接種、救急相談などを確認できます。緊急時は119番に連絡してください。",
        "学校・教育" to "小中学校、就学援助、放課後児童クラブ、教育相談などを確認できます。",
        "ひとり親支援" to "シングルマザー・シングルファザー向けに、児童扶養手当、就業支援、生活相談、医療費助成などを確認できます。受給要件は個別に確認してください。",
        "相談窓口" to "育児・家庭・発達・虐待などの相談先を探せます。児童相談所虐待対応ダイヤルは189です。",
        "公園・遊び場" to "地域の公園、児童館、子育て支援センター、屋内遊び場などを探せます。",
        "行政サービス" to "住民票、転入転出、保育・子育ての申請窓口、自治体のお知らせを確認できます。"
    )
    private var records = JSONArray()
    private val prefs by lazy { getSharedPreferences("kosodate_favorites", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            records = JSONArray(assets.open("municipalities.json").bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            Toast.makeText(this, "自治体データを読み込めません。assets/municipalities.jsonを確認してください", Toast.LENGTH_LONG).show()
        }
        home()
    }

    private fun page(title: String): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
        label(root, title, 24f, accent)
        return root
    }

    private fun label(root: LinearLayout, value: String, size: Float = 17f, color: Int = Color.DKGRAY) {
        root.addView(TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            setPadding(8, 14, 8, 14)
        })
    }

    private fun button(root: LinearLayout, title: String, action: () -> Unit) {
        root.addView(Button(this).apply {
            text = title
            textSize = 16f
            isAllCaps = false
            setOnClickListener { action() }
        })
    }

    private fun home() {
        val root = page("全国子育て支援ナビ")
        label(root, "全国47都道府県の自治体を選んで、子育て情報を探せます。")
        button(root, "都道府県・市区町村から探す") { prefectures() }
        button(root, "お気に入り") { favorites() }
        categories.forEach { cat -> button(root, cat) { detail(cat, null) } }
    }

    private fun prefectures() {
        val root = page("都道府県を選択")
        val names = linkedSetOf<String>()
        for (i in 0 until records.length()) {
            val name = records.optJSONObject(i)?.optString("pref").orEmpty()
            if (name.isNotBlank()) names.add(name)
        }
        if (names.isEmpty()) label(root, "自治体データがありません。ファイルを確認してください。")
        names.forEach { pref -> button(root, pref) { cities(pref) } }
        button(root, "トップに戻る") { home() }
    }

    private fun cities(pref: String) {
        val root = page("$pref の市区町村")
        val list = mutableListOf<Int>()
        for (i in 0 until records.length()) {
            if (records.optJSONObject(i)?.optString("pref") == pref) list.add(i)
        }
        list.forEach { index ->
            val city = records.optJSONObject(index)?.optString("city").orEmpty()
            button(root, city) { cityMenu(index) }
        }
        button(root, "都道府県に戻る") { prefectures() }
    }

    private fun cityMenu(index: Int) {
        val obj = records.optJSONObject(index) ?: return
        val name = "${obj.optString("pref")} ${obj.optString("city")}"
        val root = page(name)
        button(root, "自治体の公式サイト") { openUrl(obj.optString("url")) }
        categories.forEach { cat -> button(root, cat) { detail(cat, index) } }
        button(root, "市区町村一覧に戻る") { cities(obj.optString("pref")) }
        button(root, "トップに戻る") { home() }
    }

    private fun detail(category: String, index: Int?) {
        val obj = if (index == null) null else records.optJSONObject(index)
        val place = if (obj == null) "全国共通の案内" else "${obj.optString("pref")} ${obj.optString("city")}"
        val root = page("$place\n$category")
        label(root, descriptions[category] ?: "自治体の公式情報を確認してください。")
        label(root, "※制度の金額・対象年齢・申請期限・施設情報は自治体や年度で変わります。必ず公式情報で確認してください。", 14f)
        if (obj != null) {
            val url = obj.optString("url")
            button(root, "自治体公式サイトを開く") { openUrl(url) }
            button(root, "この自治体の「$category」を検索") {
                val q = "$place $category 公式"
                openUrl("https://www.google.com/search?q=" + Uri.encode(q))
            }
            val key = "${obj.optString("lgcode")}|$category"
            button(root, if (prefs.getBoolean(key, false)) "★ お気に入りを解除" else "☆ お気に入りに保存") {
                val newValue = !prefs.getBoolean(key, false)
                prefs.edit().putBoolean(key, newValue).apply()
                detail(category, index)
            }
            button(root, "市区町村メニューに戻る") { cityMenu(index!!) }
        } else {
            button(root, "都道府県・市区町村を選ぶ") { prefectures() }
        }
        button(root, "トップに戻る") { home() }
    }

    private fun favorites() {
        val root = page("お気に入り")
        var count = 0
        for (i in 0 until records.length()) {
            val obj = records.optJSONObject(i) ?: continue
            for (cat in categories) {
                if (prefs.getBoolean("${obj.optString("lgcode")}|$cat", false)) {
                    val index = i
                    count++
                    button(root, "${obj.optString("pref")} ${obj.optString("city")} / $cat") { detail(cat, index) }
                }
            }
        }
        if (count == 0) label(root, "保存したお気に入りはまだありません。")
        button(root, "トップに戻る") { home() }
    }

    private fun openUrl(url: String) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            Toast.makeText(this, "リンク先が登録されていません", Toast.LENGTH_SHORT).show()
            return
        }
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
        catch (e: Exception) { Toast.makeText(this, "ブラウザを開けません", Toast.LENGTH_SHORT).show() }
    }

   
}
