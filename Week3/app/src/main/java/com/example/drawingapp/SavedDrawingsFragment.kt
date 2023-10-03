package com.example.drawingapp
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class SavedDrawingsFragment : Fragment() {
    @Composable
    fun DrawingsList() {
        val drawings = listOf("Drawing 1", "Drawing 2", "Drawing 3") // Replace with your actual list of drawings

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(drawings) { drawing ->
                DrawingListItem(drawing)
            }
        }
    }
//
    @Composable
    fun DrawingListItem(drawing: String) {
        // Composable element for each drawing item goes here
    }
//
//    // Fragment code goes here
}
