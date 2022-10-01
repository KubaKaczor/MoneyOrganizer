package com.example.moneyorganizer.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyorganizer.R
import com.example.moneyorganizer.adapters.CategoryListItemAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.database.TransactionEntity
import com.example.moneyorganizer.databinding.CategoryDialogBinding
import com.example.moneyorganizer.databinding.FragmentAddTransactionBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


class AddTransactionFragment : Fragment() {

    private lateinit var binding: FragmentAddTransactionBinding

    private var mSelectedColor = ""
    private var mSelectedCategoryId = -1
    private var mDateInMils = 0L

    private var inserted = false

    private var categories : List<CategoryEntity>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCategories()

        binding.etCategory.setOnClickListener {
            if(categories != null)
                selectCategory(categories!!)
        }

        binding.btnDate.setOnClickListener {
            clickDatePicker()
        }

        binding.btnAdd.setOnClickListener {
            addTransaction()
        }
    }

    private fun selectCategory(categories: List<CategoryEntity>) {
        val dialog = Dialog(requireContext())
        val dialogBinding = CategoryDialogBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val adapter = CategoryListItemAdapter(categories, mSelectedColor)
        dialogBinding.rvCategoriesList.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.rvCategoriesList.adapter = adapter

        adapter.onClickListener = object : CategoryListItemAdapter.OnClickListener {
            override fun onClick(category: CategoryEntity) {
                binding.etCategory.setText(category.name)
                binding.etCategory.setBackgroundColor(Color.parseColor(category.color))
                binding.etCategory.setTextColor(Color.WHITE)
                binding.etCategory.background.setTint(Color.parseColor(category.color))
                mSelectedColor = category.color

                mSelectedCategoryId = category.id

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun loadCategories(){
        lifecycleScope.launch {
            val categoryDao = (activity?.application as MoneyOrganizerApp).db.categoryDao()
            categoryDao.fetchAllCategories().collect {
                categories = it
            }
        }
    }

    private fun addTransaction() {

        val value = binding.etValueTransaction.text.toString()
        val description = binding.etDescriptionTransaction.text.toString()


        if(validateForm(mSelectedCategoryId, description, value, mDateInMils)){

            val dao = (activity?.application as MoneyOrganizerApp).db.transactionDao()
            lifecycleScope.launch {
                val transaction = TransactionEntity(description = description, value = value.toFloat(), categoryId = mSelectedCategoryId, date = mDateInMils)
                dao.insert(transaction)

                inserted = true

                Toast.makeText(
                    requireContext(),
                    "Dodano transakcję",
                    Toast.LENGTH_SHORT
                ).show()



                //clearForm()
                val bottomToolbar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomToolbar.selectedItemId = R.id.menu_home

                val transactionHome = requireActivity().supportFragmentManager.beginTransaction()
                transactionHome.replace(R.id.fragment_container, HomeFragment())
                transactionHome.addToBackStack(null)
                transactionHome.commit()
            }
        }
    }

    private fun clickDatePicker() {

        val myCalendar = Calendar.getInstance()

        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"

                binding.btnDate.setText(selectedDate)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)
                theDate?.let {

                    mDateInMils = theDate.time.milliseconds.inWholeMilliseconds

                }
            },
            year,
            month,
            day
        )
        //dpd.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun validateForm(category: Int, description: String, value: String, date: Long): Boolean {
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
            category == -1 -> {
                Toast.makeText(
                    requireContext(),
                    "Wybierz kategorię",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            date == 0L -> {
                Toast.makeText(
                    requireContext(),
                    "Podaj datę",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }

}