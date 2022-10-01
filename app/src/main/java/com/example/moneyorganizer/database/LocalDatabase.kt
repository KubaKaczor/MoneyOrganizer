package com.example.moneyorganizer.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [CategoryEntity::class, TransactionEntity::class, GoalEntity::class], version = 2, exportSchema = true, autoMigrations = [
    AutoMigration (from = 1, to = 2)]
)
abstract  class LocalDatabase : RoomDatabase(){

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao

    companion object{

        @Volatile
        private var INSTANCE : LocalDatabase? = null

        fun getInstance(context : Context): LocalDatabase{

            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "locally-database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return  instance
            }
        }
    }
}