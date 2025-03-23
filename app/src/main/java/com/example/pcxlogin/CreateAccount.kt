package com.example.pcxlogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateAccount : AppCompatActivity() {

    // List of cities
    private val cities = listOf("Select City", "Dagupan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        // Initialize views
        val usernameInput = findViewById<EditText>(R.id.username_input)
        val contactNumInput = findViewById<EditText>(R.id.contactnum_input)
        val citySpinner = findViewById<Spinner>(R.id.city_spinner)

        // Spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val selectedCity = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Progress bar 50
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.progress = 50

        // Confirm Button
        val confirmButton: Button = findViewById(R.id.confirm_btn)
        confirmButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val contactNum = contactNumInput.text.toString().trim()
            val city = citySpinner.selectedItem.toString()

            // validate input
            if (username.isEmpty() || contactNum.isEmpty() || city == "Select City") {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass data to next activity
            val intent = Intent(this, CreateAccSecond::class.java).apply {
                putExtra("username", username)
                putExtra("contactNum", contactNum)
                putExtra("city", city)
            }
            startActivity(intent)
        }
    }
}
