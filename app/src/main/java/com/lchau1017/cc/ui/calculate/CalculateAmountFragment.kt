package com.lchau1017.cc.ui.calculate

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lchau1017.cc.databinding.FragmentCalculateAmountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
@AndroidEntryPoint
class CalculateAmountFragment : Fragment() {

    private var _binding: FragmentCalculateAmountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalculateAmountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalculateAmountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCalculate.setOnClickListener {
            viewModel.calculate(
                binding.etFrom.text.toString(),
                binding.spinFromCurrency.selectedItem.toString(),
                binding.spinToCurrency.selectedItem.toString()
            )
        }

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    state.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.progressBar.isVisible = true
                                binding.btnCalculate.isEnabled = false
                            }
                            is UiState.InitData -> {
                                binding.progressBar.isVisible = false
                                binding.btnCalculate.isEnabled = true
                                binding.spinFromCurrency.adapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.simple_spinner_dropdown_item,
                                    state.fromLabels
                                )
                                binding.spinToCurrency.adapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.simple_spinner_dropdown_item,
                                    state.toLabels
                                )
                            }
                        }
                    }
                }
            }
            getRatesLabels()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is Effect.ToConfirmScreen -> {
                            binding.progressBar.isVisible = false
                            val action =
                                CalculateAmountFragmentDirections.actionCalculateAmountFragmentToConvertAmountFragment(
                                    fromValue = effect.fromValue,
                                    toValue = effect.toValue,
                                    rate = effect.rate
                                )
                            findNavController().navigate(action)
                        }
                        is Effect.ShowError -> {
                            binding.progressBar.isVisible = false
                            binding.btnCalculate.isEnabled = true
                            Snackbar.make(requireView(), effect.error, 3000).show()
                        }
                    }
                }
            }
        }
    }
}

