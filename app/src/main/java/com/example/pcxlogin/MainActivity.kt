package com.example.pcxlogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var signInButton: Button
    private lateinit var createAccButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        signInButton = findViewById(R.id.signin_btn)
        createAccButton = findViewById(R.id.createacc_btn)

        // Set up password input's eye icon
        passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)

        // Handle password visibility toggle on touch event
        passwordInput.setOnTouchListener { v, event ->
            val drawableRight = passwordInput.compoundDrawables[2] // Eye icon drawable
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                // Check if the eye icon was clicked
                if (event.rawX >= (passwordInput.right - drawableRight.bounds.width())) {
                    // Toggle visibility
                    isPasswordVisible = !isPasswordVisible
                    passwordInput.inputType = if (isPasswordVisible) {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }

                    // Change the icon based on visibility state
                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0,
                        if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed,
                        0
                    )

                    passwordInput.setSelection(passwordInput.text.length) // Keep cursor at the end
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Sign in button click listener
        signInButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        // Create account button click listener
        createAccButton.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }

    // Login function
    private fun loginUser(username: String, password: String) {
        val call = LoginClient.instance.login(username, password)

        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("LOGIN_RESPONSE", loginResponse.toString())

                    if (loginResponse.success) {
                        Toast.makeText(this@MainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        val user = loginResponse.user

                        if (user != null) {
                            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()

                            editor.putInt("user_id", user.id)
                            editor.putString("username", user.username)
                            editor.putString("email", user.email)
                            editor.putString("contact", user.contactNumber)
                            editor.putString("city", user.city)

                            editor.apply()

                            Log.d("SharedPrefSaved", "Saved user: ${user.username}, email: ${user.email}")
                        } else {
                            Log.e("LOGIN_ERROR", "User data is null in the response!")
                        }

                        val intent = Intent(this@MainActivity, HomePage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Incorrect email or password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
