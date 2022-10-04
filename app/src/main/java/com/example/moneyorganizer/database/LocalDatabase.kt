package com.example.moneyorganizer.database

import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                        .addCallback(object: RoomDatabase.Callback(){
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)

                                val carCategory = CategoryEntity(name = "Samochód", color = "#FF0000")
                                val houseCategory = CategoryEntity(name = "Mieszkanie", color = "#FFA500")
                                val mediaCategory = CategoryEntity(name = "Media", color = "#0000FF")
                                val foodCategory = CategoryEntity(name = "Jedzenie", color = "#008000")
                                val shoppingCategory = CategoryEntity(name = "Zakupy", color = "#A0522D")
                                val funCategory = CategoryEntity(name = "Rozrywka", color = "#8e4585")
                                val whimsCategory = CategoryEntity(name = "Zachcianki", color = "#FF69B4")

                                GlobalScope.launch {
                                    instance?.categoryDao()?.insert(carCategory, houseCategory, mediaCategory, foodCategory, shoppingCategory, funCategory, whimsCategory)
                                }
                            }
                        })
                        .build()

                    INSTANCE = instance
                }
                return  instance
            }
        }
    }
}