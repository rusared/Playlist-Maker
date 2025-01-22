package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
//    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.search_button)
        val button1ClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Открываю поиск.", Toast.LENGTH_LONG).show()
            }
        }
        button1.setOnClickListener(button1ClickListener)

        val button2 = findViewById<Button>(R.id.library_button)
        button2.setOnClickListener {
            Toast.makeText(this@MainActivity, "Подожди. Сейчас открою кучу песен!", Toast.LENGTH_LONG).show()
        }

        val button3 = findViewById<Button>(R.id.settings_button)
        button3.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываю настройки!", Toast.LENGTH_LONG).show()
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}