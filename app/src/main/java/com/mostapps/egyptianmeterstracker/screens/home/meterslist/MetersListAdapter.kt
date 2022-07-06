package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseRecyclerViewAdapter
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem

//Use data binding to show the reminder on the item
class MetersListAdapter(callBack: (selectedMeter: MeterDataListItem, itemPosition: Int) -> Unit) :
    BaseRecyclerViewAdapter<MeterDataListItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.meter_list_item
}