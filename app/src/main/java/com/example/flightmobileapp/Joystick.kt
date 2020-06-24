package com.example.flightmobileapp
import Api
import Command
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import io.github.controlwear.virtual.joystick.android.JoystickView
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin
import kotlin.math.cos

class Joystick : AppCompatActivity() {
    private var lastElevator:Double =0.0
    private var lastAileron:Double = 0.0
    private var lastThrottle:Double = 0.0
    private var lastRudder:Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joystick)
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

            if (((normalizeY - lastElevator)  > 0.01)||((normalizeX - lastAileron) > 0.01)) {
                lastElevator = normalizeY
                lastAileron = normalizeX
                postNewCommand(lastElevator,lastAileron,lastThrottle,lastRudder)
            }
        }
        setSeekBarProgress()
    }



    private fun setSeekBarProgress(){
        val throttleSB = findViewById<SeekBar>(R.id.throttleSeekBar)
        throttleSB.max = 10
        val rudderSB = findViewById<SeekBar>(R.id.rudderSeekBar)
        rudderSB.max = 10
        val throttleText = findViewById<TextView>(R.id.thrText)
        val rudderText = findViewById<TextView>(R.id.rudText)
        throttleSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val num:Double = (progress)*0.1
                if (lastThrottle - num > 0.01){
                    lastThrottle = num
                    postNewCommand(lastElevator,lastAileron,lastThrottle,lastRudder)
                }
                throttleText.text =   String.format("%.1f", num)
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
                val num:Double = (progress)*0.1
                rudderText.text =   String.format("%.1f", num)

                if (lastRudder - num > 0.01){
                    lastRudder = num
                    postNewCommand(lastElevator,lastAileron,lastThrottle,lastRudder)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })
    }
   // findViewById<EditText>(R.id.url).text.toString()
    private fun postNewCommand(elevator:Double, aileron:Double, throttle:Double, rudder:Double){

       val gson = GsonBuilder()
           .setLenient()
           .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://localhost:5001")
            .build()
        val api = retrofit.create(Api::class.java)
       val jsonToSend: String = "{\"aileron\": $aileron,\n \"rudder\": $rudder, \n " +
               "\"elevator\": $elevator, \n \"throttle\": $throttle\n}"
       val requestBody: RequestBody =
           RequestBody.create(MediaType.parse("application/json"), jsonToSend)
       val myPost =  api.postControl(requestBody)
        myPost.enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ERROR Joystick", t.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                TODO("Not yet implemented")
            }

        })
    }

}