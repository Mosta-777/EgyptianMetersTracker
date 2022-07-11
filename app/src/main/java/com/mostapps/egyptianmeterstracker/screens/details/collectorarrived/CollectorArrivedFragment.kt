package com.mostapps.egyptianmeterstracker.screens.details.collectorarrived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentCollectorArrivedBinding
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MeterDetailsViewModel
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollectorArrivedFragment : BaseFragment() {


    override val _viewModel: CollectorArrivedViewModel by viewModel()
    private val parentViewModel: MeterDetailsViewModel by sharedViewModel()


    private lateinit var binding: FragmentCollectorArrivedBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_collector_arrived, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        parentViewModel.meter.observe(viewLifecycleOwner) { meter ->
            if (meter != null) {
                _viewModel.setSelectedMeter(meter)

            }
        }


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this



        binding.buttonStartNewMeterReadingsCollection.setOnClickListener {
            _viewModel.closeCurrentCollectionAndStartNewOne()
        }


    }

}