package com.sankalp.marketplace.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController

@Composable
actual fun rememberImagePicker(): ImagePicker {

    val vc = LocalUIViewController.current

    return remember {
        IosImagePicker(vc)
    }
}