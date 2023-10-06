package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.drawingapp.databinding.FragmentDrawingBinding
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import android.graphics.Canvas
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.content.Context
import java.io.ByteArrayOutputStream
import androidx.lifecycle.lifecycleScope
import android.util.Log
import android.graphics.BitmapFactory


class DrawingFragment : Fragment() {

    private val viewModel: SimpleView by activityViewModels()
    private lateinit var binding: FragmentDrawingBinding
    private lateinit var bitmap : Bitmap
    private lateinit var drawingRepository: DrawingRepository
    private var lastSavedFilePath: String? = null

    fun combineBitmaps(bitmapOne: Bitmap, bitmapTwo: Bitmap): Bitmap {
        val combinedBitmap = Bitmap.createBitmap(bitmapOne.width, bitmapOne.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        // Draw the first bitmap onto the canvas at (0, 0)
        canvas.drawBitmap(bitmapOne, 0f, 0f, null)
        // Draw the second bitmap onto the canvas at the end of the first bitmap
        canvas.drawBitmap(bitmapTwo, 0f, 0f, null)
        return combinedBitmap
    }
    fun closeColorPicker(){
        bitmap = combineBitmaps(bitmap, binding.customView.getCurrentBitmap())
        binding.bottomMenuContainer.visibility = View.VISIBLE
        binding.colorPickerContainer.visibility = View.GONE
        viewModel.updateBitmap(bitmap)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

    binding = FragmentDrawingBinding.inflate(inflater, container, false)
        drawingRepository = DrawingRepository(lifecycleScope, DrawingDatabase.getDatabase(requireContext()).drawingDao())

        // Link CustomView with ViewModel
        binding.customView.viewModel = viewModel

        // Observe changes in bitmap from ViewModel
        viewModel.bitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.customView.setBitmap(bitmap)
        }

        val colorPickerButton = binding.button3
        bitmap = binding.customView.getCurrentBitmap()

        //on touch color picker button
        colorPickerButton.setOnClickListener {
            bitmap = binding.customView.getCurrentBitmap()
            // Update ViewModel with the final drawing
            binding.bottomMenuContainer.visibility = View.GONE
            binding.colorPickerContainer.visibility = View.VISIBLE

            viewModel.updateBitmap(bitmap)
        }
        val clearDrawingButton = binding.button7
        clearDrawingButton.setOnClickListener {
            binding.customView.clearBitmap()
        }

        val saveDrawingButton = binding.button4
        saveDrawingButton.setOnClickListener {
            if (lastSavedFilePath == null) {
                showDrawingNameDialog()
            } else {
                val currentBitmap = binding.customView.getCurrentBitmap()
                saveBitmapToInternalStorage(currentBitmap, lastSavedFilePath!!.substringAfterLast('/'), requireContext())
                Log.d("DrawingFragment", "Saved file path: $lastSavedFilePath")
            }        }

        val loadDrawingButton = binding.button6
        loadDrawingButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_savedDrawingsFragment)
        }


        //On touch center of color picker
        val imageView = binding.imageView
        imageView.setOnClickListener{
            closeColorPicker()
        }
        val colorPicker = binding.colorPicker
        val customView = binding.customView
        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                customView.paint.color = color
                // Do whatever you want with the color
                imageView.background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        })
        val sizeSlider = binding.sizeSlider
        sizeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change here
                customView.paint.strokeWidth = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts dragging the thumb
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops dragging the thumb
            }


        })
// Check if arguments were provided when the fragment was navigated to
        val arguments = arguments
        if (arguments != null) {
            // Arguments were provided, you can access them here
            val bundleValue = arguments.getString("filePath") // Replace "key" with the actual key you used
            // Do something with the bundleValue or other arguments
            Log.d(bundleValue, "<-- should navigate here")
            if (bundleValue != null) {
                loadDrawingIntoCustomView(bundleValue)
            }
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to ClickFragment
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if(binding.colorPickerContainer.visibility == View.VISIBLE){
            closeColorPicker()
        }
        else{
            bitmap = binding.customView.getCurrentBitmap()
            viewModel.updateBitmap(bitmap)
        }
    }

    private fun showDrawingNameDialog() {
        val editText = EditText(context)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Name Your Drawing")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val drawingName = editText.text.toString()

                // Get the current bitmap
                val currentBitmap = binding.customView.getCurrentBitmap()

                // Save the bitmap to internal storage and get the file path
                val savedFilePath = saveBitmapToInternalStorage(currentBitmap, "$drawingName.png", requireContext())
                lastSavedFilePath = savedFilePath // update file path
                Log.d("DrawingFragment", "Saved file path: $savedFilePath")

                // Save the file path to the repository
                drawingRepository.saveDrawing(savedFilePath)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    fun saveBitmapToInternalStorage(bitmap: Bitmap, filename: String, context: Context): String {
        val bitmapToPNG = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapToPNG)

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(bitmapToPNG.toByteArray())
        }

        bitmapToPNG.close()

        return context.filesDir.absolutePath + "/" + filename
    }


//do update bitmap not set bitmap
    private fun loadDrawingIntoCustomView(filePath: String) {
    lastSavedFilePath = filePath
        val bitmap: Bitmap? = BitmapFactory.decodeFile(filePath)
        if (bitmap != null) {
            viewModel.updateBitmap(bitmap)
        }
    }

}


