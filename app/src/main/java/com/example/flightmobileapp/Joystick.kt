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
import io.github.controlwear.virtual.joystick.android.JoystickView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin
import kotlin.math.cos

class Joystick : AppCompatActivity() {
    private var lastElevator:Double =0.0
    private var lastAileron:Double = 0.0
    private var lastThrottle:Double = 0.0
    private var lastRudder:Double = 0.0
    @RequiresApi(Build.VERSION_CODES.O)
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



        @RequiresApi(Build.VERSION_CODES.O)
    private fun setSeekBarProgress(){
        val throttleSB = findViewById<SeekBar>(R.id.throttleSeekBar)
        throttleSB.max = 10
        val rudderSB = findViewById<SeekBar>(R.id.rudderSeekBar)
        rudderSB.max = 10
        rudderSB.min = -10
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

    private fun postNewCommand(elevator:Double, aileron:Double, throttle:Double, rudder:Double){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(findViewById<EditText>(R.id.url).text.toString())
            .build()
        val api = retrofit.create(Api::class.java)
       val myPost: Call<Command> =  api.postControl(Command(aileron,rudder,throttle,elevator))
        myPost.enqueue(object : Callback<Command>{
            override fun onFailure(call: Call<Command>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }

            override fun onResponse(call: Call<Command>, response: Response<Command>) {
                TODO("Not yet implemented")
            }

        })
    }

}