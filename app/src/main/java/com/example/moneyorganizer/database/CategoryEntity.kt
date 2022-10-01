package com.example.moneyorganizer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String,
    val transactionsNumber: Int = 0,
    val transactionsValue: Double = 0.0,
    val percentage: Double = 0.0
)
