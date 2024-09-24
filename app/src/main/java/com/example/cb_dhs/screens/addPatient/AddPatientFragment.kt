package com.example.cb_dhs.screens.addPatient


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.cb_dhs.MainActivity
import com.example.cb_dhs.R

import com.google.android.fhir.datacapture.QuestionnaireFragment
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse


/** A fragment class to show patient registration screen. */
class AddPatientFragment : Fragment(R.layout.add_patient_fragment) {

    private val viewModel: AddPatientViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBar()
        setHasOptionsMenu(true)
        updateArguments()
        if (savedInstanceState == null) {
            addQuestionnaireFragment()
        }
        observePatientSaveAction()
        (activity as MainActivity).setDrawerEnabled(false)

        /** Use the provided cancel|submit buttons from the sdc library */
        childFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, _ ->
            onSubmitAction()
        }
        childFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.CANCEL_REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, _ ->
            NavHostFragment.findNavController(this).navigateUp()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavHostFragment.findNavController(this).navigateUp()
                true
            }

            else -> false
        }
    }

    private fun setUpActionBar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = requireContext().getString(R.string.add_patient)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun updateArguments() {
        requireArguments()
            .putString(QUESTIONNAIRE_FILE_PATH_KEY, "new-patient-registration-paginated.json")
    }

    private fun addQuestionnaireFragment() {
        childFragmentManager.commit {
            replace(
                R.id.add_patient_container,
                QuestionnaireFragment.builder().setQuestionnaire(viewModel.questionnaireJson)
                    .showReviewPageBeforeSubmit(true)
                    .build(),
                QUESTIONNAIRE_FRAGMENT_TAG,
            )
        }
    }

    private fun onSubmitAction() {
        lifecycleScope.launch {
            val questionnaireFragment =
                childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
            savePatient(questionnaireFragment.getQuestionnaireResponse())
        }
    }

    private fun savePatient(questionnaireResponse: QuestionnaireResponse) {
        viewModel.savePatient(questionnaireResponse)
    }

    private fun observePatientSaveAction() {
        viewModel.isPatientSaved.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(requireContext(), "Inputs are missing.", Toast.LENGTH_SHORT).show()
                return@observe
            }
            Toast.makeText(requireContext(), "Patient is saved.", Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(this).navigateUp()
        }
    }

    companion object {
        const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
        const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    }
}
