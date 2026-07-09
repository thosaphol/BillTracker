package com.example.billtracker.domain.model

data class Category(
    val id:Int,
    val name:String,
    val iconKey:String,
    val isCustom: Boolean
)
