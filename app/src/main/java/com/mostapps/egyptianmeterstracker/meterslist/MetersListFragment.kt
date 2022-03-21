package com.mostapps.egyptianmeterstracker.meterslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.databinding.FragmentMetersListBinding
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class MetersListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val viewModel: MetersListViewModel by viewModel()

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
        binding.viewModel = viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { viewModel.loadMeters() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        viewModel.loadMeters()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        /*_viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )*/
    }

    private fun setupRecyclerView() {
        /*val adapter = RemindersListAdapter {

        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.home_menu, menu)
    }

}
