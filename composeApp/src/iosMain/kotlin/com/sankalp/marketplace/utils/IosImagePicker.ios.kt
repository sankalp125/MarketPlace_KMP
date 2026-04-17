package com.sankalp.marketplace.utils

import platform.UIKit.UIViewController
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

class IosImagePicker(
    private val viewController: UIViewController
) : ImagePicker {

    private var delegateRef: ImagePickerDelegate? = null

    override fun pickImage(
        source: ImageSource,
        onResult: (String?) -> Unit
    ) {

        val picker = UIImagePickerController()

        picker.sourceType = when (source) {
            ImageSource.CAMERA ->
                if (UIImagePickerController.isSourceTypeAvailable(
                        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    )
                )
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                else UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary

            ImageSource.GALLERY ->
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }

        // ✅ Delegate fix
        delegateRef = ImagePickerDelegate(onResult)
        picker.delegate = delegateRef

        // ✅ FINAL FIX (correct call)
        viewController.presentViewController(
            viewControllerToPresent = picker,
            animated = true,
            completion = null
        )
    }
}