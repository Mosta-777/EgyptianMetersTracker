package com.mostapps.egyptianmeterstracker.screens.home.createmeter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentAddMeterBinding
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateMeterFragment() : BaseFragment() {
    override val _viewModel: CreateMeterViewModel by viewModel()
    private lateinit var _binding: FragmentAddMeterBinding
    var items: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_meter, container, false
            )

        setupSpinners()
        _binding.viewModel = _viewModel


        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.create_new_meter))
        return _binding.root
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