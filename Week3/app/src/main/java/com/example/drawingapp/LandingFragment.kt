package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController


class LandingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val navigateButton = view.findViewById<Button>(R.id.button)

        // Set up click listeners
        letsDrawLoginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password, statusTextView)
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            createUser(email, password, statusTextView)
        }

        navigateButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_drawingFragment)
        }

        return view
    }

    private fun loginUser(email: String, password: String, statusTextView: TextView) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    statusTextView.text = "Login successful"
                } else {
                    statusTextView.text = "Login failed: ${task.exception?.message}"
                }
            }
    }

    private fun createUser(email: String, password: String, statusTextView: TextView) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    statusTextView.text = "User creation successful"
                } else {
                    statusTextView.text = "User creation failed: ${task.exception?.message}"
                }
            }
    }
}
