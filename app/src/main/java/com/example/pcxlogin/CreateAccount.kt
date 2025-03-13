package com.example.pcxlogin
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val contactNumInput = findViewById<EditText>(R.id.contactnum_input)
        val cityInput = findViewById<EditText>(R.id.city_input)


        val confirmButton: Button = findViewById(R.id.confirm_btn)
        confirmButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val contactNum = contactNumInput.text.toString()
            val city = cityInput.text.toString()

            if (username.isEmpty() || contactNum.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CreateAccSecond::class.java).apply {
                putExtra("username", username)
                putExtra("contactNum", contactNum)
                putExtra("city", city)
            }
            startActivity(intent)
        }
    }
}


