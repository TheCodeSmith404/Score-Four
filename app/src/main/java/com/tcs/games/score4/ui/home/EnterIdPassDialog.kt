package com.tcs.games.score4.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogEnterIdPassBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterIdPassDialog:DialogFragment() {
    private var _binding: DialogEnterIdPassBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEnterIdPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }
    private fun setOnClickListeners(){
        binding.dialogIdPassJoin.setOnClickListener{
            dismiss()
            findNavController().navigate(R.id.action_dialog_enter_credentials_to_waiting_room)
        }
        binding.dialogIdPassCancel.setOnClickListener{
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        // Optional: Set the dialog's window size or other configurations
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isCancelable=false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}