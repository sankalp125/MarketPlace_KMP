package com.sankalp.marketplace.utils

import platform.UIKit.*

fun getTopViewController(): UIViewController? {

    val window = UIApplication.sharedApplication.windows
        .firstOrNull { (it as UIWindow).isKeyWindow() } as? UIWindow

    var topController = window?.rootViewController

    while (topController?.presentedViewController != null) {
        topController = topController.presentedViewController
    }

    return topController
}