package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.authentication.AuthenticationActivity
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.databinding.FragmentMetersListBinding
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import com.mostapps.egyptianmeterstracker.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class MetersListFragment : BaseFragment() {

    override val _viewModel: MetersListViewModel by viewModel()

    private lateinit var binding: FragmentMetersListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding.addReminderFAB.setOnClickListener {
            navigateToAddMeter()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadMeters()
    }

    private fun navigateToAddMeter() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                MetersListFragmentDirections.actionMetersListFragmentToCreateMeterFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = MetersListAdapter {}

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
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
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.home_menu, menu)
    }

}
