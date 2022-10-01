package com.example.moneyorganizer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(vararg categoryEntity: CategoryEntity)

    @Update
    suspend fun update(categoryEntity: CategoryEntity)

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Query("SELECT * from categories where id=:id")
    fun getCategoryById(id: Int): Flow<CategoryEntity>

    @Query("SELECT c.id as id, c.name as name, c.color as color, SUM(t.value) AS transactionsValue, Count(*) AS transactionsNumber, (SUM(t.value) / (SELECT SUM(value) from transactions)) as percentage  from categories c left join transactions t on (c.id = t.categoryId) group by c.id")
    fun fetchAllCategories(): Flow<List<CategoryEntity>>

}