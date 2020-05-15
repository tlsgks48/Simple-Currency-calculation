package com.simple_currency_calculation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    // 수취국가 NumberPicker로 선택설정.
    val colors = arrayOf("한국(KRW)","일본(JPY)","필리핀(PHP)")
    val colors2 = arrayOf("KRW","JPY","PHP")

    // 송금금액 3자리마다 콤마와 항상 소수점 2자리가 반올림되어 나오도록 설정.
    val Numf2 : DecimalFormat = DecimalFormat("#,##0.00")
    // 송금금액은 소수점 2번째자리까지, 하지만 소수점 2번째자리까지 자르라는건지, 반올림하라는 것인지 자세히 조건이 안나와있음..
    // 하지만 환율특성상 소수점까지 중요하므로 자르는것보다는 반올림하는게 좋을것이라 생각함.

    var usd = 0 // 송금금액 입력숫자.(초기값 0)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 현재날짜와시간 설정
        val onlyDate: LocalDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formatted = onlyDate.format(formatter)

        // 수취국가 NumberPicker로 선택설정.
        Currency_Picker.minValue = 0
        Currency_Picker.maxValue = colors.size-1
        Currency_Picker.displayedValues = colors

        // 수취국가 변경시
        Currency_Picker.setOnValueChangedListener { _, _, newVal ->
            Currency2.text = "수취국가 : ${colors[newVal]}"
            RetrofitBuild(1)
        }

        // 초기는 맨위에 있는 한국(KRW)으로 설정.
        // 초기 환율정보 API 요청.(한국)
        RetrofitBuild(1)


        // 송금금액을 입력할때마다 계산
        // 환율은 앱이 시작될 때 한번 가져와서 계속 사용해도 되고,
        // 혹은 수취국가가 변경될때 마다 API로 서버에 요청해서 새로운 환율 정보를 가져와도 됩니다. (환율 특성상 금액이 계속 달라질 수 있으니 후자가 좋다고 생각함.)
        Currency5_2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // 금액을 지울때 다 지우고 생기는 ""(공백) 에러 해결위한 조건문.
                if(Currency5_2.text.toString() != "")
                usd = Currency5_2.text.toString().toInt()
                else usd=0

                // 수취금액이 0보다 작은 금액이거나 10,000 USD 보다 큰 금액이면 메시지 출력.
                if(usd > 10000)
                    Toast.makeText(this@MainActivity,"송금액이 바르지 않습니다. ", Toast.LENGTH_SHORT).show()
                else{ // 정상적으로 조건 다 만족되면 환율금액들과 조회시간 설정.
                    Currency4.text = "조회시간 : ${formatted}" // 입력했을때의 조회시간.
                    RetrofitBuild(2)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
    //메인끝.
    // 리트로핏 빌더를 생성과 환율 api 요청. 각 상황에 맞게 조건문 활용.
    fun RetrofitBuild(num : Int){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apilayer.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitService::class.java)
        val call = service.getCurrencyData("434df5fa312711fc52ac01729d646d76", "KRW,JPY,PHP", "USD","1")
        call.enqueue(object : Callback<Currency>{
            override fun onFailure(call: Call<Currency>, t: Throwable) {
                Log.d("MainActivity", "result :" + t.message)
            }
            override fun onResponse(call: Call<Currency>, response: Response<Currency>) {
                if(response.isSuccessful){
                    val currencyResponse = response.body()
                    Log.d("MainActivity", "result: " + currencyResponse.toString())
                    if(currencyResponse!!.success.equals("true")){ // API 불러온게 SUCCESS : TRUE라면 성공.
                        // 선택된 나라에 따른 환율선택.
                        var money = 0.0
                        if(Currency_Picker.value == 0) money = currencyResponse.quotes!!.USDKRW
                        else if(Currency_Picker.value == 1) money = currencyResponse.quotes!!.USDJPY
                        else money = currencyResponse.quotes!!.USDPHP

                        if(num == 1){
                            Currency3.text = "환율 : ${Numf2.format(money)} ${colors2.get(Currency_Picker.value)} / USD"
                            Currency6.text = "수취금액은 ${Numf2.format(money*usd)} ${colors2.get(Currency_Picker.value)} 입니다."
                        }else if(num == 2){
                            Currency6.text = "수취금액은 ${Numf2.format(money*usd)} ${colors2.get(Currency_Picker.value)} 입니다."
                        }
                    }
                }
            }
        })
    }

    // RetrofitService
    interface RetrofitService{
        @GET("api/live")
        fun getCurrencyData( // ? 뒤쪽 변수명들.
            @Query("access_key") access_key: String,
            @Query("currencies ") currencies : String,
            @Query("source  ") source  : String,
            @Query("format  ") format  : String) : Call<Currency>
    }

    // 데이터클래스 : 필요한것은 success, quotes
    class Currency(){
        @SerializedName("success")
        var success: String? = null
        @SerializedName("quotes")
        var quotes: Quotes? = null
    }

    // 데이터클래스 : 한국,일본,필리핀
    class Quotes(){ // 실수는 소수점이 더 정밀한 Double 선택.
        @SerializedName("USDKRW")
        var USDKRW: Double = 0.0
        @SerializedName("USDJPY")
        var USDJPY: Double = 0.0
        @SerializedName("USDPHP")
        var USDPHP: Double = 0.0
    }
}
