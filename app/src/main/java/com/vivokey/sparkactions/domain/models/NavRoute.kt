package com.vivokey.sparkactions.domain.models

sealed class NavRoute(val route: String) {
    data object HomeScreen: NavRoute("home_screen")
    data object EditActionScreen: NavRoute("edit_action")
    data object DigitalCardScreen: NavRoute("digital_card")
    data object ImageCropper: NavRoute("image_cropper")
}