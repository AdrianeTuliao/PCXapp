package com.example.pcxlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

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

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Logout")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

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
