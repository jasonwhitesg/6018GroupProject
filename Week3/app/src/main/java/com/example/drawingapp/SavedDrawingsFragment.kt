package com.example.drawingapp

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController




//some random comment
class SavedDrawingsFragment : Fragment() {
    // Get the ViewModel using the custom factory
    private val viewModel: SavedDrawingsViewModel by viewModels {
        DrawingViewModelFactory((requireActivity().application as DrawingApplication).drawingRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SavedDrawingsScreen(viewModel) { selectedDrawing ->
                    // Handle the drawing click here
                    // For now, we'll just print the selected drawing
                    println("Selected drawing: $selectedDrawing")
                }
            }

        }
    }


    @Composable
    fun SavedDrawingsScreen(viewModel: SavedDrawingsViewModel, onDrawingClick: (String) -> Unit) {
        val allDrawings by viewModel.allSavedDrawings.observeAsState(emptyList())
        val navController = findNavController()

        Scaffold(
            topBar = {
                // Add a top app bar if needed
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .padding(contentPadding)
            ) {
                Text(text = "Saved Drawings Fragment")

                LazyColumn {
                    items(allDrawings) { drawing ->
                        Box(
                            modifier = Modifier.clickable {
                                onDrawingClick(drawing.savedFile)
//                                val action = SavedDrawingsFragmentDirections.actionBackToDrawingFragment()
                                val bundle = bundleOf("filePath" to drawing.savedFile)
                                Log.d(drawing.savedFile, "saved file path")
                                findNavController().navigate(R.id.action_back_to_drawingFragment, bundle)

//                                navController.navigate(action)
                            }
                        ) {
                            Text(text = drawing.savedFile)
                        }
                    }
                }
            }
        }
    }
}



