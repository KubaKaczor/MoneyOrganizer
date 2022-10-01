package com.example.moneyorganizer.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyorganizer.adapters.CategoryDetailsAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.database.TransactionEntity
import com.example.moneyorganizer.databinding.DialogAddTransactionBinding
import com.example.moneyorganizer.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCategories()
    }

    private fun loadCategories(){
        val transactionDao = (activity?.application as MoneyOrganizerApp).db.transactionDao()

        var overral = 0.0.toFloat()

//        lifecycleScope.launch {
//            transactionDao.getOverallValue().collect{
//                overral = it
//            }
//        }

        binding.piechart.startAnimation()

        binding.piechart.setOnClickListener {

        }

        val categoryDao = (activity?.application as MoneyOrganizerApp).db.categoryDao()


        lifecycleScope.launch {

            categoryDao.fetchAllCategories().collect{

                val categories = it

                fillChart(categories)

                val adapter = CategoryDetailsAdapter(categories, requireContext(), overral)
                binding.rvDetails.layoutManager = LinearLayoutManager(activity)
                binding.rvDetails.adapter = adapter

                adapter.setOnClickListener(object : CategoryDetailsAdapter.OnClickListener{
                    override fun onClick(categoryId: Int, name: String) {
                        quickAddTransactionDialog(categoryId, name)
                    }
                })
            }
        }
    }

    private fun quickAddTransactionDialog(categoryId: Int, name: String){
        val dialog = Dialog(requireActivity())
        val binding = DialogAddTransactionBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvCategoryName!!.text = name

        binding.btnQuickTransactionAdd.setOnClickListener {

            val description = binding.etDescriptionTransaction!!.text.toString()
            val value = binding.etValueTransaction!!.text.toString()
            val date = getDate()

            if(validateForm(description, value)) {
                val dao = (activity?.application as MoneyOrganizerApp).db.transactionDao()
                lifecycleScope.launch {
                    val transaction = TransactionEntity(description = description, value = value.toFloat(), categoryId = categoryId, date = date)
                    dao.insert(transaction)

                    Toast.makeText(
                        requireContext(),
                        "Dodano transakcję",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
            }

        }

        binding.btnQuickTransactionCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun validateForm(description: String, value: String): Boolean {
        return when {
            TextUtils.isEmpty(description) -> {
                Toast.makeText(
                    requireContext(),
                    "Podaj opis",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(value) -> {
                Toast.makeText(
                    requireContext(),
                    "Podaj cenę",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }

    private fun getDate(): Long{

        val currentTime = System.currentTimeMillis()
        return currentTime
    }

    private fun fillChart(categories: List<CategoryEntity>){

        binding.piechart.clearChart()
        for(category in categories){
            val percentage = category.percentage * 100
            val pieModel = PieModel(category.name, percentage.toFloat(), Color.parseColor(category.color))
            binding.piechart.addPieSlice(pieModel)
        }
    }
}