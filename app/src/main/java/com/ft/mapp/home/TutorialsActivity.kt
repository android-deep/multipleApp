package com.ft.mapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ft.mapp.R
import com.jaeger.library.StatusBarUtil

class TutorialsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorials)
        StatusBarUtil.setColorNoTranslucent(this,resources.getColor(R.color.colorAccent))
    }

}
