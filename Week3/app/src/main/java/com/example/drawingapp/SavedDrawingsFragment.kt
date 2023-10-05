package com.example.drawingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavedDrawingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
