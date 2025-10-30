package com.example.pcxlogin
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        val confirmButton: Button = findViewById(R.id.confirm_btn)
        confirmButton.setOnClickListener {

            val intent = Intent(this, CreateAccSecond::class.java)
            startActivity(intent)
        }
    }
}


