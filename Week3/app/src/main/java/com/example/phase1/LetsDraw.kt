package com.example.phase1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.phase1.databinding.FragmentLetsDrawBinding

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

//        // Observe changes in circles from ViewModel and draw them
//        viewModel.circlesLiveData.observe(viewLifecycleOwner) { circles ->
//            binding.customView.drawCircles(circles)
//        }

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
