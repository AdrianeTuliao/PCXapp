package com.example.pcxlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.text.InputType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AccountFragment : Fragment() {

    private lateinit var retrofit: Retrofit
    private lateinit var changePasswordApi: ChangePasswordApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val changePasswordButton = view.findViewById<Button>(R.id.changePassword)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        // Set up Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.18.127/PCXadmin/")  // Replace with your actual API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        changePasswordApi = retrofit.create(ChangePasswordApi::class.java)

        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 1)
        val username = sharedPref.getString("username", "Unknown User")
        val email = sharedPref.getString("email", "No Email")
        val contact = sharedPref.getString("contact", "No Contact")
        val city = sharedPref.getString("city", "No City")

        Log.d("SharedPrefAnywhere", "UserId: $userId")
        Log.d("SharedPrefAnywhere", "Username: $username")
        Log.d("SharedPrefAnywhere", "Email: $email")
        Log.d("SharedPrefAnywhere", "Contact: $contact")
        Log.d("SharedPrefAnywhere", "City: $city")

        // EditText
        val etUsername = view.findViewById<TextView>(R.id.userName)
        val etEmail = view.findViewById<TextView>(R.id.Email_input)
        val etContact = view.findViewById<TextView>(R.id.contactnum_input)
        val etCity = view.findViewById<TextView>(R.id.city_input)

        // Set the text from shared preferences
        etUsername.text = "$username"
        etEmail.text = "$email"
        etContact.text = "$contact"
        etCity.text = "$city"
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)

        val newPasswordInput = dialogView.findViewById<EditText>(R.id.newPasswordInput)
        val confirmPasswordInput = dialogView.findViewById<EditText>(R.id.confirmPasswordInput)
        val submitPasswordChange = dialogView.findViewById<Button>(R.id.submitPasswordChange)
        val eyeIconNewPassword = dialogView.findViewById<ImageView>(R.id.eyeIconNewPassword)
        val eyeIconConfirmPassword = dialogView.findViewById<ImageView>(R.id.eyeIconConfirmPassword)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        // Toggle visibility for New Password
        var isPasswordVisible = false
        eyeIconNewPassword.setOnClickListener {
            if (isPasswordVisible) {
                newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeIconNewPassword.setImageResource(R.drawable.ic_eye_closed) // Eye Off icon
            } else {
                newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIconNewPassword.setImageResource(R.drawable.ic_eye_open) // Eye On icon
            }
            isPasswordVisible = !isPasswordVisible
        }

        // Toggle visibility for Confirm Password
        var isConfirmPasswordVisible = false
        eyeIconConfirmPassword.setOnClickListener {
            if (isConfirmPasswordVisible) {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeIconConfirmPassword.setImageResource(R.drawable.ic_eye_closed) // Eye Off icon
            } else {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIconConfirmPassword.setImageResource(R.drawable.ic_eye_open) // Eye On icon
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible
        }

        // Set click listener for the submit button in the dialog
        submitPasswordChange.setOnClickListener {
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Call the function to update password
                updatePasswordInBackend(newPassword)
                dialog.dismiss()  // Dismiss the dialog after password update
            }
        }

        dialog.show()
    }

    private fun updatePasswordInBackend(newPassword: String) {
        // Get userId from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 1)

        // Make API call to update password
        changePasswordApi.changePassword(userId, newPassword).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error updating password. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Logout")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { _, _ -> logoutUser() }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun logoutUser() {
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Clear all data
        editor.clear()
        editor.apply()

        // Navigate back to login screen (MainActivity)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Optionally, show a toast
        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
    }
}
