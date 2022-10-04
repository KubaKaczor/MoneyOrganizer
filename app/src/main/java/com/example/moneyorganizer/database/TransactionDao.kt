package com.example.moneyorganizer.database

import androidx.room.*
import com.example.moneyorganizer.model.MonthsValue
import com.example.moneyorganizer.model.TopCategoryValue
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity)

    @Update
    suspend fun update(transactionEntity: TransactionEntity)

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId order by value")
    fun fetchTransactionsByCategory(categoryId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT c.id as id, c.name as name, c.color as color, c.transactionsNumber as transactionsNumber, c.transactionsValue as transactionsValue, c.percentage as percentage,t.transactionId as t_transactionId, t.description as t_description, t.value as t_value, t.categoryId as t_categoryId, t.date as t_date FROM transactions t inner join categories c on (t.categoryId = c.id) WHERE t.categoryId = :categoryId order by t.date")
    fun fetchTransactionsWithCategory(categoryId: Int): Flow<List<TransactionsAndCategories>>

    @Query("SELECT c.id as id, c.name as name, c.color as color, c.transactionsNumber as transactionsNumber, c.transactionsValue as transactionsValue, c.percentage as percentage,t.transactionId as t_transactionId, t.description as t_description, t.value as t_value, t.categoryId as t_categoryId, t.date as t_date FROM transactions t inner join categories c on (t.categoryId = c.id) where t.date BETWEEN :startMonth AND :endMonth order by t.date")
    fun fetchAllTransactionsByMonth(startMonth: Long, endMonth: Long): Flow<List<TransactionsAndCategories>>

    @Query("SELECT Case WHEN SUM(value) > 0 THEN SUM(value) else 0 end as suma FROM transactions")
    fun getOverallValue(): Flow<Float>

    @Query("SELECT Count(*) FROM transactions")
    fun getNumberOfTransactions(): Flow<Int>

    @MapInfo(keyColumn = "description", valueColumn = "maxValue")
    @Query("SELECT description , MAX(value) as maxValue FROM transactions")
    fun getTheMostExpensiveTransaction(): Flow<Map<String, Float>>

    @Query("SELECT c.name as categoryName , SUM(t.value) as categoryOverall FROM transactions t inner join categories c on c.id = t.categoryId group by t.categoryId order by categoryOverall desc LIMIT 3")
    fun getTopCategories(): Flow<List<TopCategoryValue>>


    @Query("SELECT strftime('%m',date / 1000, 'unixepoch') as month, Sum(value) as value FROM transactions group by strftime('%m',date / 1000, 'unixepoch')")
    fun getValueByMonth(): Flow<List<MonthsValue>>

}