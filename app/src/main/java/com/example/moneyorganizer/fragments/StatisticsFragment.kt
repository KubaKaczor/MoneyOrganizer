package com.example.moneyorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.moneyorganizer.R
import com.example.moneyorganizer.database.MoneyOrganizerApp
import com.example.moneyorganizer.databinding.FragmentStatisticsBinding
import com.example.moneyorganizer.model.MonthsValue
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.eazegraph.lib.models.BarModel
import java.util.*


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadBarChart()
        loadData()
        loadNumberOfTransactions()
        loadTheMostExpensiveTransaction()
        loadTopCategories()
    }

    private fun loadBarChart(){

        lifecycleScope.launch {

            val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

            transactionDao.getValueByMonth().collect{

                val list = it

                val entries = ArrayList<BarEntry>()

                for(month in 1..12) {

                    var m: MonthsValue? = null
                    if(month < 10)
                        m = list.find { m -> m.month.contains(month.toString()) && m.month.startsWith('0') }
                    else
                        m = list.find { m -> m.month.contains(month.toString())}
                    if(m != null){
                        entries.add(BarEntry(m.value, month - 1))
                    }else{
                        entries.add(BarEntry(0F, month - 1))
                    }
                }

                val bardataset = BarDataSet(entries, "miesiące")

                val  labels = ArrayList<String>();
                labels.add("Styczeń")
                labels.add("Luty")
                labels.add("Marzec")
                labels.add("Kwiecień")
                labels.add("Maj")
                labels.add("Czerwiec")
                labels.add("Lipiec")
                labels.add("Sierpień")
                labels.add("Wrzesień")
                labels.add("Październik")
                labels.add("Listopad")
                labels.add("Grudzień")

                val data = BarData(labels, bardataset)
                binding.chart.setData(data) // set the data and list of labels into chart
                binding.chart.setDescription("Miesięczne wydatki")  // set the description
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
                binding.chart.animateY(1000)


                transactionDao.getOverallValue().collect{
                     val sum = it
                    val avgValue = Math.round(sum/ list.size)
                    binding.avgValue.text = "$avgValue zł"
                }
            }


        }
    }

    private fun loadData(){

        val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

        lifecycleScope.launch {

            transactionDao.getOverallValue().collect{
                binding.overallValue.text = "$it zł"
            }
        }
    }

    private fun loadNumberOfTransactions(){

        val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

        lifecycleScope.launch {

            transactionDao.getNumberOfTransactions().collect{
                binding.transactionsNumber.text = it.toString()
            }
        }
    }

    private fun loadTheMostExpensiveTransaction(){

        val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

        lifecycleScope.launch {

            transactionDao.getTheMostExpensiveTransaction().collect{
                binding.highestTransaction.text = "${it.keys.first()} - ${it.values.first()} zł"
            }
        }
    }

    private fun loadTopCategories(){

        val transactionDao = (requireActivity().application as MoneyOrganizerApp).db.transactionDao()

        lifecycleScope.launch {

            transactionDao.getTopCategories().collect{
                val list = it

                val firstCat = list[0]
                val secondCat = list[1]
                val thirdCat = list[2]

                binding.stCategory.text = "1. ${firstCat.categoryName} - ${firstCat.categoryOverall} zł"
                binding.ndCategory.text = "1. ${secondCat.categoryName} - ${secondCat.categoryOverall} zł"
                binding.rdCategory.text = "1. ${thirdCat.categoryName} - ${thirdCat.categoryOverall} zł"
            }
        }
    }

}