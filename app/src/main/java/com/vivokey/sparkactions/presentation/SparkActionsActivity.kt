package com.vivokey.sparkactions.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hoker.intra.domain.NfcActivity
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.NavRoute
import com.vivokey.sparkactions.presentation.components.SplashScreen
import com.vivokey.sparkactions.presentation.digital_card.DigitalCardScreen
import com.vivokey.sparkactions.presentation.digital_card.DigitalCardViewModel
import com.vivokey.sparkactions.presentation.digital_card.components.ImageCropper
import com.vivokey.sparkactions.presentation.edit_action.EditActionScreen
import com.vivokey.sparkactions.presentation.edit_action.EditActionViewModel
import com.vivokey.sparkactions.presentation.home_screen.HomeScreen
import com.vivokey.sparkactions.presentation.home_screen.HomeScreenViewModel
import com.vivokey.sparkactions.presentation.theme.SparkActionsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SparkActionsActivity : NfcActivity() {

    private var digitalCardViewModel: DigitalCardViewModel? = null
    private var writtenAction: MutableState<Action?> = mutableStateOf(null)
    private var selectedAction: MutableState<Action?> = mutableStateOf(null)
    private var isSelectedFromScan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            SparkActionsTheme {

                val showSplashScreen = remember { mutableStateOf(true) }

                if (showSplashScreen.value) {
                    SplashScreen {
                        showSplashScreen.value = false
                    }
                } else {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.HomeScreen.route
                    ) {
                        composable(NavRoute.HomeScreen.route) {
                            val viewModel: HomeScreenViewModel = hiltViewModel()
                            HomeScreen(
                                viewModel = viewModel,
                                writtenAction = writtenAction.value,
                                onActionSelected = { action, isDigitalCard, isFromScan ->
                                    selectedAction.value = action
                                    isSelectedFromScan = isFromScan
                                    if (isDigitalCard) {
                                        navController.navigate(NavRoute.DigitalCardScreen.route)
                                    } else {
                                        navController.navigate(NavRoute.EditActionScreen.route)
                                    }
                                }
                            )
                        }
                        composable(NavRoute.EditActionScreen.route) {
                            val viewModel: EditActionViewModel = hiltViewModel()
                            EditActionScreen(
                                viewModel = viewModel,
                                saveOnExit = !isSelectedFromScan,
                                selectedAction = selectedAction.value,
                                onBackPressed = {
                                    navController.popBackStack()
                                },
                                popBackFromWrite = { action ->
                                    writtenAction.value = action
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(NavRoute.DigitalCardScreen.route) {
                            digitalCardViewModel = hiltViewModel()
                            digitalCardViewModel?.let { viewModel ->
                                DigitalCardScreen(
                                    viewModel,
                                    saveOnExit = !isSelectedFromScan,
                                    selectedAction = selectedAction.value,
                                    navigateToImageCropper = {
                                        navController.navigate(NavRoute.ImageCropper.route)
                                    },
                                    onBackPressed = {
                                        navController.popBackStack()
                                    },
                                    popBackFromWrite = { action ->
                                        writtenAction.value = action
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable(NavRoute.ImageCropper.route) {
                            digitalCardViewModel?.let { viewModel ->
                                ImageCropper(
                                    viewModel = viewModel,
                                    popBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}