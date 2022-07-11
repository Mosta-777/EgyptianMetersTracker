package com.mostapps.egyptianmeterstracker.screens.home.createmeter


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentAddMeterBinding
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CreateMeterFragment() : BaseFragment() {
    override val _viewModel: CreateMeterViewModel by viewModel()
    private lateinit var _binding: FragmentAddMeterBinding
    private var cal = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_meter, container, false
            )

        setupSpinners()

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

            //Collector arrival date should be at maximum today and not 31 days before today

            dateDialog.datePicker.maxDate = DateUtils.now().time
            dateDialog.datePicker.minDate = DateUtils.now().time - TimeUnit.DAYS.toMillis(31)
            dateDialog.show()
        }



        _binding.viewModel = _viewModel


        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.create_new_meter))
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

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meter_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerMeterType.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.electricity_meter_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerMeterSubType.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.lifecycleOwner = this


        _binding.buttonCreateNewMeter.setOnClickListener {
            _viewModel.validateAndCreateMeter()
        }


    }


}