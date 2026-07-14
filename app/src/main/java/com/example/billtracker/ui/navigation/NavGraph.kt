package com.example.billtracker.ui.navigation

/**
 * Route ทั้งหมดของแอป - Single Activity + NavHost (ไม่มี Fragment)
 * ตาม requirement เดิม: bill_list เป็นหน้าแรกเสมอ
 */
object Routes {
    const val BILL_LIST = "bill_list"
    const val BILL_DETAIL = "bill_detail/{billId}"
    const val ADD_EDIT_BILL = "add_edit_bill?billId={billId}"
    const val CATEGORY_MANAGE = "category_manage"
    const val SETTINGS = "settings"

    fun billDetail(billId: Int) : String{
        return "bill_detail/$billId"
    }
    fun editBill(billId: Int) = "add_edit_bill?billId=$billId"
    const val addBill = "add_edit_bill"

    const val ARG_BILL_ID = "billId"
}