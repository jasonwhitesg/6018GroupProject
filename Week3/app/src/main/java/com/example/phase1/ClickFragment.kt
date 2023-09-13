package com.example.phase1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.phase1.databinding.FragmentClickBinding

class ClickFragment : Fragment() {

    private val viewModel: SimpleView by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentClickBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            // Check if circles are null or empty
            if (viewModel.circlesLiveData.value != null || viewModel.circlesLiveData.value!!.isNotEmpty()) {
                viewModel.clearCircles(requireContext())
            }


            // Generate a random color (if needed)
            viewModel.randomColor()

            // Transition to DrawFragment
            val drawFragment = LetsDraw()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, drawFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}