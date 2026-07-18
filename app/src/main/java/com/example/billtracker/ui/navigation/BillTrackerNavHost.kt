package com.example.billtracker.ui.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.billtracker.AppContainer
import com.example.billtracker.ui.components.BottomNavBar
import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.components.BillDatePicker
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
fun BillTrackerNavHost(container: AppContainer) {
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

    // route ปัจจุบัน - ใช้ตัดสินว่าควรโชว์ BottomNavBar ไหม และ tab ไหนถูกไฮไลต์
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // แสดง BottomNavBar เฉพาะ 3 หน้าหลัก (ไม่โชว์ตอนอยู่ใน Detail/AddEdit
    // เพราะหน้าพวกนี้เป็น "หน้าย่อย" ที่เข้าถึงผ่านปุ่ม back ไม่ใช่ tab)
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
                val viewModel: BillListViewModel = viewModel(
                    factory = BillListViewModel.Factory(
                        container.getAllBillsUseCase,
                        container.getAllCategoriesUseCase,
                        container.markBillAsPaidUseCase
                    )
                )
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
                val viewModel: BillDetailViewModel = viewModel(
                    factory = BillDetailViewModel.Factory(
                        container.getBillByIdUseCase,
                        container.getCategoryByIdUseCase,
                        container.markBillAsPaidUseCase,
                        container.deleteBillUseCase
                    )
                )

                LaunchedEffect(billId) { viewModel.load(billId) }

                val bill by viewModel.bill.observeAsState()
                val category by viewModel.category.observeAsState()

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
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { navController.navigate(Routes.editBill(billId)) },
                            onDeleteConfirm = { viewModel.delete() },
                            onMarkAsPaidClick = { viewModel.markAsPaid() }
                        )
                    }
                    currentBill != null && currentCategory == null -> {
                        // bill เจอ แต่ category ไม่เจอ - มักเกิดจาก categoryId ไม่ตรงกับ
                        // category ที่มีอยู่จริงใน DB (ดูคอมเมนต์ด้านบนฟังก์ชันนี้)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                "ไม่พบหมวดหมู่ของรายการนี้ (categoryId: ${currentBill.categoryId})\n" +
                                        "อาจเป็นเพราะหมวดหมู่ถูกลบไปแล้ว"
                            )
                        }
                    }
                    else -> {
                        // ยังโหลดไม่เสร็จ (LaunchedEffect(billId) ยังทำงานไม่จบ)
                        androidx.compose.foundation.layout.Box(
                            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator()
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
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val billId = backStackEntry.arguments?.getInt(Routes.ARG_BILL_ID) ?: -1
                val isEditMode = billId != -1

                val viewModel: AddEditBillViewModel = viewModel(
                    factory = AddEditBillViewModel.Factory(
                        container.getBillByIdUseCase,
                        container.getAllCategoriesUseCase,
                        container.addBillUseCase,
                        container.updateBillUseCase
                    )
                )

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
                val viewModel: CategoryManageViewModel = viewModel(
                    factory = CategoryManageViewModel.Factory(
                        container.getAllCategoriesUseCase,
                        container.addCategoryUseCase,
                        container.deleteCategoryUseCase
                    )
                )
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
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.Factory(
                        container.exportDataUseCase,
                        container.deleteAllDataUseCase
                    )
                )

                LaunchedEffect(Unit) {
                    viewModel.events.observeForever { event ->
                        when (val content = event.getContentIfNotHandled()) {
                            is SettingsEvent.ExportSuccess ->
                                scope.launch { snackbarHostState.showSnackbar("ส่งออกข้อมูลสำเร็จ") }
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
                    githubUrl = "https://github.com/yourname/bill-tracker",
                    onExportData = { viewModel.exportData() },
                    onDeleteAllDataConfirm = { viewModel.deleteAllData() },
                    onOpenGithub = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/yourname/bill-tracker")
                        )
                        context.startActivity(intent)
                    },
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}