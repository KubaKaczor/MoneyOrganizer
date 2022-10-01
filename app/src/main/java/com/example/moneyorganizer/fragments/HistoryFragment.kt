package com.example.moneyorganizer.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyorganizer.adapters.CategoryListItemAdapter
import com.example.moneyorganizer.adapters.TransactionsAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.database.TransactionEntity
import com.example.moneyorganizer.databinding.*
import com.example.moneyorganizer.utils.SwipeToDeleteCallback
import com.example.moneyorganizer.utils.SwipeToEditCallback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    private var mMonth = -1
    private var mLastDay = 0

    private var editTransactionTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.etDate.setOnClickListener {
            clickDatePicker()
        }

        binding.ivLowerMonth.setOnClickListener {
            decreaseMonth()
        }

        binding.ivUpperMonth.setOnClickListener {
            increaseMonth()
        }

        mMonth = Calendar.getInstance().get(Calendar.MONTH)
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, mMonth)
        mLastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        binding.etDate.setText("${mMonth + 1}/${cal.get(Calendar.YEAR)}")

        loadTransactions(mMonth, mLastDay)
    }

    private fun loadTransactions(month: Int, lastDay: Int){

        val startDate = getTimeInMils(month, 1)
        val endDate = getTimeInMils(month,lastDay)

        val dao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

        lifecycleScope.launch {
            dao.fetchAllTransactionsByMonth(startDate, endDate).collect{

                val transactionList = it


                if(transactionList.isNotEmpty()) {

                    val adapter = TransactionsAdapter(transactionList)

                    binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvHistory.adapter = adapter
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.tvNoHistoryTransactions.visibility = View.GONE
                }
                else{
                    binding.rvHistory.visibility = View.GONE
                    binding.tvNoHistoryTransactions.visibility = View.VISIBLE
                }
            }
        }

        val editSwipeHandler = object: SwipeToEditCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHistory.adapter as TransactionsAdapter
                val transaction = adapter.findTransaction(viewHolder.adapterPosition)

                showEditDialog(transaction)
                //this.clearView(binding.rvHistory, viewHolder)
                loadTransactions(mMonth, mLastDay)

            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHistory)


        val deleteSwipeHandler = object: SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHistory.adapter as TransactionsAdapter
                val transaction = adapter.findTransaction(viewHolder.adapterPosition)

                val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()
                lifecycleScope.launch {

                    transactionDao.delete(transaction)
                    Toast.makeText(requireContext(), "Usunięto transakcję", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val deleteItemTouchHandler = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHandler.attachToRecyclerView(binding.rvHistory)
    }

    private fun getTimeInMils(month: Int, day: Int): Long{
        val cal = Calendar.getInstance()

        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.timeInMillis
    }

    private fun clickDatePicker(){
        val myCalendar = Calendar.getInstance()

        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, _ ->

                val selectedDate = "${selectedMonth + 1}/$selectedYear"

                binding.etDate.setText(selectedDate)

                mMonth = selectedMonth
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, mMonth)
                mLastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                loadTransactions(mMonth, mLastDay)
            },
            year,
            month,
            day
        )
        //dpd.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun decreaseMonth(){
        mMonth -= 1
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, mMonth)
        mLastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        binding.etDate.setText("${mMonth + 1}/${cal.get(Calendar.YEAR)}")

        loadTransactions(mMonth, mLastDay)
    }

    private fun increaseMonth(){
        mMonth += 1
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, mMonth)
        mLastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        binding.etDate.setText("${mMonth + 1}/${cal.get(Calendar.YEAR)}")

        loadTransactions(mMonth, mLastDay)
    }

    private fun showEditDialog(transaction: TransactionEntity){
        val dialog = Dialog(requireActivity())
        val binding = EditTransactionDialogBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.etValueTransaction.setText(transaction.value.toString())
        binding.etDescriptionTransaction.setText(transaction.description)

        val formatter = SimpleDateFormat("dd/MM/yyyy");
        val data = formatter.format(Date(transaction.date))

        editTransactionTime = transaction.date
        binding.etDate.setText(data.toString())

        binding.btnUpdate.setOnClickListener {
            if(updateTransaction(binding, transaction))
                dialog.dismiss()
        }

        binding.etDate.setOnClickListener {
            selectDate(binding.etDate)
        }

        binding.btnDismiss.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun selectDate(etDate: EditText) {

        val myCalendar = Calendar.getInstance()

        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)
                theDate?.let {

                    editTransactionTime = theDate.time.milliseconds.inWholeMilliseconds
                    etDate.setText(selectedDate)
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

    private fun updateTransaction(binding: EditTransactionDialogBinding, transactionEdited: TransactionEntity): Boolean{
        val description = binding.etDescriptionTransaction!!.text.toString()
        val value = binding.etValueTransaction!!.text.toString()
        val date = editTransactionTime

        if(validateForm(description, value, editTransactionTime)) {
            val dao = (activity?.application as MoneyOrganizerApp).db.transactionDao()
            lifecycleScope.launch {
                val transaction = TransactionEntity(
                    transactionId = transactionEdited.transactionId,
                    description = description,
                    value = value.toFloat(),
                    date = date,
                    categoryId = transactionEdited.categoryId)

                dao.update(transaction)

                Toast.makeText(
                    requireContext(),
                    "Transakcja edytowana",
                    Toast.LENGTH_SHORT
                ).show()

            }
            return true
        }
        else
            return false
    }

    private fun validateForm(description: String, value: String, date: Long): Boolean {
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