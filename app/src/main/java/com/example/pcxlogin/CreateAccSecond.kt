package com.example.pcxlogin

import AccountsApi
import CreateAccountResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccSecond : AppCompatActivity() {

    // Initialize views
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var passconInput: EditText
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createacc_second)

        // Initialize views
        emailInput = findViewById(R.id.Email_input)
        passwordInput = findViewById(R.id.password_input)
        passconInput = findViewById(R.id.passcon_input)
        val createAccountButton = findViewById<Button>(R.id.create_btn)

        // Set inputType to password (by default, hide the password)
        passwordInput.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        passconInput.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Set initial eye icon to closed for both password fields
        passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)
        passconInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)

        // Retrieve data passed from the first screen
        val username = intent.getStringExtra("username") ?: ""
        val contactNum = intent.getStringExtra("contactNum") ?: ""
        val city = intent.getStringExtra("city") ?: ""

        Log.d(
            "CreateAccSecond",
            "Received -> username: $username, contactNum: $contactNum, city: $city"
        )

        // Progress bar 100
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.progress = 100

        // Handle touch on the password field to toggle visibility
        passwordInput.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                // Get the drawable for the "eye" icon
                val drawableRight = passwordInput.compoundDrawables[2]
                if (event.rawX >= (passwordInput.right - drawableRight.bounds.width())) {
                    // Toggle visibility
                    isPasswordVisible = !isPasswordVisible
                    passwordInput.inputType = if (isPasswordVisible) {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }

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

        passconInput.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                // Get the drawable for the "eye" icon
                val drawableRight = passconInput.compoundDrawables[2]
                if (event.rawX >= (passconInput.right - drawableRight.bounds.width())) {
                    // Toggle visibility
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                    passconInput.inputType = if (isConfirmPasswordVisible) {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }

                    passconInput.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0,
                        if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed,
                        0
                    )

                    passconInput.setSelection(passconInput.text.length) // Keep cursor at the end
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Create account button
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

    // Create account function
    private fun createAccount(
        username: String,
        contactNum: String,
        city: String,
        email: String,
        password: String
    ) {
        val service = RetrofitAccountsClient.instance.create(AccountsApi::class.java)

        val call = service.createAccount(username, contactNum, city, email, password)

        // Handle response
        call.enqueue(object : Callback<CreateAccountResponse> {
            override fun onResponse(
                call: Call<CreateAccountResponse>,
                response: Response<CreateAccountResponse>
            ) {
                if (response.isSuccessful) {
                    val createAccountResponse = response.body()
                    if (createAccountResponse != null) {
                        Log.d("CreateAccSecond", "Account creation success: ${createAccountResponse.message}")

                        // This stays on the same page and allows user to register into another email
                        if (createAccountResponse.message.contains("Email already exists", ignoreCase = true)) {
                            Toast.makeText(this@CreateAccSecond, "This email is already registered. Try another one.", Toast.LENGTH_SHORT).show()

                            emailInput.text.clear()
                            emailInput.requestFocus()

                        } else {
                            Toast.makeText(this@CreateAccSecond, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        }

                        // Navigate to Login page
                        startActivity(Intent(this@CreateAccSecond, MainActivity::class.java))
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
