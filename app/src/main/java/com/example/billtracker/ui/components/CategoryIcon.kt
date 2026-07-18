package com.example.billtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Category


fun iconFor(iconKey: String): ImageVector = when (iconKey) {
    "home" -> Icons.Default.Home
    "bolt" -> Icons.Default.Bolt
    "water_drop" -> Icons.Default.WaterDrop
    "wifi" -> Icons.Default.Wifi
    "phone" -> Icons.Default.PhoneAndroid
    "fitness_center" -> Icons.Default.FitnessCenter
    "credit_card" -> Icons.Default.CreditCard
    "more_horiz" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}

@Composable
fun CategoryIconBox(
    iconKey: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.surfaceVariant.let {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
            }, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconFor(iconKey),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            val isSelected = category.id == selectedCategoryId
            val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Column(
                modifier = Modifier
                    .background(bg, androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                    .clickable { onSelect(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = iconFor(category.iconKey),
                    contentDescription = category.name,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = category.name,
                    color = contentColor,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}