package com.hosco.nextcrm.callcenter.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ItemNoteListBindingImpl
import com.hosco.nextcrm.callcenter.model.response.NoteResponse
import com.hosco.nextcrm.callcenter.utils.GenColorBackground

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var responseList: ArrayList<NoteResponse> = ArrayList()

    fun addMore(data: ArrayList<NoteResponse>) {
        val countCurrent = itemCount
        responseList.addAll(data)
        notifyItemRangeInserted(countCurrent, itemCount)
    }

    fun addMore(data: NoteResponse) {
        responseList.add(0, data)
        notifyDataSetChanged()
    }

    fun clearData() {
        this.responseList.clear()
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<NoteResponse>) {
        responseList.clear()
        responseList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val dataBinding =
            ItemNoteListBindingImpl.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(dataBinding)
    }


    override fun getItemCount(): Int {
        return responseList.size
    }

    class NoteViewHolder constructor(val databinding: ViewDataBinding) :
        RecyclerView.ViewHolder(databinding.root) {
        fun fillData(itemData: NoteResponse) {
            databinding.setVariable(BR.noteModel, itemData)
            databinding.executePendingBindings()
        }

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        (holder as NoteViewHolder).fillData(responseList[position])

    }

}

//@BindingAdapter("imageUrl", "error")
//fun loadImage(view: ImageView, url: String, error: Drawable) {
//    Picasso.get().load(url).error(error).into(view)
//}

@BindingAdapter("textFirst")
fun loadText(view: TextView, text: String?) {
    if (text != null && !text.isEmpty()) {
        if (text.length >= 2) {
            if (text.contains(" "))
                view.text = text.substring(0, 1).plus(text.split(" ")[1].substring(0, 1))
            else
                view.text =
                    text.substring(0, 1).plus(text.substring(text.length - 2, text.length - 1))
        } else {
            view.text = text.substring(0, 1)
        }
    } else {
        view.text = "#"
    }
    view.background = GenColorBackground.getBackground()
}

@BindingAdapter(value = ["firstNameEmployee", "lastNameEmployee"])
fun loadFullNameWithEmployee(view: TextView, firstName: String?, lastName: String?) {
    var fullName = view.context.resources.getString(R.string.txt_employee).plus(" ")
    if (firstName != null && lastName != null) {
        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            fullName = fullName.plus(firstName.plus(" ").plus(lastName))
            if (fullName.contains("  "))
                fullName = fullName.replace("  ", " ")
        } else if (firstName.isNotEmpty() && lastName.isEmpty()) {
            fullName = fullName.plus(firstName)
        } else if (firstName.isEmpty() && lastName.isNotEmpty()) {
            fullName = fullName.plus(lastName)
        }
    } else if (firstName != null && lastName == null) {
        if (firstName.isNotEmpty())
            fullName = fullName.plus(firstName)
    } else if (firstName == null && lastName != null) {
        if (lastName.isNotEmpty())
            fullName = fullName.plus(lastName)
    } else fullName =
        fullName.plus(view.context.resources.getString(R.string.txt_not_have_care_employee1))
    view.text = fullName
}

@BindingAdapter(value = ["firstName", "lastName"])
fun loadFullName(view: TextView, firstName: String?, lastName: String?) {
    var fullName = view.context.resources.getString(R.string.txt_employee).plus(" ")
    if (firstName != null && lastName != null) {
        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            fullName = fullName.plus(firstName.plus(" ").plus(lastName))
            if (fullName.contains("  "))
                fullName = fullName.replace("  ", " ")
        } else if (firstName.isNotEmpty() && lastName.isEmpty()) {
            fullName = fullName.plus(firstName)
        } else if (firstName.isEmpty() && lastName.isNotEmpty()) {
            fullName = fullName.plus(lastName)
        }
    } else if (firstName != null && lastName == null) {
        if (firstName.isNotEmpty())
            fullName = fullName.plus(firstName)
    } else if (firstName == null && lastName != null) {
        if (lastName.isNotEmpty())
            fullName = fullName.plus(lastName)
    } else fullName =
        fullName.plus(view.context.resources.getString(R.string.txt_not_have_care_employee))
    view.text = fullName
}
