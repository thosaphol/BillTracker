package com.example.billtracker.ui.theme

import androidx.compose.ui.graphics.Color

// ---- Monochrome palette (ตามธีมที่เลือกจาก Stitch) ----
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Gray900 = Color(0xFF1A1A1A)
val Gray600 = Color(0xFF636363)
val Gray400 = Color(0xFF9E9E9E)
val Gray200 = Color(0xFFE0E0E0)
val Gray100 = Color(0xFFF0F0F0)
val Background = Color(0xFFF9F9F9)
val Surface = Color(0xFFFFFFFF)

// ---- Status colors (จำเป็นต้องมีสี ถึงจะแยกสถานะได้ชัด) ----
val StatusPaidBg = Color(0xFFE3F3E5)
val StatusPaidText = Color(0xFF2E7D32)

val StatusUnpaidBg = Color(0xFFFFF1DE)
val StatusUnpaidText = Color(0xFFEF8B1D)

val StatusOverdueBg = Color(0xFFFBE3E3)
val StatusOverdueText = Color(0xFFD32F2F)

// ---- Dark mode surfaces (follow system theme) ----
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)