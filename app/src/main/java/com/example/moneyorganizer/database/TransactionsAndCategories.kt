package com.example.moneyorganizer.database

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionsAndCategories(
    @Embedded
    val category: CategoryEntity,
    @Embedded(prefix = "t_")
    val transactions: TransactionEntity
)

