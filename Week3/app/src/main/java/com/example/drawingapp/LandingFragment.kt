package com.example.drawingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.R
import com.example.drawingapp.databinding.FragmentLandingBinding

class LandingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLandingBinding.inflate(inflater, container, false)
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_to_letsDraw)
        }
//        binding.button.setOnClickListener {
//
//            // Transition to DrawFragment
//            val drawFragment = LetsDraw()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragmentContainerView, drawFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }

        return binding.root
    }
}