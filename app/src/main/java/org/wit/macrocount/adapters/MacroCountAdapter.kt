package org.wit.macrocount.adapters

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.macrocount.R
import org.wit.macrocount.databinding.CardMacrocountBinding
import org.wit.macrocount.models.MacroCountModel
import retrofit2.Callback

interface MacroCountListener{
    fun onMacroCountClick(macroCount: MacroCountModel)
    fun onMacroDeleteClick(macroCount: MacroCountModel)
    fun onMacroCountEdit(macroCount: MacroCountModel)
    fun handleFavourite(macroCount: MacroCountModel, isFavourite: Boolean)
}
class MacroCountAdapter constructor(private var macroCounts: ArrayList<MacroCountModel>,
                                    private val listener: MacroCountListener,
                                    private val favourites: ArrayList<String>
):

    RecyclerView.Adapter<MacroCountAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMacrocountBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)

    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val macroCount = macroCounts[holder.adapterPosition]
        holder.bind(macroCount, listener, favourites)

    }

    override fun getItemCount(): Int = macroCounts.size

    fun updateData(newData: ArrayList<MacroCountModel>) {
        macroCounts = newData
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        macroCounts.removeAt(position)
        notifyItemRemoved(position)
    }



    class MainHolder(private val binding : CardMacrocountBinding) :
        RecyclerView.ViewHolder(binding.root) {



        private var isFavourite: Boolean = false

        fun bind(macroCount: MacroCountModel, listener: MacroCountListener, favourites: ArrayList<String>) {

            isFavourite = favourites.contains(macroCount.uid)

            if (isFavourite) {
                binding.bookmarkButton.setColorFilter(Color.parseColor("#0E76CC"))
            } else {
                binding.bookmarkButton.setColorFilter(Color.LTGRAY)
            }

            binding.root.tag = macroCount
            binding.macroCounterTitle.text = macroCount.title
            binding.macroCountCalories.text = macroCount.calories + "Kj"
            binding.macroCountProtein.text = "P:" + macroCount.protein + "g"
            binding.macroCountCarbs.text = "C:" + macroCount.carbs + "g"
            binding.macroCountFat.text = "F:" + macroCount.fat + "g"

            binding.root.setOnClickListener {
                listener.onMacroCountClick(macroCount)
            }

            binding.bookmarkButton.setOnClickListener() {
                toggleFavourite() {
                    listener.handleFavourite(macroCount, isFavourite)
                }
            }

        }
        private fun toggleFavourite(callback: (Boolean) -> Unit) {
            val newColor = if (!isFavourite) {
                Color.parseColor("#0E76CC")

            } else {
                Color.LTGRAY
            }
            binding.bookmarkButton.setColorFilter(newColor)
            isFavourite = !isFavourite
            callback(isFavourite)
        }

    }
}