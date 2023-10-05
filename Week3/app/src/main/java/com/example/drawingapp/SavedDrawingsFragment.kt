package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//some random comment
class SavedDrawingsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SavedDrawingsScreen()
            }
        }
    }
}


@Composable
fun SavedDrawingsScreen() {
    Scaffold(
        topBar = {
            // Add a top app bar if needed
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize() // Use Modifier to fill the available space
        ) {
            // Your Composable UI elements for the fragment go here
            Text(text = "Saved Drawings Fragment")
        }
    }
}
