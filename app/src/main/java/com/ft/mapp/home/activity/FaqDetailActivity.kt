package com.ft.mapp.home.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.ft.mapp.R
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_faq_detail.*

class FaqDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_detail)
        StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.colorAccent))

        val content = intent.getStringExtra(CONTENT)
        faq_detail_wv.settings.useWideViewPort = true
//        faq_detail_wv.settings.loadWithOverviewMode = true
        faq_detail_wv.settings.setSupportZoom(true)
        faq_detail_wv.settings.displayZoomControls=false

        faq_detail_wv.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
//        faq_detail_wv.settings.javaScriptEnabled = true
        faq_detail_wv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)

    }

    companion object {
        private const val CONTENT = "CONTENT"
        fun start(context: Context, content: String) {
            val intent = Intent(context, FaqDetailActivity::class.java)
            intent.putExtra(CONTENT, content)
            context.startActivity(intent)
        }
    }

}
