package com.example.moneyorganizer.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyorganizer.R
import com.example.moneyorganizer.adapters.CategoryListItemAdapter
import com.example.moneyorganizer.adapters.GoalsAdapter
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.database.GoalEntity
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.database.TransactionEntity
import com.example.moneyorganizer.databinding.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class GoalsFragment : Fragment() {

    private lateinit var binding: FragmentGoalsBinding

    private var mSelectedColor = ""
    private var mSelectedCategoryId = -1
    private var categories : List<CategoryEntity>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadGoals()
        loadCategories()
    }

    private fun loadGoals(){

        val goalDao = (requireActivity().application as MoneyOrganizerApp).db.goalDao()

        lifecycleScope.launch {

            goalDao.getAllGoals().collect {

                val goalsList = it

                    val adapter = GoalsAdapter(goalsList, requireContext())

                    binding.rvGoals.visibility = View.VISIBLE
                    binding.tvNoGoals.visibility = View.GONE
                    binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvGoals.adapter = adapter

                    adapter.onClickListener = object : GoalsAdapter.OnClickListener {
                        override fun onClick(view: View, goalEntity: GoalEntity?) {
                            when(view.id){
                                R.id.cvAddNewGoal -> {
                                    addGoalDialog()
                                }
                                R.id.btnDepositMoney ->{
                                    showDepositDialog(goalEntity!!)
                                }
                                R.id.btnEndGoal ->{
                                    addTransaction(goalEntity!!)
                                }
                            }

                        }
                    }

                if(goalsList.isEmpty()){
                    binding.tvNoGoals.visibility = View.VISIBLE
                }else{
                    binding.tvNoGoals.visibility = View.GONE
                }

            }
        }
    }

    private fun addGoalDialog(){
        val dialog = Dialog(requireActivity())
        val dialogBinding = AddGoalDialogBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.etCategory.setOnClickListener {
            if(categories != null)
                selectCategory(categories!!, dialogBinding)
        }

        dialogBinding.btnAddGoal.setOnClickListener {
            val goalDescription = dialogBinding.etGoalDescription.text.toString()
            val goalValue = dialogBinding.etGoalValue.text.toString()

            addGoal(goalDescription, goalValue, dialog)
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addGoal(goalDescription: String, value: String, dialog: Dialog){

        val goalDao = (requireActivity().application as MoneyOrganizerApp).db.goalDao()

        lifecycleScope.launch {
            if(validateForm(mSelectedCategoryId, goalDescription, value)){

                val goal = GoalEntity(goal = goalDescription, value = value.toFloat(), valueDeposited = 0F, categoryId = mSelectedCategoryId)
                goalDao.insert(goal)

                Toast.makeText(requireContext(), "Dodano cel", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                mSelectedColor = ""
            }

        }

    }

    private fun loadCategories(){
        lifecycleScope.launch {
            val categoryDao = (activity?.application as MoneyOrganizerApp).db.categoryDao()
            categoryDao.fetchAllCategories().collect {
                categories = it
            }
        }
    }

    private fun selectCategory(categories: List<CategoryEntity>, dialogBinding: AddGoalDialogBinding) {
        val dialog = Dialog(requireContext())
        val categoryBinding = CategoryDialogBinding.inflate(layoutInflater)

        dialog.setContentView(categoryBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val adapter = CategoryListItemAdapter(categories, mSelectedColor)
        categoryBinding.rvCategoriesList.layoutManager = LinearLayoutManager(requireContext())
        categoryBinding.rvCategoriesList.adapter = adapter

        adapter.onClickListener = object : CategoryListItemAdapter.OnClickListener {
            override fun onClick(category: CategoryEntity) {
                dialogBinding.etCategory.setText(category.name)
                dialogBinding.etCategory.setBackgroundColor(Color.parseColor(category.color))
                dialogBinding.etCategory.setTextColor(Color.WHITE)
                dialogBinding.etCategory.background.setTint(Color.parseColor(category.color))
                mSelectedColor = category.color

                mSelectedCategoryId = category.id

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun validateForm(category: Int, description: String, value: String): Boolean {
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
            else -> true
        }
    }

    private fun showDepositDialog(goalEntity: GoalEntity){
        val dialog = Dialog(requireActivity())
        val dialogBinding = DepositMoneyDialogBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialogBinding.tvDepositValue.text = "0 zł"

        val max = goalEntity.value - goalEntity.valueDeposited
        dialogBinding.sbDeposit.max = max.toInt()

        dialogBinding.sbDeposit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    dialogBinding.etDepositValue.setText(progress.toString())
                }
                dialogBinding.tvDepositValue.text = "${progress} zł"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        dialogBinding.etDepositValue.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if(text!!.isNotEmpty()){
                    if(text.toString().toInt() > 0)
                        dialogBinding.sbDeposit.progress = text.toString().toInt()
                    else
                        dialogBinding.sbDeposit.progress = 0
                }
                else
                    dialogBinding.sbDeposit.progress = 0
            }
        })

        dialogBinding.btnDeposit.setOnClickListener {
            val moneyToDeposit = dialogBinding.etDepositValue.text.toString().toInt()
            addDeposit(goalEntity, moneyToDeposit, dialog)
        }

        dialogBinding.btnCancelDeposit.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addDeposit(goalEntity: GoalEntity, moneyToDeposit: Int, dialog: Dialog){

        val goalDao = (requireActivity().application as MoneyOrganizerApp).db.goalDao()

        lifecycleScope.launch {
            val newValueDeposited = goalEntity.valueDeposited + moneyToDeposit
            val goalNewValue = GoalEntity(
                goalId = goalEntity.goalId,
                goal = goalEntity.goal,
                value =  goalEntity.value,
                valueDeposited = newValueDeposited,
                categoryId = goalEntity.categoryId
            )

            goalDao.update(goalNewValue)

            Toast.makeText(requireContext(), "Wpłacono", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun addTransaction(goalEntity: GoalEntity){

        val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()
        val goalDao = (requireActivity().application as MoneyOrganizerApp).db.goalDao()

        lifecycleScope.launch {

            val date = System.currentTimeMillis()
            val transaction = TransactionEntity(description = goalEntity.goal, value = goalEntity.valueDeposited, categoryId = goalEntity.categoryId, date = date)

            transactionDao.insert(transaction)
            Toast.makeText(requireContext(), "Dodano jako transakcję", Toast.LENGTH_SHORT).show()

            val goalCompleted = GoalEntity(
                goalId = goalEntity.goalId,
                goal = goalEntity.goal,
                value = goalEntity.value,
                valueDeposited = goalEntity.valueDeposited,
                completed = true,
                categoryId = goalEntity.categoryId
            )
            goalDao.update(goalCompleted)
        }
    }


}