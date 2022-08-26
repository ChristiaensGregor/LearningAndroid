package com.gregorchristiaens.learningandroid.second

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gregorchristiaens.learningandroid.databinding.FragmentSecondBinding
import com.gregorchristiaens.learningandroid.R

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    /**
     * This property is only valid between onCreateView and onDestroyView.
     **/
    private val binding get() = _binding!!
    private lateinit var viewModel: SecondViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SecondViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("default value", viewModel.defaultText.value.toString())
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}