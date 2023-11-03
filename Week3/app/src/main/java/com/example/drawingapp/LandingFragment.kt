package com.example.drawingapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController

class LandingFragment : Fragment() {

    private val TAG = "LandingFragment"
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Log when onCreateView is called
        Log.d(TAG, "onCreateView: started")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_landing, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find views by ID
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val letsDrawLoginButton = view.findViewById<Button>(R.id.button)
        val signUpButton = view.findViewById<Button>(R.id.button5)
        val statusTextView = view.findViewById<TextView>(R.id.statusTextView)
//        val navigateButton = view.findViewById<Button>(R.id.button5)

        // Set up click listeners
        letsDrawLoginButton.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            val email = "keegan.dohm@gmail.com"//emailEditText.text.toString()
            val password = "password"//passwordEditText.text.toString()
            loginUser(email, password, statusTextView)
        }

        signUpButton.setOnClickListener {
            Log.d(TAG, "Sign up button clicked")
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            createUser(email, password, statusTextView)
        }

        findNavController().navigate(R.id.action_to_drawingFragment)


        return view
    }

    private fun loginUser(email: String, password: String, statusTextView: TextView) {

        if (email.isBlank() || password.isBlank()) {
            if (email.isBlank()) {
                statusTextView.text = "Please enter a valid email"
            }
            if (password.isBlank()) {
                val existingText = statusTextView.text.toString()
                val newText = if (existingText.isNotEmpty()) {
                    "$existingText\nPlease enter a valid password"
                } else {
                    "Please enter a valid password"
                }
                statusTextView.text = newText
            }
            return
        }

        Log.d(TAG, "Attempting to log in user with email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Login successful")
                    Log.d(TAG, "User's UID: ${auth.currentUser?.uid}")
                    statusTextView.text = "Login successful"

                    val userName = email.substringBefore("@")
                    val bundle = bundleOf("fromLandingFragment" to true, "userName" to userName)
                    findNavController().navigate(R.id.action_to_drawingFragment, bundle)
                } else {
                    Log.e(TAG, "Login failed", task.exception)
                    statusTextView.text = "Login failed: ${task.exception?.message}"
                }
            }
    }

    private fun createUser(email: String, password: String, statusTextView: TextView) {

        if (email.isBlank() || password.isBlank()) {
            if (email.isBlank()) {
                statusTextView.text = "Please enter a valid email"
            }
            if (password.isBlank()) {
                val existingText = statusTextView.text.toString()
                val newText = if (existingText.isNotEmpty()) {
                    "$existingText\nPlease enter a valid password"
                } else {
                    "Please enter a valid password"
                }
                statusTextView.text = newText
            }
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User creation successful")
                    Log.d(TAG, "User's UID: ${auth.currentUser?.uid}")
                    statusTextView.text = "User creation successful"

                    val userName = email.substringBefore("@")
                    val bundle = bundleOf("fromLandingFragment" to true, "userName" to userName)
                    findNavController().navigate(R.id.action_to_drawingFragment, bundle)
                } else {
                    Log.e(TAG, "User creation failed", task.exception)
                    statusTextView.text = "User creation failed: ${task.exception?.message}"
                }
            }
    }

}


