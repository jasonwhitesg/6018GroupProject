package com.example.phase1

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
import com.example.phase1.databinding.FragmentLetsDrawBinding
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import android.graphics.Canvas



class LetsDraw : Fragment() {

    private val viewModel: SimpleView by activityViewModels()
    private lateinit var binding: FragmentLetsDrawBinding
    private lateinit var bitmap : Bitmap
//        get() {
//           return binding.customView.getCurrentBitmap()
//        }

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
    ): View {
        binding = FragmentLetsDrawBinding.inflate(inflater, container, false)

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bitmap = binding.customView.getCurrentBitmap()
        viewModel.updateBitmap(bitmap)
        if(binding.colorPickerContainer.visibility == View.VISIBLE){
            closeColorPicker()
        }
    }


}
