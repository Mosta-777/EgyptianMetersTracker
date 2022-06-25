package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
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
        val adapter = MetersListAdapter {}
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

                val authenticatedUser = _viewModel.authenticatedUser

                if (authenticatedUser != null) {
                    _viewModel.startDataSyncing(authenticatedUser.uid)
                } else {
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
