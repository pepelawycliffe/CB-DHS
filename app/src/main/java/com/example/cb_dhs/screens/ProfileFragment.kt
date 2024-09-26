package com.example.cb_dhs.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.example.cb_dhs.api.SessionManager
import com.example.cb_dhs.databinding.FragmentProfileBinding
import com.example.cb_dhs.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var mNavController: NavController
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        getUserInfo()

        authViewModel.userInfoResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Loading -> {
//                    binding.progressBar.show()
//                }
//
//                is Resource.Success -> {
//                    binding.progressBar.hide()
//                    binding.apply {
//                        tvName.text = it.data!!.user.name
//                        tvEmail.text = it.data.user.email
//                    }
//                }
//
//                is Resource.Error -> {
//                    binding.progressBar.hide()
//                }
//
//                else -> {}
//            }
        }

        return binding.root
    }


    fun getUserInfo() {
        val token = "token=${SessionManager(requireContext()).getAuthToken()}"
//            "token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY0ZDhjYjY5MWZlYjZjZmYyM2VkNzM3OSIsImlhdCI6MTY5MTkyOTU2MSwiZXhwIjoxNjkyNTM0MzYxfQ.f2fP7AQODfPTxRJKKCMiOvIbyqSBKLupyY1DNOlpvOY"

        authViewModel.getUserInfo(token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}