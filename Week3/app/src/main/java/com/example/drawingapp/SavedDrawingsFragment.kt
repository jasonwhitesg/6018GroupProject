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
import androidx.fragment.app.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.fragment.app.activityViewModels
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import java.io.File
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//some random comment
class SavedDrawingsFragment : Fragment() {
    // Get the ViewModel using the custom factory
    private val sharedDrawingsViewModel: SharedDrawingsViewModel by viewModels { SharedDrawingViewModelFactory() }
    private fun shareDrawing(filePath: String) {
//        val fileName = "/jason.png"
//        val root = context?.filesDir?.absolutePath
        val file = File(filePath)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (file.exists() && uid != null) { // Check that the file exists and uid is not null
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val response = sharedDrawingsViewModel.sendFileToServer(file, uid) // uid is now known to be non-null
                    Log.d("FileUploadResponse", response)
                } catch (e: Exception) {
                    Log.e("FileUploadError", "Error sending file to server", e)
                }
            }
        } else if (uid == null) {
            Log.e("FileUploadError", "User ID is null, user might not be logged in.")


        }

    }

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
                SavedDrawingsScreen(
                    viewModel = viewModel,
                    onDrawingClick = { selectedDrawing ->
                        // Handle the drawing click here
                        println("Selected drawing: $selectedDrawing")
                    },
                    navController = navController,
                    shareDrawing = { filePath ->
                        // Implement the sharing functionality here
                        shareDrawing(filePath)
                    }
                )
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
    fun SavedDrawingsScreen(
        viewModel: SavedDrawingsViewModel,
        onDrawingClick: (String) -> Unit,
        navController: NavController,
        shareDrawing: (String) -> Unit
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
                // Add a button at the top of the screen
                Button(
                    onClick = {
                        try {
                            navController.navigate(R.id.action_to_sharedDrawingsFragment)
                        } catch (_: Exception) {
                            Log.d("FAILED TO OPEN SHARED DRAWINGS FRAGMENT", "FUCK")
                        }

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
                                .padding(4.dp)
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
                                    onDrawingClick(drawing.savedFile)
                                    val bundle = bundleOf("filePath" to drawing.savedFile)
                                    Log.d(drawing.savedFile, "saved file path")
                                    navController.navigate(
                                        R.id.action_back_to_drawingFragment,
                                        bundle
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val fileName = drawing.savedFile.substringAfterLast("/")
                                val imageBitmap = loadBitmapFromFile(drawing.savedFile)
                                if (imageBitmap != null) {
                                    // Display the loaded image as a thumbnail
                                    ImageBitmapComposable(
                                        imageBitmap = imageBitmap.asImageBitmap(),
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Centered text
                                        Text(
                                            text = fileName,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier.weight(1f) // Takes up the remaining space
                                        )

                                        val shareIconState = remember { mutableStateOf(true) }

                                        IconButton(
                                            onClick = {
                                                // If the shareIconState is true, call shareDrawing.
                                                if (shareIconState.value) {
                                                    shareDrawing(drawing.savedFile)
                                                    // Optional: Toggle the icon state if you want to change the icon after sharing
                                                    shareIconState.value = !shareIconState.value
                                                } else {
                                                    // Handle delete action
                                                    shareIconState.value = !shareIconState.value
                                                }
                                            },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = if (shareIconState.value) R.drawable.ic_share_icon_shared_drawing else R.drawable.ic_trash_icon_shared_drawing),
                                                contentDescription = if (shareIconState.value) "Share" else "Delete",
                                                tint = if (shareIconState.value) Color.Blue else Color.Red
                                            )
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
}
