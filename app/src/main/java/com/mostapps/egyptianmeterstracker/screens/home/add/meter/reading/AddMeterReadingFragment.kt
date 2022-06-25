package com.mostapps.egyptianmeterstracker.screens.home.add.meter.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.databinding.FragmentAddMeterReadingBinding
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddMeterReadingFragment : BaseFragment() {


    override val _viewModel: AddMeterReadingViewModel by viewModel()


    private lateinit var binding: FragmentAddMeterReadingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_meter_reading, container, false
            )

        binding.viewModel = _viewModel

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.add_new_meter_reading))
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.buttonAddMeterReading.setOnClickListener {
            _viewModel.validateAndAddMeterReading();
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            //something to do
            _viewModel.navigationCommand.postValue(NavigationCommand.Back)
            true
        } else super.onOptionsItemSelected(item)
    }








    override fun onResume() {
        super.onResume()
        _viewModel.loadMeters()
    }


}