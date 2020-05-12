package com.simple_currency_calculation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 수취국가 선택세팅.
        val colors = arrayOf("한국(KRW)","일본(JPY)","필리핀(PHP)")
        Currency_Picker.minValue = 0
        Currency_Picker.maxValue = colors.size-1
        Currency_Picker.displayedValues = colors
        // 수취국가 변경시
        Currency_Picker.setOnValueChangedListener { _, _, newVal ->
            Currency2.text = "수취국가 : ${colors[newVal]}"
        }
    }
}
