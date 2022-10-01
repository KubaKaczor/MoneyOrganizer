package com.example.moneyorganizer.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyorganizer.database.CategoryEntity
import com.example.moneyorganizer.databinding.CategoryDetailItemBinding
import com.example.moneyorganizer.databinding.ItemLabelColorBinding
import java.lang.Exception

class CategoryListItemAdapter(private val categoriesList: List<CategoryEntity>, val selectedColor: String): RecyclerView.Adapter<CategoryListItemAdapter.MyCategoryItemViewHolder>() {

    var onClickListener: CategoryListItemAdapter.OnClickListener? = null

    inner class MyCategoryItemViewHolder(binding: ItemLabelColorBinding): RecyclerView.ViewHolder(binding.root){
        val categoryName = binding.viewCategoryName
        val selected = binding.ivSelectedColor
        val viewMain = binding.viewMain
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryItemViewHolder {
        val view = ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCategoryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCategoryItemViewHolder, position: Int) {
        val category = categoriesList[position]

        holder.categoryName.text = category.name
        holder.viewMain.setBackgroundColor(Color.parseColor(category.color))

        if(selectedColor == category.color){
            holder.selected.visibility = View.VISIBLE
        }
        else{
            holder.selected.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(category)
            }
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    interface OnClickListener{
        fun onClick(category: CategoryEntity)
    }


}