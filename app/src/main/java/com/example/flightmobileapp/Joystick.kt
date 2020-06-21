package com.example.flightmobileapp

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.joystick.*

class Joystick : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joystick)
        val joystick = findViewById<JoystickView>(R.id.joystickView)
        joystick.setOnMoveListener { angle, strength ->

        }
    }

}