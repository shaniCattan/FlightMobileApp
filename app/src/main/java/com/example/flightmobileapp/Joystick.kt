package com.example.flightmobileapp

import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.joystick.*

class Joystick : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joystick)
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
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })
    }
}