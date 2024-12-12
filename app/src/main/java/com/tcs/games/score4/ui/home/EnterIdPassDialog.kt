package com.tcs.games.score4.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogEnterIdPassBinding
import dagger.hilt.android.AndroidEntryPoint
import data.repository.GameDetailsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EnterIdPassDialog:DialogFragment() {
    private var _binding: DialogEnterIdPassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnterIdPassViewModel by viewModels()
    @Inject
    lateinit var gameDetailsRepository:GameDetailsRepository

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
            verifyCredentials()

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

    private fun verifyCredentials(){
        val id=binding.verifyCredsId.text.toString().uppercase()
        val pass=binding.verifyCredsPassword.text.toString()
        if(id.isNotEmpty()&&pass.isNotEmpty()){
           if(viewModel.verifyLocally(id,pass)){

               //TODO show a loading bar
               isInputAllowed(false)
               lifecycleScope.launch {
                   val result=viewModel.verifyGlobally(id,pass)
                   if(result!=null){
                       val userAdded=viewModel.joinGameRoom(result)
                   Log.d("Players",userAdded.isSuccess.toString())
                       if(userAdded.isSuccess){
                           dismiss()
                           findNavController().navigate(R.id.action_dialog_enter_credentials_to_waiting_room)
                       }else{
                           isInputAllowed(true)
                           Toast.makeText(requireContext(),"Game finished or room full",Toast.LENGTH_LONG).show()
                       }
                   }else{
                       Toast.makeText(requireContext(),"Unable to find game room",Toast.LENGTH_SHORT).show()
                       isInputAllowed(true)
                   }
               }
           }else{
               isInputAllowed(true)
               Toast.makeText(requireContext(),"Invalid Entries",Toast.LENGTH_SHORT).show()
           }
        }
    }
    private fun isInputAllowed(isAllowed:Boolean){
        if(isAllowed){
            binding.dialogIdPassJoin.visibility=View.VISIBLE
            binding.enterIdPassDialogProgress.visibility=View.GONE
        }else{
            binding.dialogIdPassJoin.visibility=View.GONE
            binding.enterIdPassDialogProgress.visibility=View.VISIBLE
        }
        binding.dialogIdPassCancel.isEnabled=isAllowed
        binding.verifyCredsId.isEnabled=isAllowed
        binding.verifyCredsPassword.isEnabled=isAllowed
    }
}