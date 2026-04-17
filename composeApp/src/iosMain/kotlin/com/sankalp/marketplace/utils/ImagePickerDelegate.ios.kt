package com.sankalp.marketplace.utils


import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

class ImagePickerDelegate(
    private val onResult: (String?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val path = image?.let { saveImageToTemp(it) }

        onResult(path)

        picker.dismissViewControllerAnimated(true, null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onResult(null)
        picker.dismissViewControllerAnimated(true, null)
    }
    fun saveImageToTemp(image: UIImage): String? {

        // UIImage → NSData (JPEG)
        val data = UIImageJPEGRepresentation(image, 0.9) ?: return null

        // unique filename
        val fileName = "camera_${NSDate().timeIntervalSince1970}.jpg"

        // temp directory path
        val tempDir = NSTemporaryDirectory()
        val fullPath = tempDir + fileName

        // save file
        val nsData = data as NSData

        return if (nsData.writeToFile(fullPath, true)) {
            fullPath
        } else {
            null
        }
    }
}