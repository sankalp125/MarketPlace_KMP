package com.sankalp.marketplace.utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class DesktopImagePicker : ImagePicker {
    override fun pickImage(
        source: ImageSource,
        onResult: (String?) -> Unit
    ) {
        val dialog = FileDialog(Frame(), "Select Image")
        dialog.isVisible = true

        val file = dialog.file ?: return
        onResult(File(dialog.directory, file).absolutePath)
    }
}