package com.example.customlistview2shopproducts

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MyAlertDialog : DialogFragment() {

    private var removable: Removable? = null
    private var updatable: Updatable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        removable = context as Removable?
        updatable = context as Updatable?
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val product = requireArguments().getSerializable("product")
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        return builder
            .setTitle("Внимание!")
            .setMessage("Предполагаемые действия")
            .setPositiveButton("Удалить") { dialog, which ->
                removable?.remove(product as Product)
            }
            .setNeutralButton("Редактировать") { dialog, which ->
                updatable?.update(product as Product)
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
}