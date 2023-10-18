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
import androidx.navigation.fragment.findNavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import androidx.fragment.app.activityViewModels


//some random comment
class SavedDrawingsFragment : Fragment() {
    // Get the ViewModel using the custom factory

    //per whitney's suggestion changed to by "activityViewModels" instead of by "viewModels"
    private val viewModel: SavedDrawingsViewModel by activityViewModels {
        DrawingViewModelFactory((requireActivity().application as DrawingApplication).drawingRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navController = findNavController() // Fetch NavController
        return ComposeView(requireContext()).apply {
            setContent {
                SavedDrawingsScreen(viewModel, { selectedDrawing ->
                    // Handle the drawing click here
                    println("Selected drawing: $selectedDrawing")
                }, navController)  // Pass the NavController here
            }
        }
    }

    @Composable
    fun SavedDrawingsScreen(
        viewModel: SavedDrawingsViewModel,
        onDrawingClick: (String) -> Unit,
        navController: NavController
    ) {
        val allDrawings by viewModel.allSavedDrawings.observeAsState(emptyList())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Saved Drawings") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate Back")
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .padding(contentPadding)
            ) {
                LazyColumn {
                    items(allDrawings) { drawing ->
                        Box(
                            modifier = Modifier.clickable {
                                onDrawingClick(drawing.savedFile)
                                val bundle = bundleOf("filePath" to drawing.savedFile)
                                Log.d(drawing.savedFile, "saved file path")
                                navController.navigate(
                                    R.id.action_back_to_drawingFragment,
                                    bundle
                                )
                            }
                        ) {
                            val fileName = drawing.savedFile.substringAfterLast("/")
                            Text(text = fileName, style = TextStyle(fontSize = 22.sp))
                        }
                    }
                }
            }
        }
    }
}




