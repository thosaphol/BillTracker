package com.example.billtracker.ui.navigation


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.billtracker.AppContainer
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.ui.components.BillDatePicker
// import androidx.hilt.navigation.compose.hiltViewModel  // <- เปิดใช้ตอนมี ViewModel จริง

import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.ui.screens.AddEditBillScreen
import com.example.billtracker.ui.screens.BillDetailScreen
import com.example.billtracker.ui.screens.BillFormState
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

/**
 * NavHost หลักของแอป - เรียกจาก MainActivity ตัวเดียว (Single Activity)
 *
 * *** สำคัญ: ไฟล์นี้เป็นแค่ "โครงเส้นทาง" ***
 * ทุกจุดที่คอมเมนต์ว่า "TODO: ต่อ ViewModel" คือจุดที่คุณต้องแทนที่
 * ด้วย hiltViewModel<YourViewModel>() แล้วดึง state จริงจาก LiveData
 * (ตอนนี้ผมใส่ placeholder ว่างๆ ไว้ให้ compile ผ่านก่อนเฉยๆ)
 */
@Composable
fun BillTrackerNavHost(
//    navController: NavHostController = rememberNavController()
    container: AppContainer
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // ใช้ร่วมกันทุกหน้าเพื่อสลับ bottom nav (รายการ/หมวดหมู่/ตั้งค่า)
    fun onNavSelect(destination: BottomNavDestination) {
        val route = when (destination) {
            BottomNavDestination.BILLS -> Routes.BILL_LIST
            BottomNavDestination.CATEGORIES -> Routes.CATEGORY_MANAGE
            BottomNavDestination.SETTINGS -> Routes.SETTINGS
        }
        navController.navigate(route) {
            // กันไม่ให้ backstack พองเวลากดสลับ bottom nav ไปมา
            popUpTo(Routes.BILL_LIST) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        NavHost(navController = navController, startDestination = Routes.BILL_LIST) {

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
                    onNavSelect = ::onNavSelect
                )
            }

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
                if (currentBill != null && currentCategory != null) {
                    BillDetailScreen(
                        bill = currentBill,
                        category = currentCategory,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = { navController.navigate(Routes.editBill(billId)) },
                        onDeleteConfirm = { viewModel.delete() },
                        onMarkAsPaidClick = { viewModel.markAsPaid() }
                    )
                }
                // ถ้ายังโหลดไม่เสร็จ (currentBill == null) จะไม่ render อะไร
                // อยากได้ loading indicator ระหว่างนี้ ให้เพิ่ม else { LoadingIndicator() } ตรงนี้

            }

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
                        onDueDateClick = {
                            showDueDatePicker = true
                        },
                        onReminderEnabledChange = viewModel::onReminderEnabledChange,
                        onReminderTypeChange = viewModel::onReminderTypeChange,
                        onReminderStartDateClick = {
                            showReminderStartDatePicker = true
                        },
                        onNoteChange = viewModel::onNoteChange,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { viewModel.save() }
                    )

                    if (showDueDatePicker) {
                        BillDatePicker(
                            initialMillis = currentFormState.dueDate,
                            onDismiss = { showDueDatePicker = false },
                            onConfirm = { millis ->
                                millis?.let { viewModel.onDueDateSelected(it) }
                                showDueDatePicker = false
                            }
                        )
                    }

                    if (showReminderStartDatePicker) {
                        BillDatePicker(
                            initialMillis = currentFormState.reminderStartDate,
                            onDismiss = { showReminderStartDatePicker = false },
                            onConfirm = { millis ->
                                millis?.let { viewModel.onReminderStartDateSelected(it) }
                                showReminderStartDatePicker = false
                            }
                        )
                    }
                }
            }

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
                    onNavSelect = ::onNavSelect
                )
            }

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
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/yourname/bill-tracker")
                        )
                        context.startActivity(intent)
                    },
                    onBackClick = { navController.popBackStack() },
                    onNavSelect = ::onNavSelect
                )
            }


        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}