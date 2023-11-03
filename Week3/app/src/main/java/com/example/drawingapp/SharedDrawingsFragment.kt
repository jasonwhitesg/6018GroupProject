package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.foundation.lazy.items

//some random comment
class SharedDrawingsFragment : Fragment() {
    // Get the ViewModel using the custom factory

    //per whitney's suggestion changed to by "activityViewModels" instead of by "viewModels"
    private val viewModel: SharedDrawingsViewModel by activityViewModels {
        DrawingViewModelFactory((requireActivity().application as DrawingApplication).drawingRepository)
    }
    private fun getSharedDrawings(): LiveData<List<Drawing>> {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.updateSharedDrawingsList()
        }
        return viewModel.getDrawingsLiveData()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navController = findNavController() // Fetch NavController

        return ComposeView(requireContext()).apply {
            setContent {
                SharedDrawingsScreen(viewModel, { selectedDrawing ->
                    // Handle the drawing click here
                    println("Selected drawing: $selectedDrawing")
                }, navController)  // Pass the NavController here
            }
        }
    }


    private fun loadBitmapFromFile(filePath: String): Bitmap? {
        val file = File(filePath)
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.absolutePath)
        }
        return null
    }
    @Composable
    fun ImageBitmapComposable(
        imageBitmap: ImageBitmap,
        modifier: Modifier = Modifier
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
    @Composable
    fun SharedDrawingsScreen(
        viewModel: SharedDrawingsViewModel,
        onDrawingClick: (String) -> Unit,
        navController: NavController
    ) {

        val allDrawings: List<Drawing> by viewModel.getDrawingsLiveData().observeAsState(emptyList())


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
                // Add a button at the top of the screen
                Button(
                    onClick = {
                        // Handle button click action here
                    },
                    shape = RoundedCornerShape(16.dp), // Specify the corner radius
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Import")
                }
                LazyColumn {
                    items(allDrawings) { drawing ->
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp) // Specify the corner radius
                                )
                                .border(
                                    width = 3.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onDrawingClick(drawing.filePath)
                                    val bundle = bundleOf("filePath" to drawing.filePath)
                                    Log.d(drawing.filePath, "saved file path")
                                    navController.navigate(
                                        R.id.action_back_to_drawingFragment,
                                        bundle
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val fileName = drawing
                                val imageBitmap = loadBitmapFromFile(drawing.filePath)
                                if (imageBitmap != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Display the loaded image as a thumbnail
                                        ImageBitmapComposable(
                                            imageBitmap = imageBitmap.asImageBitmap(),
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        // Centered text
//                                        Text(
//                                            text = fileName,
//                                            style = TextStyle(fontSize = 22.sp),
//                                            modifier = Modifier.fillMaxWidth()
//                                        )
//                            val fileName = drawing.savedFile.substringAfterLast("/")
//                            Text(text = fileName, style = TextStyle(fontSize = 22.sp))

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}