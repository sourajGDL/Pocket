@file:OptIn(ExperimentalPermissionsApi::class)

package com.cureius.pocket.feature_dashboard.presentation.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.cureius.pocket.R
import com.cureius.pocket.feature_account.presentation.account.AccountsViewModel
import com.cureius.pocket.feature_account.presentation.add_account.AddAccountEvent
import com.cureius.pocket.feature_account.presentation.add_account.AddAccountFormDialog
import com.cureius.pocket.feature_account.presentation.add_account.AddAccountViewModel
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.ActionElement
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.ActionItem
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.CurveBottomMask
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.DashBoardHeader
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.IncomeCreditPrompt
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.InfoSection
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.ItemRow
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.PotItem
import com.cureius.pocket.feature_dashboard.presentation.dashboard.components.RoundIconButton
import com.cureius.pocket.feature_pot.presentation.add_pot.AddPotEvent
import com.cureius.pocket.feature_pot.presentation.add_pot.AddPotViewModel
import com.cureius.pocket.feature_pot.presentation.add_pot.cmponents.AddPotDialog
import com.cureius.pocket.feature_pot.presentation.pots.PotsViewModel
import com.cureius.pocket.feature_pot.presentation.pots.components.CategoryItem
import com.cureius.pocket.feature_sms_sync.presentation.PopUpService
import com.cureius.pocket.feature_sms_sync.presentation.SmsService
import com.cureius.pocket.feature_transaction.presentation.add_transaction.AddTransactionViewModel
import com.cureius.pocket.feature_transaction.presentation.transactions.TransactionsViewModel
import com.cureius.pocket.util.ScreenDimensions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navHostController: NavHostController,
    viewModel: DashBoardViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    addAccountViewModel: AddAccountViewModel = hiltViewModel(),
    potsViewModel: PotsViewModel = hiltViewModel(),
    addPotViewModel: AddPotViewModel = hiltViewModel(),
    transactionsViewModel: TransactionsViewModel = hiltViewModel(),
    addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
) {

    val context: Context = LocalContext.current
    val save = ImageVector.vectorResource(id = R.drawable.save)
    val wallet = ImageVector.vectorResource(id = R.drawable.wallet)
    val emi = ImageVector.vectorResource(id = R.drawable.coins)
    val add = ImageVector.vectorResource(id = R.drawable.add)

    val food = ImageVector.vectorResource(id = R.drawable.food)
    val entertainment = ImageVector.vectorResource(id = R.drawable.food)
    val travel = ImageVector.vectorResource(id = R.drawable.travel)
    val house = ImageVector.vectorResource(id = R.drawable.home)
    val fuel = ImageVector.vectorResource(id = R.drawable.fuel)
    val health = ImageVector.vectorResource(id = R.drawable.health)
    val shopping = ImageVector.vectorResource(id = R.drawable.shopping)
    val grocery = ImageVector.vectorResource(id = R.drawable.shop)
    val account = ImageVector.vectorResource(id = R.drawable.accounts)
    val pot = ImageVector.vectorResource(id = R.drawable.pot)
    val download = ImageVector.vectorResource(id = R.drawable.download)

    val gridItems = listOf(
        CategoryItem(
            icon = food,
            name = "Food",
            fill = 0.4,
        ),
        CategoryItem(
            icon = entertainment,
            name = "Fun",
            fill = 0.7,
        ),
        CategoryItem(
            icon = travel,
            name = "Travel",
            fill = 0.2,
        ),
        CategoryItem(
            icon = house,
            name = "House",
            fill = 0.8,
        ),
        CategoryItem(
            icon = fuel,
            name = "Fuel",
            fill = 0.3,
        ),
        CategoryItem(
            icon = health,
            name = "Health",
            fill = 0.4,
        ),
        CategoryItem(
            icon = shopping,
            name = "Shopping",
            fill = 1.0,
        ),
        CategoryItem(
            icon = grocery,
            name = "Grocery",
            fill = 0.8,
        ),
        CategoryItem(
            adder = true
        ),
    )
    val potItems = potsViewModel.templatePots.value
    val size = ScreenDimensions()
    val screenWeight = size.width()
    val rowCap = (screenWeight / 80)
    val rowNumber = if ((gridItems.size % rowCap) == 0) {
        gridItems.size / rowCap
    } else {
        (gridItems.size / rowCap) + 1
    }
    val startPadding = ((screenWeight - (rowCap * 80)) / 2)
    val intent = Intent(context, SmsService::class.java)
    val popUpIntent = Intent(context, PopUpService::class.java)
    val actionElementList = mutableListOf<ActionItem>()
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchMultiplePermissionRequest()
                }

                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onEvent(DashBoardEvent.ToggleAskPermission)
        }
    }


    if (accountsViewModel.state.value.isEmpty()) {
        actionElementList.add(ActionItem(
            icon = account,
            title = "Add Account",
            subTitle = "Press To Add Account, Necessary To Track",
            borderColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.15f),
            position = -1,
            condition = accountsViewModel.state.value.isEmpty()
        ) {
            addAccountViewModel.onEvent(AddAccountEvent.ToggleAddAccountDialog)
        })
    }

    if (potsViewModel.state.value.isEmpty()) {
        actionElementList.add(ActionItem(
            icon = pot,
            title = "Add Pots",
            subTitle = "Press To Add Pots, Necessary To Track",
            borderColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.15f),
            position = 0,
            condition = potsViewModel.state.value.isEmpty()
        ) {
            addPotViewModel.onEvent(AddPotEvent.ToggleAddAccountDialog)
        })
    }

    if (viewModel.startSyncPromptVisibility.value) {
        actionElementList.add(ActionItem(
            icon = download,
            title = "Start SMS Sync",
            subTitle = "Press To Track Expenses Live On-The-Go",
            borderColor = MaterialTheme.colors.secondary.copy(alpha = 0.4f),
            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.15f),
            position = 1,
            condition = viewModel.startSyncPromptVisibility.value
        ) {
            viewModel.onEvent(DashBoardEvent.ToggleAskPermission)
            context.startService(intent)

            var checkAllPermission = true;
            permissionState.permissions.forEach {
                if (!it.status.isGranted) {
                    checkAllPermission = false
                }
            }
            if (checkAllPermission) {
                println("Permission Granted")
                viewModel.onEvent(DashBoardEvent.ReadAllSMS)
            }

        })
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashBoardHeader(navHostController)
            if (transactionsViewModel.state.value.transactionsOnCurrentMonth.isNotEmpty() && accountsViewModel.state.value.isNotEmpty()) {
                InfoSection(viewModel)
            }
        }
        Box(contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (0).dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
            ) {
                itemsIndexed(actionElementList) { index, item ->
                    ActionElement(
                        icon = item.icon!!,
                        title = item.title!!,
                        subTitle = item.subTitle!!,
                        borderColor = item.borderColor!!,
                        backgroundColor = item.backgroundColor!!,
                        position = if (index == 0) {
                            -1
                        } else if (index == actionElementList.size - 1) {
                            1
                        } else {
                            0
                        },
                        condition = item.condition!!
                    ) {
                        item.onClick()
                    }
                }
                item {
                    if (viewModel.creditDetectionPromptVisibility.value) {
                        IncomeCreditPrompt(position = 1)
                    }
                }
                item {
//                    if (viewModel.permissionPrompt.value && viewModel.startSyncPromptVisibility.value) {
//                        MultiplePermissionExample(viewModel, addTransactionViewModel)
//                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    Text(
                        text = "Your Pots",
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(4.dp, 0.dp)
                    )
                }
                item {
                    LazyRow(
                        modifier = Modifier.height(152.dp), contentPadding = PaddingValues(
                            start = 0.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                        )
                    ) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Spacer(modifier = Modifier.height(8.dp))
                                RoundIconButton(icon = add,
                                    label = "",
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colors.onBackground, CircleShape
                                        )
                                        .height(44.dp)
                                        .width(44.dp),
                                    iconColor = MaterialTheme.colors.background,
                                    onClick = {
                                        addPotViewModel.onEvent(AddPotEvent.ToggleAddAccountDialog)
                                    })
                                Text(
                                    text = "Create new",
                                    color = MaterialTheme.colors.onBackground,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        items(potItems) { item ->
                            Log.d("Pot", "DashboardScreen: $item")
                            PotItem(
                                item, item.icon, 0.8f, item.title, item.type
                            )

                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    Text(
                        text = "Expense Categories",
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(4.dp, 0.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                (1..rowNumber).forEach {
                    item {
                        ItemRow(
                            gridItems.subList(
                                (rowCap * (it - 1)),
                                if ((rowCap * (it - 1)) + rowCap > gridItems.size) {
                                    gridItems.size
                                } else {
                                    (rowCap * (it - 1)) + rowCap
                                }
                            ), modifier = Modifier.padding(start = startPadding.dp)
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
                item {
                    Button(onClick = {
                        requestOverlayPermission(context)
                        context.startService(popUpIntent)
                    }) {
                        Text(text = "Start Service")
                    }
                }
                item {
                    Button(onClick = {
                        context.stopService(popUpIntent)
                    }) {
                        Text(text = "Stop Service")
                    }
                }
                item { Spacer(modifier = Modifier.height(88.dp)) }
            }
            CurveBottomMask(cornerColor = MaterialTheme.colors.surface)
        }
        if (addAccountViewModel.dialogVisibility.value) {
            AddAccountFormDialog(onDismiss = {
                addAccountViewModel.onEvent(AddAccountEvent.ToggleAddAccountDialog)
            }, onSubmit = {

            })
        }
        if (addPotViewModel.dialogVisibility.value) {
            AddPotDialog(onDismiss = {
                addPotViewModel.onEvent(AddPotEvent.ToggleAddAccountDialog)
            }, onSubmit = {

            })
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionExample() {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.READ_SMS)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {

        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchPermissionRequest()
                }

                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (permissionState.status.isGranted) {
            Text(text = "Permission Granted")
        } else {
            val text = if (permissionState.status.shouldShowRationale) {
                "Permission Required"
            } else {
                "Please provide Permission"
            }
            Text(text = text)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissionExample(
    viewModel: DashBoardViewModel, addTransactionViewModel: AddTransactionViewModel
) {

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchMultiplePermissionRequest()
                }

                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onEvent(DashBoardEvent.ToggleAskPermission)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var checkAllPermission = true;
        permissionState.permissions.forEach {
            if (!it.status.isGranted) {
                checkAllPermission = false
            }
        }
        if (checkAllPermission) {
            println("Permission Granted")
            viewModel.onEvent(DashBoardEvent.ReadAllSMS)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
private fun requestOverlayPermission(context: Context) {
    if (!Settings.canDrawOverlays(context)) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
