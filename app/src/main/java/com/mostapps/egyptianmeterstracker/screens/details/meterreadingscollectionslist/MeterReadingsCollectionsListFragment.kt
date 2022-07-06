package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentMeterReadingCollectionsListBinding
import com.mostapps.egyptianmeterstracker.databinding.FragmentMetersListBinding
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MeterDetailsViewModel
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MetersDetailsActivity
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListAdapter
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListFragment
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import com.mostapps.egyptianmeterstracker.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MeterReadingsCollectionsListFragment : BaseFragment() {


    override val _viewModel: MeterReadingsCollectionsListViewModel by viewModel()
    private val parentViewModel: MeterDetailsViewModel by sharedViewModel()


    private lateinit var binding: FragmentMeterReadingCollectionsListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_meter_reading_collections_list, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        parentViewModel.meter.observe(viewLifecycleOwner, Observer { meter ->
            _viewModel.setSelectedMeter(meter)
        })


        return binding.root

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val adapter =
            MeterReadingsCollectionListAdapter { _: MeterReadingsCollectionListItem, position: Int ->
                //TODO handle on meter collection list item clicked
            }
        binding.meterCollectionsRecyclerView.setup(adapter)
    }



}