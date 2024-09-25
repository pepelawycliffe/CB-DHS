package com.example.cb_dhs.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.cb_dhs.databinding.FragmentRegisterBinding
import com.example.cb_dhs.model.RegisterRequest
import com.example.cb_dhs.utils.Resource
import com.example.cb_dhs.utils.hide
import com.example.cb_dhs.utils.show
import com.example.cb_dhs.utils.snackbar
import com.example.cb_dhs.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), View.OnFocusChangeListener, TextWatcher {
    private lateinit var mNavController: NavController
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 2

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)


        binding.apply {
            etSignUpFullName.onFocusChangeListener = this@RegisterFragment
            etSignUpEmail.onFocusChangeListener = this@RegisterFragment
            etSignUpPassword.onFocusChangeListener = this@RegisterFragment
            etSignUpConPassword.onFocusChangeListener = this@RegisterFragment
        }

        binding.apply {
            etSignUpConPassword.addTextChangedListener(this@RegisterFragment)
        }


        binding.btnSignUp.setOnClickListener {
            register()
//            getUserInfo()
        }

        binding.tvBackToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            mNavController.navigate(action)
        }

        authViewModel.registerResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.show()
                }

                is Resource.Success -> {
                    binding.progressBar.hide()

                    // save token in shared preference
                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveAuthToken(it.data!!.token)

                    val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                    mNavController.navigate(action)
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

    private fun register() {
        if (!validateAll()) {
            return
        }
        val name = binding.etSignUpFullName.text.toString().trim()
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val avatar = "https://aui.atlassian.com/aui/8.8/docs/images/avatar-person.svg"

        val registerRequest = RegisterRequest(name, email, password, avatar)

        authViewModel.register(registerRequest)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.et_signUp_fullName -> {
                    if (hasFocus) {
                        if (binding.signUpFullName.isErrorEnabled) {
                            binding.signUpFullName.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }

                R.id.et_signUp_email -> {
                    if (hasFocus) {
                        if (binding.signUpEmail.isErrorEnabled) {
                            binding.signUpEmail.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.et_signUp_password -> {
                    if (hasFocus) {
                        if (binding.signUpPassword.isErrorEnabled) {
                            binding.signUpPassword.isErrorEnabled = false
                        }
                    } else {
                        binding.signUpConPassword.setStartIconDrawable(R.drawable.password)
                        if (validatePassword() && binding.etSignUpPassword.text!!.isNotEmpty() &&
                            validateConPassword() && validatePasswordAndConPassword()
                        ) {
                            if (binding.signUpConPassword.isErrorEnabled) {
                                binding.signUpConPassword.isErrorEnabled = false
                            }
                            binding.signUpConPassword.setStartIconDrawable(R.drawable.ic_correct)
                        }
                    }
                }

                R.id.et_signUp_conPassword -> {
                    if (hasFocus) {
                        if (binding.signUpConPassword.isErrorEnabled) {
                            binding.signUpConPassword.isErrorEnabled = false
                        }
                    } else {
                        binding.signUpConPassword.setStartIconDrawable(R.drawable.password)
                        if (validateConPassword() && validatePassword() && validatePasswordAndConPassword()) {
                            if (binding.signUpPassword.isErrorEnabled) {
                                binding.signUpPassword.isErrorEnabled = false
                            }
                            binding.signUpConPassword.setStartIconDrawable(R.drawable.ic_correct)
                        }
                    }
                }
            }
        }
    }


    private fun validateAll(): Boolean {
        return if (!validateFullName()) {
            false
        } else if (!validateEmail()) {
            false
        } else if (!validatePassword()) {
            false
        } else if (!validateConPassword())
            false
        else validatePasswordAndConPassword()
    }

    private fun validateFullName(): Boolean {
        var errorMes: String? = null
        val firstName = binding.etSignUpFullName.text.toString()
        if (firstName.isEmpty()) {
            errorMes = "First name is required"
        }

        if (errorMes != null) {
            binding.signUpFullName.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun validateEmail(): Boolean {
        var errorMes: String? = null
        val email = binding.etSignUpEmail.text.toString()
        if (email.isEmpty()) {
            errorMes = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMes = "Email address is invalid"
        }

        if (errorMes != null) {
            binding.signUpEmail.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun validatePassword(): Boolean {
        var errorMes: String? = null
        val password = binding.etSignUpPassword.text.toString()
        if (password.isEmpty()) {
            errorMes = "Password is required"
        } else if (password.length < 6) {
            errorMes = "Password must be 6 chars long"
        }

        if (errorMes != null) {
            binding.signUpPassword.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun validateConPassword(): Boolean {
        var errorMes: String? = null
        val conPassword = binding.etSignUpConPassword.text.toString()
        if (conPassword.isEmpty()) {
            errorMes = "Confirm password is required"
        } else if (conPassword.length < 6) {
            errorMes = "Confirm password must be 6 chars long"
        }

        if (errorMes != null) {
            binding.signUpConPassword.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    private fun validatePasswordAndConPassword(): Boolean {
        var errorMes: String? = null
        val password = binding.etSignUpPassword.text.toString()
        val conPassword = binding.etSignUpConPassword.text.toString()
        if (password != conPassword) {
            errorMes = "Confirm password doesn't much with password"
        }

        if (errorMes != null) {
            binding.signUpConPassword.apply {
                isErrorEnabled = true
                error = errorMes
            }
        }

        return errorMes == null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        val pass = binding.etSignUpPassword.text.toString()
        val conPass = binding.etSignUpConPassword.text.toString()
        if (pass == conPass && conPass.isNotEmpty() && validatePassword()) {
            binding.signUpConPassword.setStartIconDrawable(R.drawable.ic_correct)
        } else {
            binding.signUpConPassword.setStartIconDrawable(R.drawable.password)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            Log.i("image", "" + selectedImageUri)
            // Process the selected image URI here
            // For example, you can display the image in an ImageView
//            binding.ivAvatar.setImageURI(selectedImageUri)
        }
    }


}