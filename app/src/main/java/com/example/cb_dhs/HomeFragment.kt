package com.example.cb_dhs

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


@Suppress("DEPRECATION")
class HomeFragment : Fragment(R.layout.fragment_home) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = resources.getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)
        (activity as MainActivity).setDrawerEnabled(true)
        setOnClicks()
    }
    private fun setOnClicks() {
        requireView().findViewById<CardView>(R.id.item_new_patient).setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPatientFragment())
        }
        requireView().findViewById<CardView>(R.id.item_patient_list).setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
        }
        requireView().findViewById<CardView>(R.id.item_search).setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (requireActivity() as MainActivity).openNavigationDrawer()
                true
            }

            else -> false
        }
    }


}
