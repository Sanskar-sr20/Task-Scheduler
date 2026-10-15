package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import com.example.ui.theme.CategoryAssignment
import com.example.ui.theme.CategoryExam
import com.example.ui.theme.CategoryOther
import com.example.ui.theme.CategoryPersonal
import com.example.ui.theme.CategoryPractical
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium

@Composable
fun PriorityBadge(priority: TaskPriority, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority) {
        TaskPriority.HIGH -> Triple(PriorityHigh.copy(alpha = 0.15f), PriorityHigh, "High Priority")
        TaskPriority.MEDIUM -> Triple(PriorityMedium.copy(alpha = 0.15f), PriorityMedium, "Medium")
        TaskPriority.LOW -> Triple(PriorityLow.copy(alpha = 0.15f), PriorityLow, "Low")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryBadge(category: TaskCategory, modifier: Modifier = Modifier) {
    val color = when (category) {
        TaskCategory.ASSIGNMENT -> CategoryAssignment
        TaskCategory.PRACTICAL -> CategoryPractical
        TaskCategory.EXAM_PREPARATION -> CategoryExam
        TaskCategory.PERSONAL -> CategoryPersonal
        TaskCategory.OTHER -> CategoryOther
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = category.displayName,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        TaskStatus.COMPLETED -> Pair(Color(0xFF2D6A4F).copy(alpha = 0.15f), Color(0xFF1B4D3E))
        TaskStatus.IN_PROGRESS -> Pair(Color(0xFF028090).copy(alpha = 0.15f), Color(0xFF028090))
        TaskStatus.PENDING -> Pair(Color(0xFFD97706).copy(alpha = 0.15f), Color(0xFFB45309))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
