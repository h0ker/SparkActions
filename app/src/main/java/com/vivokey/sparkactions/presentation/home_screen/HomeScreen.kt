package com.vivokey.sparkactions.presentation.home_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.DigitalCardActionTarget
import com.vivokey.sparkactions.presentation.components.GradientLazyColumn
import com.vivokey.sparkactions.presentation.home_screen.components.ActionCard
import com.vivokey.sparkactions.presentation.home_screen.components.HomeScreenTopBar
import com.vivokey.sparkactions.presentation.home_screen.components.InfoCard
import com.vivokey.sparkactions.presentation.home_screen.components.SpeedDialFab
import com.vivokey.sparkactions.presentation.theme.SparkActionsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    writtenAction: Action?,
    onActionSelected: (Action?, Boolean, Boolean) -> Unit
) {

    val context = LocalContext.current
    val actions = viewModel.actions.collectAsState()
    
    LaunchedEffect(viewModel.toastChannel) {
        viewModel.toastChannel.collect {
            Toast.makeText(context, context.getText(it), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(writtenAction) {
        writtenAction?.let {
            viewModel.currentAction = it
        }
    }

    SparkActionsTheme {
        Scaffold(
            topBar = {
                HomeScreenTopBar()
            },
            floatingActionButton = {
                SpeedDialFab(
                    fabText = "Add Action",
                    firstOptionText = "Digital Card",
                    secondOptionText = "New Action",
                    firstOptionIcon = Icons.Default.AccountBox,
                    secondOptionIcon = Icons.Default.Share,
                    onFirstOptionSelected = {
                        onActionSelected(null, true, false)
                    },
                    onSecondOptionSelected = {
                        onActionSelected(null, false, false)
                    }
                )
            },
            contentColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                GradientLazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (actions.value.isEmpty() && viewModel.currentAction == null) {
                        item {
                            InfoCard(infoText = "Choose what you want to happen when someone scans your VivoKey Spark")
                        }
                    }

                    item {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "On Your Spark",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp
                        )
                    }

                    if (viewModel.getResult == HomeScreenViewModel.NEW && viewModel.currentAction == null) {
                        item {
                            InfoCard(
                                modifier = Modifier.fillMaxWidth(),
                                infoText = "There is currently no action set on your Spark"
                            )
                        }
                    }

                    if (viewModel.currentAction == null && viewModel.getResult == null) {
                        item {
                            InfoCard(
                                modifier = Modifier.fillMaxWidth(),
                                infoText = "Scan your Spark to see its current action."
                            )
                        }
                    }

                    viewModel.currentAction?.let { action ->
                        item {
                            ActionCard(
                                action = action,
                                onDeleteAction = null
                            )
                        }
                    }

                    val digitalCardList = actions.value.filter { it.target is DigitalCardActionTarget }.sortedByDescending { it.id }
                    if (digitalCardList.isNotEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                                text = "Saved Digital Cards",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                        }

                        items(digitalCardList) { action ->
                            ActionCard(
                                modifier = Modifier.padding(top = 16.dp),
                                action = action,
                                onDeleteAction = {
                                    viewModel.removeAction(action)
                                }
                            ) {
                                onActionSelected(action, action.target is DigitalCardActionTarget, false)
                            }
                        }
                    }

                    val actionList = actions.value.filter { it.target !is DigitalCardActionTarget }.sortedByDescending { it.id }
                    if (actionList.isNotEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "Saved Actions",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                        }

                        items(actionList) { action ->
                            ActionCard(
                                modifier = Modifier.padding(bottom = 16.dp),
                                action = action,
                                onDeleteAction = {
                                    viewModel.removeAction(action)
                                }
                            ) {
                                onActionSelected(action, action.target is DigitalCardActionTarget, false)
                            }
                        }
                    }
                }
                if (viewModel.isLoading) {
                    Surface(
                        color = Color.Black.copy(alpha = .5f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}