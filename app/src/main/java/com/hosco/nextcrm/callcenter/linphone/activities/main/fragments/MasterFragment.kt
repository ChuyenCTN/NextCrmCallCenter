package org.linphone.activities.main.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import org.linphone.activities.main.adapters.SelectionListAdapter
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.activities.main.viewmodels.ListTopBarViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.AppUtils
import org.linphone.utils.DialogUtils

/**
 * This fragment can be inherited by all fragments that will display a list
 * where items can be selected for removal through the ListTopBarFragment
 */
abstract class MasterFragment<T : ViewDataBinding, U : SelectionListAdapter<*, *>> : SecureFragment<T>() {
    protected var _adapter: U? = null
    protected val adapter: U
        get() {
            if (_adapter == null) {
                Log.e("[Master Fragment] Attempting to get a null adapter!")
            }
            return _adapter!!
        }

    protected lateinit var listSelectionViewModel: ListTopBarViewModel
    protected open val dialogConfirmationMessageBeforeRemoval: Int = R.plurals.dialog_default_delete

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List selection
        listSelectionViewModel = ViewModelProvider(this).get(ListTopBarViewModel::class.java)

        listSelectionViewModel.isEditionEnabled.observe(
            viewLifecycleOwner,
            {
                if (!it) listSelectionViewModel.onUnSelectAll()
            }
        )

        listSelectionViewModel.selectAllEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    listSelectionViewModel.onSelectAll(getItemCount() - 1)
                }
            }
        )

        listSelectionViewModel.unSelectAllEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    listSelectionViewModel.onUnSelectAll()
                }
            }
        )

        listSelectionViewModel.deleteSelectionEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    val confirmationDialog = AppUtils.getStringWithPlural(dialogConfirmationMessageBeforeRemoval, listSelectionViewModel.selectedItems.value.orEmpty().size)
                    val viewModel = DialogViewModel(confirmationDialog)
                    val dialog: Dialog = DialogUtils.getDialog(requireContext(), viewModel)

                    viewModel.showCancelButton {
                        dialog.dismiss()
                        listSelectionViewModel.isEditionEnabled.value = false
                    }

                    viewModel.showDeleteButton(
                        {
                            delete()
                            dialog.dismiss()
                            listSelectionViewModel.isEditionEnabled.value = false
                        },
                        getString(R.string.dialog_delete)
                    )

                    dialog.show()
                }
            }
        )
    }

    private fun delete() {
        val list = listSelectionViewModel.selectedItems.value ?: arrayListOf()
        deleteItems(list)
    }

    private fun getItemCount(): Int {
        return adapter.itemCount
    }

    abstract fun deleteItems(indexesOfItemToDelete: ArrayList<Int>)
}
