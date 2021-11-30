package com.hosco.nextcrm.callcenter.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ItemHistoryListBinding
import com.hosco.nextcrm.callcenter.linphone.utils.TimeCrmUtils
import com.hosco.nextcrm.callcenter.model.response.HistoryResponse
import com.hosco.nextcrm.callcenter.utils.GenColorBackground

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var responseList: ArrayList<HistoryResponse> = ArrayList()

    fun addMore(data: ArrayList<HistoryResponse>) {
        val countCurrent = itemCount
        responseList.addAll(data)
        notifyItemRangeInserted(countCurrent, itemCount)
    }

    fun clearData() {
        this.responseList.clear()
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<HistoryResponse>) {
        responseList.clear()
        responseList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fillData(responseList[position])
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    class ViewHolder constructor(val databinding: ViewDataBinding) :
        RecyclerView.ViewHolder(databinding.root) {
        fun fillData(itemData: HistoryResponse) {
            databinding.setVariable(BR.historyModel, itemData)
            databinding.executePendingBindings()
        }
    }
}

@BindingAdapter("textDuration")
fun loadTextDuration(view: TextView, seconds: Int) {
    seconds.let {
        val sec = seconds % 60
        val minutes = seconds % 3600 / 60
        val hours = seconds % 86400 / 3600
        val days = seconds / 86400
        if (days > 0)
            view.text =
                "${days} ".plus(view.context.getString(R.string.txt_day)).plus(" ${hours} ")
                    .plus(view.context.getString(R.string.txt_hour)).plus(" ${minutes} ")
                    .plus(view.context.getString(R.string.txt_minute)).plus(" ${sec} ")
                    .plus(view.context.getString(R.string.txt_second))
        else if (hours > 0)
            view.text =
                "${hours} ".plus(view.context.getString(R.string.txt_hour)).plus(" ${minutes} ")
                    .plus(view.context.getString(R.string.txt_minute)).plus(" ${sec} ")
                    .plus(view.context.getString(R.string.txt_second))
        else if (minutes > 0)
            view.text = "${minutes} "
                .plus(view.context.getString(R.string.txt_minute)).plus(" ${sec} ")
                .plus(view.context.getString(R.string.txt_second))
        else
            view.text = " ${sec} "
                .plus(view.context.getString(R.string.txt_second))
    }
}