package com.example.moneyorganizer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(vararg goalEntity: GoalEntity)

    @Update
    suspend fun update(goalEntity: GoalEntity)

    @Delete
    suspend fun delete(goalEntity: GoalEntity)

    @Query("SELECT c.id as c_id, c.name as c_name, c.color as c_color, c.transactionsNumber as c_transactionsNumber, c.transactionsValue as c_transactionsValue, c.percentage as c_percentage,  g.goalId as goalId, g.goal as goal, g.value as value, g.valueDeposited as valueDeposited, g.completed as completed,g.categoryId as categoryId from goals g inner join categories c on g.categoryId = c.id")
    fun getAllGoals(): Flow<List<GoalAndCategory>>


}