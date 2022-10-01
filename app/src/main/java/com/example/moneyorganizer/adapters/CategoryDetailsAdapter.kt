package com.example.moneyorganizer.adapters

import android.R.attr.x
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyorganizer.Constants
import com.example.moneyorganizer.activities.TransactionsActivity
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.databinding.CategoryDetailItemBinding


class CategoryDetailsAdapter(val categoriesList: List<CategoryEntity>, val context: Context, val overall: Float): RecyclerView.Adapter<CategoryDetailsAdapter.MyCategoryViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyCategoryViewHolder(binding: CategoryDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        val textDetail = binding.tvCategoryLabel
        val detailCardView = binding.details
        val viewCategory = binding.viewCategory
        val ivAdd = binding.ivAdd
        val categoryDivider = binding.categoryDivider
        val btnShowTransaction = binding.btnShowTransaction
        val transactionsNumber = binding.tvCategoryTransaction
        val moneySum = binding.tvCategoryMoney
        val percentage = binding.tvCategoryLabelPercentage
    }

    fun setOnClickListener(listener: OnClickListener){
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
        val view = CategoryDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
        val category = categoriesList[position]

        if(category.transactionsNumber == 1 && category.transactionsValue == 0.0) {

            holder.transactionsNumber.text = "0"
            holder.moneySum.text = "0.0 zł"
        }
        else {

            holder.transactionsNumber.text = category.transactionsNumber.toString()
            holder.moneySum.text = "${category.transactionsValue} zł"
        }

        val percentage = Math.round(category.percentage * 100).toInt()
        holder.percentage.text = "${percentage}%"

        val color = Color.parseColor(category.color)

        holder.textDetail.text = "${category.name} - "
        holder.detailCardView.strokeColor = color
        holder.viewCategory.setBackgroundColor(color)
        holder.ivAdd.setColorFilter(color)
        holder.categoryDivider.setBackgroundColor(color)
        holder.btnShowTransaction.setBackgroundColor(color)

        holder.ivAdd.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(category.id, category.name)
            }
        }

        holder.btnShowTransaction.setOnClickListener {
            val intent = Intent(context, TransactionsActivity::class.java)
            intent.putExtra(Constants.CATEGORY_ID, category.id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    interface OnClickListener{
        fun onClick(categoryId: Int, name: String)
    }

}