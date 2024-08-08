package com.example.sketchcrew.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.example.sketchcrew.R

class SavCanvasDialog(
    context: Context,
    private val onSave: (String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.save_canvas_dialog)

        val canvasNameEditText: EditText = findViewById(R.id.canvasNameEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        canvasNameEditText.doAfterTextChanged {
            saveButton.isEnabled = it.toString().trim().isNotEmpty()
        }

        saveButton.setOnClickListener {
            val canvasName = canvasNameEditText.text.toString().trim()
            if (canvasName.isNotEmpty()) {
                onSave(canvasName)
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}