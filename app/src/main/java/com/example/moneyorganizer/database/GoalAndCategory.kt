package com.example.moneyorganizer.database

import androidx.room.Embedded

data class GoalAndCategory(
    @Embedded(prefix = "c_")
    val category: CategoryEntity,
    @Embedded
    val goal: GoalEntity
)
