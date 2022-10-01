package com.example.moneyorganizer.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyorganizer.activities.MainActivity
import com.example.moneyorganizer.database.TransactionEntity
import com.example.moneyorganizer.database.TransactionsAndCategories
import com.example.moneyorganizer.databinding.TransactionItemBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(private val list: List<TransactionsAndCategories>): RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(binding: TransactionItemBinding): RecyclerView.ViewHolder(binding.root){
        val description = binding.tvDescription
        val date = binding.tvDate
        val category = binding.tvCategory
        val value = binding.tvValueItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = list[position]

        val transactionDate = getDateFromMils(transaction.transactions.date)
        holder.description.text = transaction.transactions.description
        holder.date.text = transactionDate
        holder.category.text = transaction.category.name
        holder.value.text = "-${transaction.transactions.value} zł"

        if(position > 0){

            val previousTransactionDate = getDateFromMils(list[position-1].transactions.date)

            if(transactionDate == previousTransactionDate) {
                holder.date.visibility = View.GONE
                holder.description.text = transaction.transactions.description
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getDateFromMils(timeInMils: Long): String {

        val formatter = SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(Date(timeInMils))
    }


    fun findTransaction(position: Int): TransactionEntity{
        val transaction = list[position].transactions
        return transaction
    }
}