package com.tcs.games.score4.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentLoginBinding
import com.tcs.games.score4.utils.AlertDialogManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding ?= null
    private val binding get() = _binding!!
    private val viewModel:LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setObserver()
    }
    private fun setObserver(){
        viewModel.user.observe(viewLifecycleOwner){user->
            if(user!=null){
                AlertDialogManager.hideDialog()
                viewModel.user.removeObservers(viewLifecycleOwner)
                findNavController().navigate(R.id.action_fragment_login_to_fragment_home)
            }

        }

        viewModel.loadingMessage.observe(viewLifecycleOwner){message->
            if(message.equals("Error")){
                AlertDialogManager.hideDialog()
                Toast.makeText(requireContext(),"Unable to Login",Toast.LENGTH_LONG).show()
            }else{
                AlertDialogManager.updateProgressDialogText(message)
            }
        }
    }

    private fun setOnClickListeners(){
        binding.loginGoogle.setOnClickListener{
            lifecycleScope.launch {
                AlertDialogManager.showLoadingDialog(
                    requireContext(),
                    true,
                    "Signing in with Google"
                )
                signInWithGoogle()
            }
        }
        binding.loginAnonymous.setOnClickListener{
            lifecycleScope.launch {
                AlertDialogManager.showLoadingDialog(
                    requireContext(),
                    true,
                    "Signing in with Anonymously"
                )
            }
            viewModel.signInAnonymousLy()
        }
    }

    private suspend fun signInWithGoogle() {
        try {
            val context = requireContext() // Avoid repeated calls

            val gIO = GetSignInWithGoogleOption.Builder(
                serverClientId = context.getString(R.string.default_web_client_id)
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(gIO)
                .build()

            val result = CredentialManager.create(context)
                .getCredential(request = request, context = context)

            val idTokenCredential = result.credential as? GoogleIdTokenCredential
                ?: throw IllegalStateException("Invalid Google Sign-In credential")

            val idToken = idTokenCredential.idToken
            viewModel.signInWithGoogle(idToken)
        } catch (e: Exception) {
            AlertDialogManager.hideDialog()
            Log.e("SignIn", "Google Sign-In failed: ${e.message}")
        }
    }

}