package com.lchau1017.cc.ui.convert

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lchau1017.cc.R
import com.lchau1017.cc.databinding.FragmentConvertAmountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
@AndroidEntryPoint
class ConvertAmountFragment : Fragment() {

    private var _binding: FragmentConvertAmountBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ConvertAmountViewModel by viewModels()

    private var alert: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConvertAmountBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConvert.setOnClickListener {
            viewModel.convert()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    state.collect { state ->
                        when (state) {
                            is UiState.InitData -> {
                                binding.tvFrom.text = state.fromValue
                                binding.tvTo.text = state.toValue
                            }
                            is UiState.CountDown -> {
                                binding.tvTime.text = getString(R.string.sec, state.seconds)
                            }
                        }
                    }
                }
            }
            countDown()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is Effect.BackToBegin -> {
                            if (alert != null) {
                                alert!!.dismiss()
                            }
                            Snackbar.make(requireView(), R.string.time_out, 3000).show()
                            findNavController().popBackStack()
                        }
                        is Effect.ShowError -> {
                            Snackbar.make(requireView(), effect.error, 3000).show()
                        }
                        is Effect.ShowResult -> {
                            findNavController().navigate(
                                ConvertAmountFragmentDirections.actionConvertAmountFragmentToResultFragment(
                                    amount = effect.amount,
                                    rate = effect.rate
                                )
                            )
                        }
                        is Effect.ShowPopup -> {
                            alert = AlertDialog.Builder(requireContext())
                                .setTitle(R.string.alert_title)
                                .setMessage(
                                    getString(
                                        R.string.alert_desc,
                                        effect.fromValue,
                                        effect.toValue
                                    )
                                )
                                .setPositiveButton(
                                    R.string.alert_confirm
                                ) { dialog, _ ->
                                    viewModel.showResult()
                                    dialog.dismiss()
                                }
                                .setNegativeButton(
                                    R.string.alert_cancel
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }


}