package com.hosco.nextcrm.callcenter.ui.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.databinding.ItemDeviceContactListBinding
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.model.response.InternalResponse
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.item_device_contact_list.view.*
import kotlinx.android.synthetic.main.item_internal_list.view.*
import org.linphone.contact.Contact


class DeviceContactAdapter : RecyclerView.Adapter<DeviceContactAdapter.ViewHolder>() {

    var responseList: ArrayList<DeviceContact> = ArrayList()
    var responseListFilter: ArrayList<DeviceContact> = ArrayList()

    lateinit var itemClickDevice: ItemClickDevice

    fun setListener(itemClickDevice: ItemClickDevice) {
        this.itemClickDevice = itemClickDevice
    }

    fun addMore(data: ArrayList<DeviceContact>) {
        val countCurrent = itemCount
        responseList.addAll(data)
        notifyItemRangeInserted(countCurrent, itemCount)
    }

    fun clearData() {
        this.responseList.clear()
        this.responseListFilter.clear()
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<DeviceContact>) {
        responseList.clear()
        responseList.addAll(data)
        responseListFilter.clear()
        responseListFilter.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemDeviceContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fillData(itemClickDevice, responseListFilter[position])
    }

    override fun getItemCount(): Int {
        return responseListFilter.size
    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    responseListFilter = responseList
                } else {
                    val filteredList: ArrayList<DeviceContact> = ArrayList()
                    for (row in responseList) {
                        if (row.name.toString().lowercase()
                                .contains(charString.lowercase()) || row.phone.toString()
                                .lowercase().contains(charString.lowercase())
                        )
                            filteredList.add(row)
                    }
                    responseListFilter = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = responseListFilter
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                responseListFilter = filterResults.values as ArrayList<DeviceContact>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder constructor(val databinding: ViewDataBinding) :
        RecyclerView.ViewHolder(databinding.root) {
        fun fillData(itemClickDevice: ItemClickDevice, itemData: DeviceContact) {
            databinding.setVariable(BR.deviceContactModel, itemData)
            databinding.executePendingBindings()
            PushDownAnim.setPushDownAnimTo(databinding.root.layoutItemDevice)
                .setOnClickListener { itemClickDevice.onClick(itemData) }
        }
    }
}

interface ItemClickDevice {
    fun onClick(deviceContact: DeviceContact)
}