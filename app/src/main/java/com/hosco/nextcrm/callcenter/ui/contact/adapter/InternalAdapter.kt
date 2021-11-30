package com.hosco.nextcrm.callcenter.ui.contact.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ItemInternalListBinding
import com.hosco.nextcrm.callcenter.model.response.InternalResponse
import com.hosco.nextcrm.callcenter.utils.GenColorBackground
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.item_internal_list.view.*

class InternalAdapter : RecyclerView.Adapter<InternalAdapter.ViewHolder>() {

    var responseList: ArrayList<InternalResponse> = ArrayList()
    var responseListFilter: ArrayList<InternalResponse> = ArrayList()

    lateinit var itemClickInternal: ItemClickInternal

    fun setInternalListener(itemClickInternal: ItemClickInternal) {
        this.itemClickInternal = itemClickInternal
    }

    fun addMore(data: ArrayList<InternalResponse>) {
        val countCurrent = itemCount
        responseList.addAll(data)
        responseListFilter.addAll(data)
        notifyItemRangeInserted(countCurrent, itemCount)
    }

    fun clearData() {
        this.responseList.clear()
        this.responseListFilter.clear()
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<InternalResponse>) {
        responseList.clear()
        responseList.addAll(data)
        this.responseListFilter = responseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemInternalListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fillData(itemClickInternal, responseListFilter[position])
    }

    override fun getItemCount(): Int {
        return responseListFilter.size
    }

    class ViewHolder constructor(val databinding: ViewDataBinding) :
        RecyclerView.ViewHolder(databinding.root) {
        fun fillData(itemClickInternal: ItemClickInternal, itemData: InternalResponse) {
            databinding.setVariable(BR.internalModel, itemData)
            databinding.executePendingBindings()
            PushDownAnim.setPushDownAnimTo(databinding.root.layoutItemInternalContact)
                .setOnClickListener { itemClickInternal.onClick(itemData) }
        }
    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    responseListFilter = responseList
                } else {
                    val filteredList: ArrayList<InternalResponse> = ArrayList()
                    for (row in responseList) {
                        row.let {
                            if (!it.firstname.isNullOrEmpty() && it.firstname.lowercase()
                                    .contains(charString.lowercase()) ||
                                !it.lastname.isNullOrEmpty() && it.lastname.lowercase()
                                    .contains(charString.lowercase()) ||
                                !it.crmExtensionId.isNullOrEmpty() && it.crmExtensionId.lowercase()
                                    .contains(charString.lowercase())
                            )
                                filteredList.add(row)
                        }
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
                responseListFilter = filterResults.values as ArrayList<InternalResponse>
                notifyDataSetChanged()
            }
        }
    }

}

interface ItemClickInternal {
    fun onClick(internalResponse: InternalResponse)
}

@BindingAdapter("numberPhoneInternal")
fun loadTextNumberPhone(view: TextView, text: String?) {
    var phone = ": "
    if (text != null && text.isNotEmpty())
        phone = phone.plus(text)
    else phone = phone.plus(view.context.resources.getString(R.string.txt_no_identify))
    view.text = phone
}