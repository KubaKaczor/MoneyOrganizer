package com.example.moneyorganizer.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyorganizer.Constants
import com.example.moneyorganizer.R
import com.example.moneyorganizer.activities.TransactionsActivity
import com.example.moneyorganizer.database.GoalAndCategory
import com.example.moneyorganizer.database.GoalEntity
import com.example.moneyorganizer.databinding.GoalItemBinding
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class GoalsAdapter(private val goalsList: List<GoalAndCategory>,private val context: Context): RecyclerView.Adapter<GoalsAdapter.MyGoalViewHolder>() {

    var onClickListener: GoalsAdapter.OnClickListener? = null

    inner class MyGoalViewHolder(binding: GoalItemBinding): RecyclerView.ViewHolder(binding.root){
        val categoryName = binding.tvGoalCategoryName
        val goalName = binding.tvGoalName
        val goalValue = binding.tvGoalValue
        val addNewGoal = binding.cvAddNewGoal
        val goalDetails = binding.details
        val btnDepositMoney = binding.btnDepositMoney
        val goalProgressBar = binding.goalProgressBar
        val progressPercentage = binding.tvProgressPercentage
        val btnEndGoal = binding.btnEndGoal
        val viewLabel = binding.viewLabel
        val ivCompleted = binding.ivCompleted
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGoalViewHolder {
        val view = GoalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyGoalViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MyGoalViewHolder, position: Int) {

        holder.addNewGoal.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(it, null)
            }
        }

        holder.btnDepositMoney.setOnClickListener {
            if (onClickListener != null) {
                val goalEntity = goalsList[position].goal
                onClickListener!!.onClick(it, goalEntity)
            }
        }

        holder.btnEndGoal.setOnClickListener {
            if (onClickListener != null) {
                val goalEntity = goalsList[position].goal
                onClickListener!!.onClick(it, goalEntity)
            }
        }

        if(goalsList.isNotEmpty()) {

            val model = goalsList[position]

            if (position == goalsList.size - 1)
                holder.addNewGoal.visibility = View.VISIBLE
            holder.categoryName.text = model.category.name
            holder.goalName.text = model.goal.goal

            val dep = model.goal.valueDeposited
            val sum = model.goal.value
            holder.goalValue.text = "${dep.toInt()} / ${sum.toInt()} zł"

            val progress = Math.round(dep / sum * 100)
            holder.goalProgressBar.progress = progress
            holder.progressPercentage.text = "${progress} %"

            if(dep >= sum){
                holder.btnDepositMoney.visibility = View.GONE
                holder.btnEndGoal.visibility = View.VISIBLE
            }else{
                holder.btnDepositMoney.visibility = View.VISIBLE
                holder.btnEndGoal.visibility = View.GONE
            }

            if(model.goal.completed){
                holder.btnEndGoal.visibility = View.GONE
                holder.viewLabel.setBackgroundColor(context.getColor(R.color.foodColor))
                holder.ivCompleted.setColorFilter(context.getColor(R.color.foodColor))
                holder.goalProgressBar.progressTintList = ColorStateList.valueOf(context.getColor(R.color.foodColor))
            }



        }else{
            holder.goalDetails.visibility = View.GONE
            holder.addNewGoal.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        if(goalsList.size == 0)
            return 1
        else
            return goalsList.size
    }


    interface OnClickListener{
        fun onClick(view: View, goalEntity: GoalEntity?)
    }
}