package com.example.phase1

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.phase1.databinding.FragmentLetsDrawBinding
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener


class LetsDraw : Fragment() {

    private val viewModel: SimpleView by activityViewModels()
    private lateinit var binding: FragmentLetsDrawBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLetsDrawBinding.inflate(inflater, container, false)

        // Link CustomView with ViewModel
        binding.customView.viewModel = viewModel

//        // Load saved circles when the view is created (if any exist)
//        viewModel.loadCircles(requireContext()) // Load circles from ViewModel
//

        // Observe changes in bitmap from ViewModel
        viewModel.bitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.customView.setBitmap(bitmap)
        }
        val button = binding.button3
        button.setOnClickListener {
            binding.bottomMenuContainer.visibility = View.GONE
            binding.colorPickerContainer.visibility = View.VISIBLE
        }
        val imageView = binding.imageView
        imageView.setOnClickListener{
            binding.bottomMenuContainer.visibility = View.VISIBLE
            binding.colorPickerContainer.visibility = View.GONE
        }

//        // Observe changes in circles from ViewModel and draw them
//        viewModel.circlesLiveData.observe(viewLifecycleOwner) { circles ->
//            binding.customView.drawCircles(circles)
//        }
        val colorPicker = binding.colorPicker
        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                binding.customView.paint.color = color
                // Do whatever you want with the color
                imageView.background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        })


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveCircles(requireContext())
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCircles(requireContext())
    }

}
