package com.mostapps.egyptianmeterstracker.screens.details.meterdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MetersDetailsActivity : AppCompatActivity() {


    lateinit var navController: NavController
    private val viewModel: MeterDetailsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        navController = this.findNavController(R.id.nav_host_fragment)


        //Get meter data from the previous screen
        viewModel.setSelectedMeter(intent.getSerializableExtra(MetersListFragment.SELECTED_METER_KEY) as DatabaseMeter)

        NavigationUI.setupActionBarWithNavController(this, navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }


}