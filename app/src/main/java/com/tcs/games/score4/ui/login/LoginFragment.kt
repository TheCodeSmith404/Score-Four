package com.tcs.games.score4.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding ?= null
    private val binding get() = _binding!!
    private val viewModel:LoginViewModel by viewModels()
    private lateinit var gso:GoogleSignInOptions
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    lifecycleScope.launch {
                        viewModel.signInWithGoogle(idToken)
                    }
                }
            } catch (e: ApiException) {
                Log.e("SignIn", "Google sign-in failed with code: ${e.statusCode}", e)
                Log.d("SignIn","Google sign in message: ${e.message.toString()}")
            }
        }

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
    }

    private fun setOnClickListeners(){
        binding.loginGoogle.setOnClickListener{
            signInWithGoogle()
        }
        binding.loginAnonymous.setOnClickListener{
            viewModel.signInAnonymousLy()
        }
    }

    private fun signInWithGoogle(){
        gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient=GoogleSignIn.getClient(requireContext(),gso)
        val signInIntent=mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

}