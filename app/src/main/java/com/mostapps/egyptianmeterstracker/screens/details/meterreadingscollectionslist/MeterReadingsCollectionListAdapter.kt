package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist

import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseRecyclerViewAdapter
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem


class MeterReadingsCollectionListAdapter(callBack: (selectedMeter: MeterReadingsCollectionListItem, itemPosition: Int) -> Unit) :
    BaseRecyclerViewAdapter<MeterReadingsCollectionListItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.meter_readings_collection_list_item
}