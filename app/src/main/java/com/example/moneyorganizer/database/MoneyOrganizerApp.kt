package com.example.moneyorganizer.database

import android.app.Application

class MoneyOrganizerApp: Application() {
    val db by lazy {
        LocalDatabase.getInstance(this)
    }
}