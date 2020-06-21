package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.joystick.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.connect_button)
        }
    //connect to server, move to the next activity
    fun doneCLicked (view: View){
  //      val url = view.findViewById<EditText>(R.id.url).toString()
   //     val imageView = view.findViewById<ImageView>(R.id.server_screenshot)
  //      Picasso.get().load(url).placeholder(R.drawable.error).error(R.drawable.error).into(imageView)
        val intent = Intent(this,Joystick::class.java)
        startActivity(intent)
    }
}





