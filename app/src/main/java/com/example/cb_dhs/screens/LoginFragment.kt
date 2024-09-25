package com.example.cb_dhs.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.cb_dhs.R
import com.example.cb_dhs.api.SessionManager
import com.example.cb_dhs.databinding.FragmentLoginBinding
import com.example.cb_dhs.model.LoginRequest
import com.example.cb_dhs.utils.Resource
import com.example.cb_dhs.utils.hide
import com.example.cb_dhs.utils.show
import com.example.cb_dhs.utils.snackbar
import com.example.cb_dhs.viewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnFocusChangeListener {
    private lateinit var mNavController: NavController
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            etEmailLogin.onFocusChangeListener = this@LoginFragment
            etPasswordLogin.onFocusChangeListener = this@LoginFragment
        }

        val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (acct != null) {
            // transfer to home fragment
        }

        if (!SessionManager(requireContext()).getAuthToken().isNullOrEmpty()) {
            transferToHome()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvSignUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            mNavController.navigate(action)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            binding.progressBar.show()
            googleSignIn()
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.show()
                }

                is Resource.Success -> {
                    binding.progressBar.hide()
                    // save token in shared preference
                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveAuthToken(it.data!!.token)

                    // transfer to home fragment
                    transferToHome()

                }

                is Resource.Error -> {
                    binding.progressBar.hide()
                    requireView().snackbar(it.message.toString()) {
                        it.dismiss()
                    }
                }

                else -> {}
            }
        }

        return binding.root
    }

    private fun login() {
        if (!validateAll()) {
            return
        }

        val email = binding.etEmailLogin.text.toString().trim()
        val password = binding.etPasswordLogin.text.toString().trim()

        val loginRequest = LoginRequest(email, password)

        authViewModel.login(loginRequest)
    }

    private fun googleSignIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 1000)
    }


    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.et_Email_login -> {
                    if (hasFocus) {
                        if (binding.loginEmail.isErrorEnabled) {
                            binding.loginEmail.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.et_password_login -> {
                    if (hasFocus) {
                        if (binding.loginPassword.isErrorEnabled) {
                            binding.loginPassword.isErrorEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }

            }
        }
    }

    private fun validateAll(): Boolean {
        return if (!validateEmail()) {
            false
        } else validatePassword()
    }

    private fun validateEmail(): Boolean {
        var errorMes: String? = null
        val email = binding.etEmailLogin.text.toString()
        if (email.isEmpty()) {
            errorMes = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMes = "Email address is invalid"
        }

        if (errorMes != null) {
            binding.loginEmail.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun validatePassword(): Boolean {
        var errorMes: String? = null
        val password = binding.etPasswordLogin.text.toString()
        if (password.isEmpty()) {
            errorMes = "Password is required"
        } else if (password.length < 6) {
            errorMes = "Password must be 6 chars long"
        }

        if (errorMes != null) {
            binding.loginPassword.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun transferToHome() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        mNavController.navigate(action)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.progressBar.hide()
        if (requestCode == 1000) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // transfer to home fragment
                transferToHome()
            } catch (e: ApiException) {
                // Handle the exception, log an error, or show an appropriate message to the user
                Log.e("SignIn", "Google sign-in failed", e)
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}