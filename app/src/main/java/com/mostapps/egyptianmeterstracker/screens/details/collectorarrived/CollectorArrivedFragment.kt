package com.mostapps.egyptianmeterstracker.screens.details.collectorarrived

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentCollectorArrivedBinding
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MeterDetailsViewModel
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class CollectorArrivedFragment : BaseFragment() {


    override val _viewModel: CollectorArrivedViewModel by viewModel()
    private val parentViewModel: MeterDetailsViewModel by sharedViewModel()


    private lateinit var _binding: FragmentCollectorArrivedBinding
    private var cal = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_collector_arrived, container, false
            )
        _binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.collector_arrived))


        parentViewModel.meter.observe(viewLifecycleOwner) { meter ->
            if (meter != null) {
                _viewModel.setSelectedMeter(meter)

            }
        }


        _binding.editTextFirstReadingDate.setOnClickListener {
            val dateDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateInView()
                },
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            //Collector arrival date should be at maximum today
            //and not before the date of the last recorded meter reading
            dateDialog.datePicker.maxDate = DateUtils.now().time
            dateDialog.datePicker.minDate =
                (_viewModel.lastMeterReading?.readingDate?.time) ?: DateUtils.now().time
            dateDialog.show()
        }


        return _binding.root

    }


    private fun updateDateInView() {
        _binding.editTextFirstReadingDate.setText(
            DateUtils.formatDate(
                cal.time,
                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
            )
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.lifecycleOwner = this



        _binding.buttonStartNewMeterReadingsCollection.setOnClickListener {
            _viewModel.closeCurrentCollectionAndStartNewOne()
        }


    }

}