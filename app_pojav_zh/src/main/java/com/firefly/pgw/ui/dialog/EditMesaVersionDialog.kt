package com.firefly.pgw.ui.dialog

import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import com.movtery.pojavzh.ui.dialog.DraggableDialog
import com.movtery.pojavzh.ui.dialog.FullScreenDialog
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.prefs.LauncherPreferences

class EditMesaVersionDialog(context: Context) : FullScreenDialog(context), DraggableDialog.DialogInitializationListener {

    private lateinit var mMesaGLVersion: EditText
    private lateinit var mMesaGLSLVersion: EditText
    private lateinit var mCancelButton: Button
    private lateinit var mConfirmButton: Button

    init {
        setCancelable(false)
        setContentView(R.layout.dialog_edit_mesa_version)
        init()
        DraggableDialog.initDialog(this)
    }

    private fun init() {
        mMesaGLVersion = findViewById(R.id.zh_edit_mesa_version_gl_version)
        mMesaGLSLVersion = findViewById(R.id.zh_edit_mesa_version_glsl_version)
        mCancelButton = findViewById(R.id.zh_edit_mesa_version_cancel_button)
        mConfirmButton = findViewById(R.id.zh_edit_mesa_version_confirm_button)

        mMesaGLVersion.setText(LauncherPreferences.PREF_MESA_GL_VERSION)
        mMesaGLSLVersion.setText(LauncherPreferences.PREF_MESA_GLSL_VERSION)

        mCancelButton.setOnClickListener { dismiss() }

        mConfirmButton.setOnClickListener {
            val glVersion = mMesaGLVersion.text.toString()
            val glslVersion = mMesaGLSLVersion.text.toString()

            if (isInvalidVersion(glVersion, "2.8", "4.6") && isInvalidVersion(glslVersion, "280", "460")) {
                mMesaGLVersion.setError(context.getString(R.string.customglglsl_alertdialog_error_gl))
                mMesaGLVersion.requestFocus()
                mMesaGLSLVersion.setError(context.getString(R.string.customglglsl_alertdialog_error_glsl))
                mMesaGLSLVersion.requestFocus()
                return@setOnClickListener
            } else if (isInvalidVersion(glVersion, "2.8", "4.6")) {
                mMesaGLVersion.setError(context.getString(R.string.customglglsl_alertdialog_error_gl))
                mMesaGLVersion.requestFocus()
                return@setOnClickListener
            } else if (isInvalidVersion(glslVersion, "280", "460")) {
                mMesaGLSLVersion.setError(context.getString(R.string.customglglsl_alertdialog_error_glsl))
                mMesaGLSLVersion.requestFocus()
                return@setOnClickListener
            }

            LauncherPreferences.PREF_MESA_GL_VERSION = glVersion
            LauncherPreferences.PREF_MESA_GLSL_VERSION = glslVersion

            LauncherPreferences.DEFAULT_PREF.edit()
                .putString("mesaGLVersion", glVersion)
                .putString("mesaGLSLVersion", glslVersion)
                .apply()

            dismiss()
        }
    }

    private fun isInvalidVersion(version: String, minVersion: String, maxVersion: String): Boolean {
        return try {
            val versionNumber = version.toFloat()
            val minVersionNumber = minVersion.toFloat()
            val maxVersionNumber = maxVersion.toFloat()
            !(versionNumber >= minVersionNumber && versionNumber <= maxVersionNumber)
        } catch (e: NumberFormatException) {
            true
        }
    }

    override fun onInit(): Window {
        return window!!
    }
}