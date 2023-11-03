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
import android.content.Intent
import java.io.ByteArrayOutputStream
import androidx.lifecycle.lifecycleScope
import android.util.Log
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class DrawingFragment : Fragment() {

    private val viewModel: SimpleView by activityViewModels()
    private lateinit var binding: FragmentDrawingBinding
    private lateinit var bitmap: Bitmap
    private lateinit var drawingRepository: DrawingRepository
    private var lastSavedFilePath: String? = null
    private var tempFilePath: String? = null

    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null

    private fun combineBitmaps(bitmapOne: Bitmap, bitmapTwo: Bitmap): Bitmap {
        val combinedBitmap =
            Bitmap.createBitmap(bitmapOne.width, bitmapOne.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        // Draw the first bitmap onto the canvas at (0, 0)
        canvas.drawBitmap(bitmapOne, 0f, 0f, null)
        // Draw the second bitmap onto the canvas at the end of the first bitmap
        canvas.drawBitmap(bitmapTwo, 0f, 0f, null)
        return combinedBitmap
    }

    private fun closeColorPicker() {
        bitmap = combineBitmaps(bitmap, binding.customView.getCurrentBitmap())
        binding.bottomMenuContainer.visibility = View.VISIBLE
        binding.colorPickerContainer.visibility = View.GONE
        viewModel.updateBitmap(bitmap)
    }

    private fun showDrawingNameDialog() {
        val editText = EditText(context)
        AlertDialog.Builder(requireContext())
            .setTitle("Name Your Drawing")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val drawingName = editText.text.toString()

                // Get the current bitmap
                val currentBitmap = binding.customView.getCurrentBitmap()

                // Save the bitmap to internal storage and get the file path
                val savedFilePath =
                    saveBitmapToInternalStorage(
                        currentBitmap,
                        "$drawingName.png",
                        requireContext()
                    )
                lastSavedFilePath = savedFilePath // update file path
                Log.d("DrawingFragment", "Saved file path: $savedFilePath")

                // Save the file path to the repository
                drawingRepository.saveDrawing(savedFilePath)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBitmapToInternalStorage(
        bitmap: Bitmap,
        filename: String,
        context: Context
    ): String {
        val bitmapToPNG = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapToPNG)

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(bitmapToPNG.toByteArray())
        }

        bitmapToPNG.close()

        return context.filesDir.toString() + "/$filename"
    }

    //do update bitmap not set bitmap
    private fun loadDrawingIntoCustomView(filePath: String) {
        lastSavedFilePath = filePath
        val bitmap: Bitmap? = BitmapFactory.decodeFile(filePath)
        if (bitmap != null) {
            viewModel.updateBitmap(bitmap)
        }
    }

    private fun restoreColorAndSliderValues() {
        // Retrieve the selected color and slider value from the ViewModel
        val selectedColor = viewModel.getSelectedColor()
        val selectedSliderValue = viewModel.getSelectedSliderValue()

        // Update the color and slider value
        binding.customView.paint.color = selectedColor
        binding.sizeSlider.progress = selectedSliderValue
    }

    private fun restoreBallColorAndSize() {
        val ballColor = viewModel.getBallColor()
        val ballSize = viewModel.getBallSize().toFloat()
        binding.customView.setBallColor(ballColor)
        binding.customView.setBallSize(ballSize)
    }

    private fun restoreBallPosition() {
        // Log entry point of the function
        Log.d("DrawingApp", "restoreBallPosition - Entering function")

        // Retrieve ball position from the ViewModel
        val (x, y) = viewModel.getBallPosition()
        Log.d("DrawingApp", "restoreBallPosition - Retrieved ball position: x = $x, y = $y")

        // Check if the ball position is not null and set it on the custom view
        if (x != null && y != null) {
            Log.d(
                "DrawingApp",
                "restoreBallPosition - Ball position is valid, setting on custom view"
            )
            binding.customView.setBallPosition(x, y)
            Log.d(
                "DrawingApp",
                "restoreBallPosition - Ball position set successfully: x = $x, y = $y"
            )
        } else {
            // Log a message if the ball position is null
            Log.d(
                "DrawingApp",
                "restoreBallPosition - Ball position is null, not setting on custom view"
            )
        }

        // Log exit point of the function
        Log.d("DrawingApp", "restoreBallPosition - Exiting function")
    }

    private fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    private fun getFileUri(context: Context, filePath: String): Uri? {
        val file = File(filePath)
        return FileProvider.getUriForFile(context, "com.example.drawingapp.fileprovider", file)
    }

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
    }

    override fun onResume() {
        super.onResume()

        val (x, y) = viewModel.getBallPosition()
        Log.d("Fragment", "onResume - Retrieved ball position from ViewModel: x = $x, y = $y")

        if (x != null && y != null) {
            binding.customView.setBallPosition(x, y)
            Log.d("Fragment", "onResume - Ball position set on CustomView: x = $x, y = $y")
        } else {
            Log.d("Fragment", "onResume - Ball position is null in ViewModel")
        }

        gravitySensor?.let {
            val isRegistered = sensorManager?.registerListener(
                binding.customView,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
            if (isRegistered == true) {
                Log.d("Fragment", "onResume - Sensor listener registered successfully")
            } else {
                Log.d("Fragment", "onResume - Failed to register sensor listener")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(binding.customView)

        val (x, y) = viewModel.getBallPosition()
        Log.d("Fragment", "onPause - Retrieved ball position from CustomView: x = $x, y = $y")

        if (x != null && y != null) {
            binding.customView.setBallPosition(x, y)
            viewModel.updateBallPosition(x, y)
//c            Log.d("Fragment", "onPause - Updated ball position in ViewModel: x = $x, y = $y")
        } else {
            Log.d("Fragment", "onPause - Ball position is null in CustomView")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDrawingBinding.inflate(inflater, container, false)
        drawingRepository = DrawingRepository(
            lifecycleScope,
            DrawingDatabase.getDatabase(requireContext()).drawingDao()
        )

        // Link CustomView with ViewModel
        binding.customView.viewModel = viewModel

        // Observe changes in bitmap from ViewModel
        viewModel.bitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.customView.setBitmap(bitmap)
        }
        //GET BITMAP
        bitmap = binding.customView.getCurrentBitmap()

        //COLOR PICKER BUTTON LISTENER
        binding.colorPickerButton.setOnClickListener {
            bitmap = binding.customView.getCurrentBitmap()
            // Update ViewModel with the final drawing
            binding.bottomMenuContainer.visibility = View.GONE
            binding.colorPickerContainer.visibility = View.VISIBLE

            viewModel.updateBitmap(bitmap)
        }

        //CLEAR BUTTON LISTENER
        binding.clearButton.setOnClickListener {
            binding.customView.clearBitmap()
        }

        //SAVE BUTTON LISTENER
        binding.saveButton.setOnClickListener {
            if (lastSavedFilePath == null) {
                showDrawingNameDialog()
            } else {
                val currentBitmap = binding.customView.getCurrentBitmap()
                saveBitmapToInternalStorage(
                    currentBitmap,
                    lastSavedFilePath!!.substringAfterLast('/'),
                    requireContext()
                )
            }
        }

        //LOAD BUTTON LISTENER
        binding.loadButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_savedDrawingsFragment)
        }

        //LOAD BUTTON LISTENER
        binding.imageView.setOnClickListener {
            closeColorPicker()
        }

        //COLOR PICKER LISTENER
        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                if (!binding.toggleButton.isChecked) { // "Ball" mode
                    binding.customView.setBallColor(color)
                    viewModel.updateBallColor(color)
                } else { // "Pen" mode
                    binding.customView.paint.color = color
                    viewModel.updateSelectedColor(color) // Update the selected color in ViewModel
                }
                binding.imageView.background.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        })

        //SIZE SLIDER LISTENER
        binding.sizeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val newSize = progress.toFloat() // Convert the progress to float

                if (!binding.toggleButton.isChecked) {
                    binding.customView.setBallSize(newSize)
                    viewModel.updateBallSize(newSize) // Update Ball SIze
                } else {
                    binding.customView.paint.strokeWidth = newSize
                    viewModel.updateSelectedSliderValue(progress) // Update the selected slider value in ViewModel
                }
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

        // Call the function to restore color and slider values
        restoreColorAndSliderValues()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromLandingFragment = arguments?.getBoolean("fromLandingFragment", false) ?: false
        val userName = arguments?.getString("userName")

        if (fromLandingFragment && userName != null) {
//            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Welcome $userName")
                .setMessage("Let's Draw Something!")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            // Handle the toggle button state change
            binding.customView.setMode(isChecked)
        }

        // Handle back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call the function to restore color and slider values
                restoreColorAndSliderValues()

                // Navigate back to ClickFragment
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Observe changes in ball size
        viewModel.ballSize.observe(viewLifecycleOwner) { size ->
            binding.customView.setBallSize(size)
        }

        binding.shareButton.setOnClickListener {
            Log.d("DEBUG", "shareButton clicked")
            val filePath = lastSavedFilePath
            if (filePath != null) {
                val fileExists = doesFileExist(filePath)
                if (fileExists) {
                    Log.d("FILE EXISTENCE", "FILE EXISTS")
                } else {
                    Log.d("FILE EXISTENCE", "FILE DOES NOT EXIST")
                }
                val uri = getFileUri(requireContext(), filePath)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            } else {
                val currentBitmap = binding.customView.getCurrentBitmap()

                // Save the bitmap to internal storage and get the file path
                val drawingName = "tempFIle"

                val filePath =
                    saveBitmapToInternalStorage(
                        currentBitmap,
                        "$drawingName.png",
                        requireContext()
                    )

                val uri = getFileUri(requireContext(), filePath)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, null))
                Log.e("ERROR", "lastSavedFilePath is null")

//                    deleteFileFromInternalStorage("$drawingName.png", requireContext())
            }

        }

        // Observe changes in ball color
        viewModel.ballColor.observe(viewLifecycleOwner) { color ->
            binding.customView.setBallColor(color)
        }

        viewModel.ballX.observe(viewLifecycleOwner) { x ->
            if (x != null) {
                val y = viewModel.ballY.value ?: return@observe
                Log.d("BallPosition", "X: $x, Y: $y")  // Log the X and Y positions
                binding.customView.setBallPosition(x, y)
            }
        }

        viewModel.ballY.observe(viewLifecycleOwner) { y ->
            if (y != null) {
                val x = viewModel.ballX.value ?: return@observe
                Log.d("BallPosition", "X: $x, Y: $y")  // Log the X and Y positions
                binding.customView.setBallPosition(x, y)
            }
        }

        // Restore ball state
        restoreBallColorAndSize()
        restoreBallPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val (x, y) = viewModel.getBallPosition()

        Log.d("Fragment", "onDestroyView - Current ball position: x = $x, y = $y")

        if (x != null && y != null) {
            viewModel.updateBallPosition(x, y)
            Log.d(
                "Fragment",
                "onDestroyView - Ball position updated in ViewModel: x = $x, y = $y"
            )
        }

        if (binding.colorPickerContainer.visibility == View.VISIBLE) {
            closeColorPicker()
        } else {
            bitmap = binding.customView.getCurrentBitmap()
            viewModel.updateBitmap(bitmap)
        }
    }


}




