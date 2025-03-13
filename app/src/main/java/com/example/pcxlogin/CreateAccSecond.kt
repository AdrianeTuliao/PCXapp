package com.example.pcxlogin

import AccountsApi
import CreateAccountResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccSecond : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createacc_second)

        val emailInput = findViewById<EditText>(R.id.Email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val passconInput = findViewById<EditText>(R.id.passcon_input)
        val createAccountButton = findViewById<Button>(R.id.create_btn)

        // Retrieve data passed from first screen
        val username = intent.getStringExtra("username") ?: ""
        val contactNum = intent.getStringExtra("contactNum") ?: ""
        val city = intent.getStringExtra("city") ?: ""

        Log.d(
            "CreateAccSecond",
            "Received -> username: $username, contactNum: $contactNum, city: $city"
        )

        createAccountButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val passcon = passconInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || passcon.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passcon) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("CreateAccSecond", "Creating account with email: $email")

            createAccount(username, contactNum, city, email, password)
        }
    }

    private fun createAccount(
        username: String,
        contactNum: String,
        city: String,
        email: String,
        password: String
    ) {
        val service = RetrofitAccountsClient.instance
        (AccountsApi::class.java)
        val call = service.createAccount(username, contactNum, city, email, password)

        call.enqueue(object : Callback<CreateAccountResponse> {
            override fun onResponse(
                call: Call<CreateAccountResponse>,
                response: Response<CreateAccountResponse>
            ) {
                if (response.isSuccessful) {
                    val createAccountResponse = response.body()
                    if (createAccountResponse != null) {
                        Log.d("CreateAccSecond", "Account creation success: ${createAccountResponse.message}")
                        Toast.makeText(this@CreateAccSecond, createAccountResponse.message, Toast.LENGTH_SHORT).show()

                        // Navigate to HomePage
                        startActivity(Intent(this@CreateAccSecond, HomePage::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@CreateAccSecond, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CreateAccSecond, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("CreateAccSecond", "Server error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                Toast.makeText(this@CreateAccSecond, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CreateAccSecond", "Failure: ${t.message}")
            }
        })
    }

}






