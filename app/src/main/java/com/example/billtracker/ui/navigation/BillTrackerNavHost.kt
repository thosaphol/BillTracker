package com.example.billtracker.ui.navigation


import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.billtracker.ui.components.BillDatePicker
import com.example.billtracker.ui.components.BottomNavBar
import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.screens.AddEditBillScreen
import com.example.billtracker.ui.screens.BillDetailScreen
import com.example.billtracker.ui.screens.BillListScreen
import com.example.billtracker.ui.screens.CategoryManageScreen
import com.example.billtracker.ui.screens.SettingsScreen
import com.example.billtracker.ui.viewmodel.AddEditBillViewModel
import com.example.billtracker.ui.viewmodel.BillDetailEvent
import com.example.billtracker.ui.viewmodel.BillDetailViewModel
import com.example.billtracker.ui.viewmodel.BillListViewModel
import com.example.billtracker.ui.viewmodel.CategoryManageViewModel
import com.example.billtracker.ui.viewmodel.SaveBillResult
import com.example.billtracker.ui.viewmodel.SettingsEvent
import com.example.billtracker.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch


@Composable
fun BillTrackerNavHost(){
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun onNavSelect(destination: BottomNavDestination) {
        val route = when (destination) {
            BottomNavDestination.BILLS -> Routes.BILL_LIST
            BottomNavDestination.CATEGORIES -> Routes.CATEGORY_MANAGE
            BottomNavDestination.SETTINGS -> Routes.SETTINGS
        }
        navController.navigate(route) {
            popUpTo(Routes.BILL_LIST) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }


    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route


    val currentBottomNavDestination = when (currentRoute) {
        Routes.BILL_LIST -> BottomNavDestination.BILLS
        Routes.CATEGORY_MANAGE -> BottomNavDestination.CATEGORIES
        Routes.SETTINGS -> BottomNavDestination.SETTINGS
        else -> null
    }

    Scaffold(
        bottomBar = {
            if (currentBottomNavDestination != null) {
                BottomNavBar(current = currentBottomNavDestination, onSelect = ::onNavSelect)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { scaffoldPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.BILL_LIST,
            modifier = Modifier.padding(scaffoldPadding)
        ) {

            // ---------------- BillListScreen ----------------
            composable(Routes.BILL_LIST) {

                val viewModel: BillListViewModel = hiltViewModel()
                val bills by viewModel.bills.observeAsState(emptyList())
                val categories by viewModel.categories.observeAsState(emptyList())

                LaunchedEffect(Unit) {
                    viewModel.errorEvent.observeForever { event ->
                        event.getContentIfNotHandled()?.let { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    }
                }

                BillListScreen(
                    bills = bills,
                    categories = categories,
                    onBillClick = { billId -> navController.navigate(Routes.billDetail(billId)) },
                    onTogglePaid = { bill -> viewModel.togglePaid(bill) },
                    onAddClick = { navController.navigate(Routes.addBill) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                )
            }

            // ---------------- BillDetailScreen ----------------
            composable(
                route = Routes.BILL_DETAIL,
                arguments = listOf(navArgument(Routes.ARG_BILL_ID) { type = NavType.IntType })
            ) { backStackEntry ->
                val billId = backStackEntry.arguments?.getInt(Routes.ARG_BILL_ID) ?: return@composable
                val viewModel: BillDetailViewModel = hiltViewModel()

                LaunchedEffect(billId) { viewModel.load(billId) }

                val bill by viewModel.bill.observeAsState()
                val category by viewModel.category.observeAsState()
                val holidayWarnings by viewModel.holidayWarning.observeAsState(emptyList())

                LaunchedEffect(Unit) {
                    viewModel.events.observeForever { event ->
                        when (val content = event.getContentIfNotHandled()) {
                            is BillDetailEvent.DeletedSuccessfully -> navController.popBackStack()
                            is BillDetailEvent.Error -> scope.launch { snackbarHostState.showSnackbar(content.message) }
                            null -> Unit
                        }
                    }
                }

                val currentBill = bill
                val currentCategory = category
                when {
                    currentBill != null && currentCategory != null -> {
                        BillDetailScreen(
                            bill = currentBill,
                            category = currentCategory,
                            holidayWarnings = holidayWarnings,
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { navController.navigate(Routes.editBill(billId)) },
                            onDeleteConfirm = { viewModel.delete() },
                            onMarkAsPaidClick = { viewModel.markAsPaid() }
                        )
                    }
                    currentBill != null && currentCategory == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "ไม่พบหมวดหมู่ของรายการนี้ (categoryId: ${currentBill.categoryId})\n" +
                                        "อาจเป็นเพราะหมวดหมู่ถูกลบไปแล้ว"
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // ---------------- AddEditBillScreen ----------------
            composable(
                route = Routes.ADD_EDIT_BILL,
                arguments = listOf(
                    navArgument(Routes.ARG_BILL_ID) {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val billId = backStackEntry.arguments?.getInt(Routes.ARG_BILL_ID) ?: -1
                val isEditMode = billId > 0
                val viewModel: AddEditBillViewModel = hiltViewModel()

                LaunchedEffect(billId) { if (isEditMode) viewModel.load(billId) }

                val formState by viewModel.formState.observeAsState()
                val categories by viewModel.categories.observeAsState(emptyList())

                LaunchedEffect(Unit) {
                    viewModel.saveEvent.observeForever { event ->
                        when (val result = event.getContentIfNotHandled()) {
                            is SaveBillResult.Success -> navController.popBackStack()
                            is SaveBillResult.Error -> scope.launch { snackbarHostState.showSnackbar(result.message) }
                            null -> Unit
                        }
                    }
                }

                val currentFormState = formState
                var showDueDatePicker by remember { mutableStateOf(false) }
                var showReminderStartDatePicker by remember { mutableStateOf(false) }

                if (currentFormState != null) {
                    AddEditBillScreen(
                        isEditMode = isEditMode,
                        formState = currentFormState,
                        categories = categories,
                        onTitleChange = viewModel::onTitleChange,
                        onAmountChange = viewModel::onAmountChange,
                        onCategorySelect = viewModel::onCategorySelect,
                        onDueDateClick = { showDueDatePicker = true },
                        onReminderEnabledChange = viewModel::onReminderEnabledChange,
                        onReminderTypeChange = viewModel::onReminderTypeChange,
                        onReminderStartDateClick = { showReminderStartDatePicker = true },
                        onNoteChange = viewModel::onNoteChange,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { viewModel.save() }
                    )

                    if (showDueDatePicker) {
                        BillDatePicker(
                            onDismiss = { showDueDatePicker = false },
                            onConfirm = { millis ->
                                millis?.let { viewModel.onDueDateSelected(it) }
                                showDueDatePicker = false
                            }
                        )
                    }

                    if (showReminderStartDatePicker) {
                        BillDatePicker(
                            onDismiss = { showReminderStartDatePicker = false },
                            onConfirm = { millis ->
                                millis?.let { viewModel.onReminderStartDateSelected(it) }
                                showReminderStartDatePicker = false
                            }
                        )
                    }
                }
            }

            // ---------------- CategoryManageScreen ----------------
            composable(Routes.CATEGORY_MANAGE) {
                val viewModel: CategoryManageViewModel = hiltViewModel()
                val categories by viewModel.categories.observeAsState(emptyList())

                LaunchedEffect(Unit) {
                    viewModel.errorEvent.observeForever { event ->
                        event.getContentIfNotHandled()?.let { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    }
                }

                CategoryManageScreen(
                    categories = categories,
                    onAddCategory = { name -> viewModel.addCategory(name) },
                    onDeleteCategory = { category -> viewModel.deleteCategory(category) },
                    onBackClick = { navController.popBackStack() },
                )
            }

            // ---------------- SettingsScreen ----------------
            composable(Routes.SETTINGS) {
                val context = LocalContext.current
                val viewModel: SettingsViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    viewModel.events.observeForever { event ->
                        when (val content = event.getContentIfNotHandled()) {
                            is SettingsEvent.ExportSuccess ->
                                scope.launch { snackbarHostState.showSnackbar("ส่งออกข้อมูลสำเร็จ") }
                            is SettingsEvent.ImportSuccess ->
                                scope.launch { snackbarHostState.showSnackbar("นำเข้าข้อมูลสำเร็จ") }
                            is SettingsEvent.DeleteAllSuccess ->
                                scope.launch { snackbarHostState.showSnackbar("ลบข้อมูลทั้งหมดสำเร็จ") }
                            is SettingsEvent.Error ->
                                scope.launch { snackbarHostState.showSnackbar(content.message) }
                            null -> Unit
                        }
                    }
                }

                SettingsScreen(
                    appVersion = "1.0.0",
                    onExportData = { password -> viewModel.exportData(password) },
                    onImportData = { uri, password -> viewModel.importData(uri, password) },
                    onDeleteAllDataConfirm = { viewModel.deleteAllData() },
                    onOpenNotificationSettings = {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                        } else {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(android.net.Uri.parse("package:${context.packageName}"))
                        }
                        context.startActivity(intent)
                    },
                    onBackClick = { navController.popBackStack() },
//                    onTestReminderClick = {
//                        scope.launch { container.debugTestReminderNow(context) }
//                    },
                )
            }
        }
    }
}