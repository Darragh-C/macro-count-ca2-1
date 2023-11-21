package org.wit.macrocount.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.macrocount.databinding.CardMacrocountBinding
import org.wit.macrocount.models.MacroCountModel

interface MacroCountListener{
    fun onMacroCountClick(macroCount: MacroCountModel)
    fun onMacroDeleteClick(macroCount: MacroCountModel)
}
class MacroCountAdapter constructor(private var macroCounts: List<MacroCountModel>,
                                    private val listener: MacroCountListener
):

    RecyclerView.Adapter<MacroCountAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMacrocountBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val macroCount = macroCounts[holder.adapterPosition]
        holder.bind(macroCount, listener)
    }

    override fun getItemCount(): Int = macroCounts.size

    fun updateData(newData: List<MacroCountModel>) {
        macroCounts = newData
        notifyDataSetChanged()
    }

    class MainHolder(private val binding : CardMacrocountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(macroCount: MacroCountModel, listener: MacroCountListener) {
            binding.macroCounterTitle.text = macroCount.title
            binding.macroCountCalories.text = macroCount.calories + "Kj"
            binding.macroCountProtein.text = "ptn:" + macroCount.protein + "g"
            binding.macroCountCarbs.text = "crb:" + macroCount.carbs + "g"
            binding.macroCountFat.text = "fat:" + macroCount.fat + "g"

            binding.liButtonDel.setOnClickListener {
                listener.onMacroDeleteClick(macroCount)
            }

            binding.root.setOnClickListener { listener.onMacroCountClick(macroCount) }

        }


    }
}