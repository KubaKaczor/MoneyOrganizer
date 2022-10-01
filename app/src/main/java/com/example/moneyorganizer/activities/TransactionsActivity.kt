package com.example.moneyorganizer.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyorganizer.Constants
import com.example.moneyorganizer.R
import com.example.moneyorganizer.adapters.TransactionsAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.databinding.ActivityTransactionsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Constants.CATEGORY_ID)){
            val categoryId = intent.getIntExtra(Constants.CATEGORY_ID, -1)
            loadTransactions(categoryId)
        }

    }

    private fun loadTransactions(categoryId: Int){

        val dao = (application as MoneyOrganizerApp).db.transactionDao()
        val categoryDao = (application as MoneyOrganizerApp).db.categoryDao()

        lifecycleScope.launch {
            //dao.fetchTransactionsByCategory(categoryId).collect{
            dao.fetchTransactionsWithCategory(categoryId).collect{

                val transactionList = it


                if(transactionList.size > 0) {

                    val adapter = TransactionsAdapter(transactionList)

                    binding.rvTransactions.layoutManager = LinearLayoutManager(this@TransactionsActivity)
                    binding.rvTransactions.adapter = adapter
                    binding.rvTransactions.visibility = View.VISIBLE
                    binding.tvNoTransactions.visibility = View.GONE
                }
                else{
                    binding.rvTransactions.visibility = View.GONE
                    binding.tvNoTransactions.visibility = View.VISIBLE
                }

                categoryDao.getCategoryById(categoryId).collect{
                    val category = it
                    setUpActionBar(category)
                }
            }
        }
    }

    private fun setUpActionBar(category: CategoryEntity){
        val toolbar = binding.toolbarTransaction

        if(toolbar != null){
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24))

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            toolbar.title = "Transakcje ${category.name}"
            toolbar.setBackgroundColor(Color.parseColor(category.color))
        }
    }
}