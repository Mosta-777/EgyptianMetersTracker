package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MetersDetailsActivity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.authentication.AuthenticationActivity
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.databinding.FragmentMetersListBinding
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import com.mostapps.egyptianmeterstracker.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel


class MetersListFragment : BaseFragment() {


    companion object {
        const val SELECTED_METER_KEY = "selectedMeterKey"
    }


    override val _viewModel: MetersListViewModel by viewModel()

    private lateinit var binding: FragmentMetersListBinding
    var isAllFabsVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_meters_list, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadMeters() }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addMeterOrMeterReadingFab.setOnClickListener {
            navigateToAddMeterScreen()
        }

        _viewModel.showResolveConflictDialogue.observe(viewLifecycleOwner) { noOfConflicts ->
            if (noOfConflicts != null && noOfConflicts != 0) {
                with(AlertDialog.Builder(requireContext()))
                {
                    setTitle(getString(R.string.title_conflict_dialogue))
                    setMessage(getString(R.string.label_conflict_dialogue, noOfConflicts))
                    setPositiveButton(
                        getString(R.string.label_keep_local_data)
                    ) { _, _ ->


                        if (_viewModel.authenticatedUser?.run {
                                _viewModel.syncConflictedData(
                                    numberOfConflicts = noOfConflicts,
                                    keepLocal = true,
                                    uid = uid
                                )
                            } == null) {
                            //TODO ask them to login to continue syncing the data

                        }


                    }
                    setNegativeButton(
                        getString(R.string.label_keep_remote_data)
                    ) { _, _ ->
                        if (_viewModel.authenticatedUser?.run {
                                _viewModel.syncConflictedData(
                                    numberOfConflicts = noOfConflicts,
                                    keepLocal = false,
                                    uid = uid
                                )
                            } == null) {
                            //TODO ask them to login to continue syncing the data

                        }
                    }
                    show()
                }

            }
        }

        binding.addMeterFab.visibility = View.GONE
        binding.addMeterReadingFab.visibility = View.GONE
        binding.addMeterFabText.visibility = View.GONE
        binding.addMeterReadingText.visibility = View.GONE




        binding.addMeterOrMeterReadingFab.setOnClickListener {
            if (!isAllFabsVisible) {
                binding.addMeterOrMeterReadingFab.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_forward
                    )
                )
                binding.addMeterFab.show()
                binding.addMeterReadingFab.show()
                binding.addMeterFabText.visibility = View.VISIBLE
                binding.addMeterReadingText.visibility = View.VISIBLE
                isAllFabsVisible = true
            } else {
                binding.addMeterOrMeterReadingFab.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_backward
                    )
                )
                binding.addMeterFab.hide()
                binding.addMeterReadingFab.hide()
                binding.addMeterFabText.visibility = View.GONE
                binding.addMeterReadingText.visibility = View.GONE
                isAllFabsVisible = false
            }
        }

        binding.addMeterReadingFab.setOnClickListener {
            navigateToAddMeterReadingScreen()
        }

        binding.addMeterFab.setOnClickListener {
            navigateToAddMeterScreen()
        }


    }

    private fun navigateToAddMeterReadingScreen() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                MetersListFragmentDirections.actionMetersListFragmentToAddMeterReadingFragment()
            )
        )
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadMeters()
    }

    private fun navigateToAddMeterScreen() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                MetersListFragmentDirections.actionMetersListFragmentToCreateMeterFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = MetersListAdapter { _: MeterDataListItem, position: Int ->
            activity?.let {
                val intent = Intent(it, MetersDetailsActivity::class.java)
                intent.putExtra(SELECTED_METER_KEY, _viewModel.metersList[position])
                it.startActivity(intent)
            }
        }
        binding.metersRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                    val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    requireActivity().startActivity(intent)
                }
            }
            R.id.sync -> {


                if (_viewModel.authenticatedUser?.run {
                        _viewModel.startDataSyncing(uid)
                    } == null) {
                    //TODO ask them to login to continue syncing the data

                }

            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

}
