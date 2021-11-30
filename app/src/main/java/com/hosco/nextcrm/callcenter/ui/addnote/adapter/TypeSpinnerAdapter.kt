package com.hosco.nextcrm.callcenter.ui.addnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse

class TypeSpinnerAdapter(val list: List<TypeResponse>) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position].name
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_spinner_add_note, parent, false)
            vh = ItemViewHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemViewHolder
        }
        vh.label.text = list.get(position).name

        return view
    }

    class ItemViewHolder(view: View) {
        val label: TextView

        init {
            label = view?.findViewById(R.id.tvItemSpinner) as TextView
        }
    }
}
