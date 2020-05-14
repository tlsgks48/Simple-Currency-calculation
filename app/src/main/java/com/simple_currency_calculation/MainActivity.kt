package com.simple_currency_calculation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 현재날짜와시간 설정
        val onlyDate: LocalDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formatted = onlyDate.format(formatter)

        // 수취국가 NumberPicker로 선택설정.
        val colors = arrayOf("한국(KRW)","일본(JPY)","필리핀(PHP)")
        Currency_Picker.minValue = 0
        Currency_Picker.maxValue = colors.size-1
        Currency_Picker.displayedValues = colors

        // 초기는 맨위에 있는 한국(KRW)으로 설정.
        Currency6.text = "수취금액은 "+Currency_Picker.value

        // 수취국가 변경시
        Currency_Picker.setOnValueChangedListener { _, _, newVal ->
            Currency2.text = "수취국가 : ${colors[newVal]}"
        }

        // 송금금액은 소수점 2번째자리까지, 하지만 소수점 2번째자리까지 자르라는건지, 반올림하라는 것인지 자세히 조건이 안나와있음..
        // 하지만 환율특성상 소수점까지 중요하므로 자르는것보다는 반올림하는게 좋을것이라 생각함.
        //Math.round()

        // 환율은 앱이 시작될 때 한번 가져와서 계속 사용해도 되고,
        // 혹은 수취국가가 변경될때 마다 API로 서버에 요청해서 새로운 환율 정보를 가져와도 됩니다. (환율 특성상 금액이 계속 달라질 수 있으니 후자가 좋다고 생각함.)

        // 송금금액 3자리마다 콤마
        val Numf : NumberFormat = NumberFormat.getInstance()

       // 송금금액을 입력할때마다 계산
        Currency5_2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var usd = 0 // 초기값 0

                // 금액을 지울때 다 지우고 생기는 ""(공백) 에러 해결위한 조건문.
                if(Currency5_2.text.toString() != "")
                usd = Currency5_2.text.toString().toInt()

                // 수취금액이 0보다 작은 금액이거나 10,000 USD 보다 큰 금액이면 메시지 출력.
                if(usd > 10000)
                    Toast.makeText(this@MainActivity,"송금액이 바르지 않습니다. ", Toast.LENGTH_SHORT).show()
                else{ // 정상적으로 조건 다 만족되면 금액들과 조회시간 설정.
                    Currency6.text = "수취금액은 ${Numf.format(usd)} 입니다."
                    Currency4.text = "조회시간 : ${formatted}"
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Retrofit 클라이언트

    // RetrofitService
}
