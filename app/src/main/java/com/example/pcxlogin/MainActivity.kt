package com.example.pcxlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var signInButton: Button
    private lateinit var createAccButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        signInButton = findViewById(R.id.signin_btn)
        createAccButton = findViewById(R.id.createacc_btn)

        signInButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        val createAccButton: Button = findViewById(R.id.createacc_btn)
        createAccButton.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        val call = LoginClient.instance.login(username, password)

        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    android.util.Log.d("LOGIN_RESPONSE", loginResponse.toString())

                    if (loginResponse.success) {
                        Toast.makeText(this@MainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        val user = loginResponse.user

                        // Check if user is not null
                        if (user != null) {
                            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()

                            editor.putInt("user_id", user.id)
                            editor.putString("username", user.username)
                            editor.putString("email", user.email)
                            editor.putString("contact", user.contactNumber)
                            editor.putString("city", user.city)

                            editor.apply()

                            android.util.Log.d("SharedPrefSaved", "Saved user: ${user.username}, email: ${user.email}")
                        } else {
                            android.util.Log.e("LOGIN_ERROR", "User data is null in the response!")
                        }

                        val intent = Intent(this@MainActivity, HomePage::class.java)
                        startActivity(intent)
                        finish()
                    }

                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect email or password!.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
