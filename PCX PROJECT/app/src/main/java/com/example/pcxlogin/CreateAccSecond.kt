package com.example.pcxlogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class CreateAccSecond : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createacc_second)

        val createAccountButton: Button = findViewById(R.id.create_btn)
        createAccountButton.setOnClickListener {

            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }
}
