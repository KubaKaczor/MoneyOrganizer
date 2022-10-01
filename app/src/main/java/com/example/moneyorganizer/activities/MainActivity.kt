package com.example.moneyorganizer.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyorganizer.R
import com.example.moneyorganizer.adapters.CategoryDetailsAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.databinding.ActivityMainBinding
import com.example.moneyorganizer.fragments.*
import kotlinx.coroutines.launch
import org.eazegraph.lib.models.PieModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //addCategories()

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()

        binding.bottomNavigationView.selectedItemId = R.id.menu_home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_history ->{
                    loadFragment(HistoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.menu_add ->{
                    loadFragment(AddTransactionFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.menu_home ->{
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.menu_goal ->{
                    loadFragment(GoalsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.menu_statistics ->{
                    loadFragment(StatisticsFragment())
                    return@setOnItemSelectedListener true
                }
                else ->return@setOnItemSelectedListener false
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun addCategories(){
        val categoryDao = (application as MoneyOrganizerApp).db.categoryDao()

        val carCategory = CategoryEntity(name = "Samochód", color = "#FF0000")
        val houseCategory = CategoryEntity(name = "Mieszkanie", color = "#FFA500")
        val mediaCategory = CategoryEntity(name = "Media", color = "#0000FF")
        val foodCategory = CategoryEntity(name = "Jedzenie", color = "#008000")
        val shoppingCategory = CategoryEntity(name = "Zakupy", color = "#A0522D")
        val funCategory = CategoryEntity(name = "Rozrywka", color = "#8e4585")
        val whimsCategory = CategoryEntity(name = "Zachcianki", color = "#FF69B4")

        lifecycleScope.launch {
            categoryDao.insert(carCategory, houseCategory, mediaCategory, foodCategory, shoppingCategory, funCategory, whimsCategory)
        }
    }
}