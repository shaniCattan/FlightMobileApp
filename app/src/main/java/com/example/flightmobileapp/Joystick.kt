package com.example.flightmobileapp
import Api
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.joystick.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Joystick : AppCompatActivity() {
    private var lastElevator:Double = 0.0
    private var lastAileron:Double = 0.0
    private var lastThrottle:Double = 0.0
    private var lastRudder:Double = 0.0

   private var gson = GsonBuilder()
        .setLenient()
        .create()

   private var retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl("http://10.0.2.2:50321/")
        .build()
//  private var url: String = intent.getStringExtra(MainActivity).toString()
   private var api = retrofit.create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joystick)
        runImg()
        findViewById<JoystickView>(R.id.joystickView).setOnMoveListener { angle, strength ->
            val angleRad=Math.toRadians(angle.toDouble())
            val baseRadius:Double = ((240*80)/200).toDouble()
            val joyRadius:Double = (strength*baseRadius)/100
            //calculate
            val y:Double = joyRadius*sin(angleRad)
            val x:Double = joyRadius*cos(angleRad)
            //normalize the value between 0 to 1
            //elevator
            val normalizeY:Double=  y / baseRadius
            //aileron
            val normalizeX:Double=  x/ baseRadius
            if ((abs(normalizeY - lastElevator)  > 0.02)||(abs(normalizeX - lastAileron) > 0.02)) {
                lastElevator = normalizeY
                lastAileron = normalizeX
                CoroutineScope(IO).launch {
                    postNewCommand()
                }
            }
        }

        val throttleSB = findViewById<SeekBar>(R.id.throttleSeekBar)
        val rudderSB = findViewById<SeekBar>(R.id.rudderSeekBar)
        rudderSB.max = 200
        throttleSB.max = 100
        val throttleText = findViewById<TextView>(R.id.thrText)
        val rudderText = findViewById<TextView>(R.id.rudText)
        throttleSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val num = (progress.toDouble() / 100)
                if (num == 0.0) {
                    throttleText.text = "0"
                }else{
                    throttleText.text = String.format("%.2f", num).toDouble().toString()
                }
                if (abs(lastThrottle - num) > 0.01){
                    lastThrottle = num
                    CoroutineScope(IO).launch {
                        postNewCommand()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })

        rudderSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val num = (progress.toDouble() / 100) - 1
                if (num == 0.0) {
                    rudderText.text = "0"
                }else{
                    rudderText.text = String.format("%.2f", num).toDouble().toString()
                }

                if (abs(lastRudder - num) > 0.02){
                    lastRudder = num
                    CoroutineScope(IO).launch {
                        postNewCommand()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })    }


   // findViewById<EditText>(R.id.url).text.toString()
    private fun postNewCommand(){
       val jsonToSend: String = "{\"aileron\": $lastAileron,\n \"rudder\": $lastRudder, \n " +
               "\"elevator\": $lastElevator, \n \"throttle\": $lastThrottle\n}"
       val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), jsonToSend)
       api.postControl(requestBody).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ERROR Joystick", t.message.toString())
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println("good joystick")
            }
        })
   }

    private fun runImg(){
        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)
        fixedRateTimer("timer", false,0L, 1000){
            uiScope.launch{
                println("in launch")
                getImageFromServer()
            }
        }

    }

    private fun getImageFromServer(){
        api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val I = response.body()?.byteStream()
                val B = BitmapFactory.decodeStream(I)
                server_screenshot.setImageBitmap(B)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

}


