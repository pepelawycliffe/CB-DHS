package com.example.cb_dhs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.cb_dhs.databinding.FragmentPatientListBinding
import com.example.cb_dhs.extensions.launchAndRepeatStarted
import com.example.cb_dhs.fhir.FhirApplication
import com.example.cb_dhs.screens.patientDetails.PatientListFragmentDirections
import com.example.cb_dhs.screens.patientDetails.PatientListViewModel.PatientListViewModelFactory
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.google.android.fhir.sync.LastSyncJobStatus
import com.google.android.fhir.sync.PeriodicSyncJobStatus
import com.google.android.fhir.sync.SyncJobStatus
import timber.log.Timber
import kotlin.math.roundToInt

class ReferralListFragment : Fragment() {
    private lateinit var fhirEngine: FhirEngine
    private lateinit var referralListViewModel: ReferralListViewModel
    private lateinit var searchView: SearchView
    private lateinit var topBanner: LinearLayout
    private lateinit var syncStatus: TextView
    private lateinit var syncPercent: TextView
    private lateinit var syncProgress: ProgressBar
    private var _binding: FragmentPatientListBinding? = null
    private val binding
        get() = _binding!!

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "referral list"
            setDisplayHomeAsUpEnabled(true)
        }
        fhirEngine = FhirApplication.fhirEngine(requireContext())
        referralListViewModel =
            ViewModelProvider(
                this,
                PatientListViewModelFactory(requireActivity().application, fhirEngine),
            )
                .get(ReferralListViewModel::class.java)
        val recyclerView: RecyclerView = binding.patientListContainer.patientList
        val adapter = ReferralItemRecyclerViewAdapter(this::onReferralItemClicked)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                setDrawable(ColorDrawable(Color.LTGRAY))
            },
        )

        referralListViewModel.liveSearchedReferrals.observe(viewLifecycleOwner) {
            Timber.d("Submitting ${it.count()} patient records")
            adapter.submitList(it)
        }

        referralListViewModel.patientCount.observe(viewLifecycleOwner) {
            binding.patientListContainer.patientCount.text = "$it Patient(s)"
        }

        searchView = binding.search
        topBanner = binding.syncStatusContainer.linearLayoutSyncStatus
        topBanner.visibility = View.GONE
        syncStatus = binding.syncStatusContainer.tvSyncingStatus
        syncPercent = binding.syncStatusContainer.tvSyncingPercent
        syncProgress = binding.syncStatusContainer.progressSyncing
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    referralListViewModel.searchPatientsByName(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    referralListViewModel.searchPatientsByName(query)
                    return true
                }
            },
        )
        searchView.setOnQueryTextFocusChangeListener { view, focused ->
            if (!focused) {
                // hide soft keyboard
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (searchView.query.isNotEmpty()) {
                            searchView.setQuery("", true)
                        } else {
                            isEnabled = false
                            activity?.onBackPressed()
                        }
                    }
                },
            )

        binding.apply {
            addPatient.setOnClickListener { onAddPatientClick() }
            addPatient.setColorFilter(Color.WHITE)
        }
        setHasOptionsMenu(true)
        (activity as MainActivity).setDrawerEnabled(false)
        launchAndRepeatStarted(
            { mainActivityViewModel.pollState.collect(::currentSyncJobStatus) },
            { mainActivityViewModel.pollPeriodicSyncJobStatus.collect(::periodicSyncJobStatus) },
        )
    }

    private fun currentSyncJobStatus(currentSyncJobStatus: CurrentSyncJobStatus) {
        when (currentSyncJobStatus) {
            is CurrentSyncJobStatus.Running -> {
                Timber.i(
                    "Sync: ${currentSyncJobStatus::class.java.simpleName} with data ${currentSyncJobStatus.inProgressSyncJob}",
                )
                fadeInTopBanner(currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Succeeded -> {
                Timber.i(
                    "Sync: ${currentSyncJobStatus::class.java.simpleName} at ${currentSyncJobStatus.timestamp}",
                )
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                mainActivityViewModel.updateLastSyncTimestamp(currentSyncJobStatus.timestamp)
                fadeOutTopBanner(currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Failed -> {
                Timber.i(
                    "Sync: ${currentSyncJobStatus::class.java.simpleName} at ${currentSyncJobStatus.timestamp}",
                )
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                mainActivityViewModel.updateLastSyncTimestamp(currentSyncJobStatus.timestamp)
                fadeOutTopBanner(currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Enqueued -> {
                Timber.i("Sync: Enqueued")
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                fadeOutTopBanner(currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Cancelled -> {
                Timber.i("Sync: Cancelled")
                fadeOutTopBanner(currentSyncJobStatus)
            }
//      is CurrentSyncJobStatus.Blocked -> {
//        Timber.i("Sync: Blocked")
//        fadeOutTopBanner(currentSyncJobStatus)
//      }
        }
    }

    private fun periodicSyncJobStatus(periodicSyncJobStatus: PeriodicSyncJobStatus) {
        when (periodicSyncJobStatus.currentSyncJobStatus) {
            is CurrentSyncJobStatus.Running -> {
                fadeInTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Succeeded -> {
                val lastSyncTimestamp =
                    (periodicSyncJobStatus.currentSyncJobStatus as CurrentSyncJobStatus.Succeeded).timestamp
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                mainActivityViewModel.updateLastSyncTimestamp(lastSyncTimestamp)
                fadeOutTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Failed -> {
                val lastSyncTimestamp =
                    (periodicSyncJobStatus.currentSyncJobStatus as CurrentSyncJobStatus.Failed).timestamp
                Timber.i(
                    "Sync: ${periodicSyncJobStatus.currentSyncJobStatus::class.java.simpleName} at $lastSyncTimestamp}",
                )
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                mainActivityViewModel.updateLastSyncTimestamp(lastSyncTimestamp)
                fadeOutTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Enqueued -> {
                Timber.i("Sync: Enqueued")
                referralListViewModel.searchPatientsByName(searchView.query.toString().trim())
                fadeOutTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
            }

            is CurrentSyncJobStatus.Cancelled -> {
                Timber.i("Sync: Cancelled")
                fadeOutTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
            }
//      is CurrentSyncJobStatus.Blocked -> {
//        Timber.i("Sync: Blocked")
//        fadeOutTopBanner(periodicSyncJobStatus.currentSyncJobStatus)
//      }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun onReferralItemClicked(referralItem: ReferralListViewModel.ReferralItem) {
        findNavController()
            .navigate(PatientListFragmentDirections.navigateToProductDetail(referralItem.resourceId))
    }

    private fun onAddPatientClick() {
        findNavController()
            .navigate(PatientListFragmentDirections.actionPatientListToAddPatientFragment())
    }

    private fun fadeInTopBanner(state: CurrentSyncJobStatus) {
        if (topBanner.visibility != View.VISIBLE) {
            syncStatus.text = resources.getString(R.string.syncing).uppercase()
            syncPercent.text = ""
            syncProgress.progress = 0
            syncProgress.visibility = View.VISIBLE
            topBanner.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(topBanner.context, R.anim.fade_in)
            topBanner.startAnimation(animation)
        } else if (
            state is CurrentSyncJobStatus.Running && state.inProgressSyncJob is SyncJobStatus.InProgress
        ) {
            val inProgressState = state.inProgressSyncJob as? SyncJobStatus.InProgress
            val progress =
                inProgressState
                    ?.let { it.completed.toDouble().div(it.total) }
                    ?.let { if (it.isNaN()) 0.0 else it }
                    ?.times(100)
                    ?.roundToInt()
            "$progress% ${inProgressState?.syncOperation?.name?.lowercase()}ed"
                .also { syncPercent.text = it }
            syncProgress.progress = progress ?: 0
        }
    }

    private fun fadeOutTopBanner(state: CurrentSyncJobStatus) {
        fadeOutTopBanner(state::class.java.simpleName.uppercase())
    }

    private fun fadeOutTopBanner(state: LastSyncJobStatus) {
        fadeOutTopBanner(state::class.java.simpleName.uppercase())
    }

    private fun fadeOutTopBanner(statusText: String) {
        syncPercent.text = ""
        syncProgress.visibility = View.GONE
        if (topBanner.visibility == View.VISIBLE) {
            "${resources.getString(R.string.sync).uppercase()} $statusText".also {
                syncStatus.text = it
            }

            val animation = AnimationUtils.loadAnimation(topBanner.context, R.anim.fade_out)
            topBanner.startAnimation(animation)
            Handler(Looper.getMainLooper()).postDelayed({ topBanner.visibility = View.GONE }, 2000)
        }
    }
}
