package com.hosco.nextcrm.callcenter.ui.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ItemContactListBinding
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.item_contact_list.view.*

class ContactAdapter() :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    var responseList: ArrayList<ContactResponse> = ArrayList()
    var responseListFilter: ArrayList<ContactResponse> = ArrayList()

    lateinit var itemClick: itemClickListener

    fun addMore(data: ArrayList<ContactResponse>) {
        val countCurrent = itemCount
        responseList.addAll(data)
        responseListFilter.addAll(data)
        notifyItemRangeInserted(countCurrent, itemCount)
    }

    fun clearData() {
        this.responseList.clear()
        responseListFilter.clear()
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<ContactResponse>) {
        responseList.clear()
        responseList.addAll(data)
        responseListFilter = responseList
        notifyDataSetChanged()
    }

    @JvmName("setItemClick1")
    fun setItemClick(itemClickListener: itemClickListener) {
        this.itemClick = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fillData(itemClick, responseListFilter[position])
    }

    override fun getItemCount(): Int {
        return responseListFilter.size
    }

    class ViewHolder constructor(val databinding: ViewDataBinding) :
        RecyclerView.ViewHolder(databinding.root) {
        fun fillData(itemClickListener: itemClickListener, itemData: ContactResponse) {
            databinding.setVariable(BR.contactModel, itemData)
            databinding.executePendingBindings()
            PushDownAnim.setPushDownAnimTo(databinding.root.layoutItemContact)
                .setOnClickListener { itemClickListener.onClick(itemData) }
        }
    }

    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    responseListFilter = responseList
                } else {
                    val filteredList: ArrayList<ContactResponse> = ArrayList()
                    for (row in responseList) {
                        if (!row.name.isNullOrEmpty() && row.name.lowercase()
                                .contains(charString.lowercase()) || !row.mobile
                                .isNullOrEmpty() && row.mobile
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
                responseListFilter = filterResults.values as ArrayList<ContactResponse>
                notifyDataSetChanged()
            }
        }
    }
}

interface itemClickListener {
    fun onClick(contactResponse: ContactResponse)
}

@BindingAdapter("phoneNumberValue")
fun setPhoneNumberText(textView: TextView, phoneNumber: String?) {
    if (phoneNumber.isNullOrEmpty()) {
        textView.text =
            "${textView.context.getString(R.string.txt_contact_phone_number)}: ${textView.context.getString(R.string.txt_no_identify)}"
    } else {
        textView.text =
            "${textView.context.getString(R.string.txt_contact_phone_number)}: ${phoneNumber}"
    }
}