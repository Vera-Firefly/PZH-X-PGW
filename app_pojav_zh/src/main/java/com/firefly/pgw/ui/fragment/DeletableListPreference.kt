package com.firefly.pgw.ui.fragment

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference

import com.firefly.pgw.renderer.RendererManager
import com.firefly.pgw.utils.ListAndArray
import com.firefly.pgw.utils.MesaUtils

import com.movtery.pojavzh.ui.dialog.TipDialog

import net.kdt.pojavlaunch.R

class DeletableListPreference @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ListPreference(context, attrs) {

    private val defaultLibs: List<String>
    private var preferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        defaultLibs = context.resources.getStringArray(R.array.osmesa_values).toList()
    }

    override fun onClick() {
        val initialValue = value
        val builder = AlertDialog.Builder(context).apply {
            setTitle(dialogTitle)
            setItems(entries.map { it.toString() }.toTypedArray()) { dialog, which ->
                val newValue = entryValues[which].toString()
                if (newValue != initialValue) {
                    preferenceChangeListener?.let {
                        if (it.onPreferenceChange(this@DeletableListPreference, newValue)) {
                            value = newValue
                        }
                    } ?: run {
                        value = newValue
                    }
                }
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()

        val listView = dialog.listView
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedVersion = entryValues[position].toString()
            if (defaultLibs.contains(selectedVersion)) {
                Toast.makeText(context, R.string.preference_rendererexp_mesa_delete_defaultlib, Toast.LENGTH_SHORT).show()
            } else {
                showDeleteConfirmationDialog(selectedVersion)
            }
            dialog.dismiss()
            true
        }
    }

    override fun setOnPreferenceChangeListener(listener: OnPreferenceChangeListener?) {
        preferenceChangeListener = listener
        super.setOnPreferenceChangeListener(listener)
    }

    private fun showDeleteConfirmationDialog(version: String) {
        TipDialog.Builder(context)
            .setTitle(R.string.preference_rendererexp_mesa_delete_title)
            .setMessage(context.getString(R.string.preference_rendererexp_mesa_delete_message, version))
            .setConfirmClickListener {
                val success = MesaUtils.INSTANCE.deleteMesaLib(version)
                if (success) {
                    Toast.makeText(context, R.string.preference_rendererexp_mesa_deleted, Toast.LENGTH_SHORT).show()
                    setEntriesAndValues()
                } else {
                    Toast.makeText(context, R.string.preference_rendererexp_mesa_delete_fail, Toast.LENGTH_SHORT).show()
                }
            }.setCancelable(false).buildDialog()
    }

    private fun setEntriesAndValues() {
        val array: ListAndArray = RendererManager.getCompatibleCMesaLib(context)
        entries = array.getArray()
        entryValues = array.getList().toTypedArray()
        val currentValue = value
        if (!array.getList().contains(currentValue)) {
            setValueIndex(0)
        }
    }
}