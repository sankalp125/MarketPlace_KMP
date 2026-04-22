package com.sankalp.marketplace.utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.SwingUtilities

class DesktopImagePicker : ImagePicker {
    override fun pickImage(
        source: ImageSource,
        onResult: (String?) -> Unit
    ) {
        SwingUtilities.invokeLater {
            val dialog = FileDialog(null as Frame?, "Select Image", FileDialog.LOAD)
            dialog.isMultipleMode = false
            dialog.isVisible = true  // ← Block karta hai but SwingUtilities thread pe

            val file = dialog.file
            val dir  = dialog.directory

            val path = if (file != null && dir != null) {
                File(dir, file).absolutePath
            } else null

            onResult(path)
        }
    }
}