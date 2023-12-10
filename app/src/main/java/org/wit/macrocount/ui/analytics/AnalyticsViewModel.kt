package org.wit.macrocount.ui.analytics

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserManager
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.helpers.calcBmr
import org.wit.macrocount.helpers.calcProtein
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.UserRepo
import kotlin.math.roundToInt

class AnalyticsViewModel: ViewModel() {

    private val macroList = MutableLiveData<List<MacroCountModel>>()
    private val user = MutableLiveData<UserModel>()
    private val dailyCalories = MutableLiveData<Int>()
    private val dailyProtein = MutableLiveData<Int>()
    private val dailyCarbs = MutableLiveData<Int>()
    private val dailyFat = MutableLiveData<Int>()
    private val calorieGoal = MutableLiveData<Int>()
    private val proteinGoal = MutableLiveData<Int>()
    private val caloriesProgress = MutableLiveData<Int?>()
    private val proteinProgress = MutableLiveData<Int?>()
    private val calorieFraction = MutableLiveData<String>()
    private val proteinFraction = MutableLiveData<String>()
    private val macroTotals = MutableLiveData<List<PieEntry>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    val observableUser: LiveData<UserModel>
        get() = user

    val observableCalorieTotal: MutableLiveData<Int>
        get() = dailyCalories

    val observableProteinTotal: MutableLiveData<Int>
        get() = dailyProtein

    val observableCarbsTotal: MutableLiveData<Int>
        get() = dailyCarbs

    val observableFatTotal: MutableLiveData<Int>
        get() = dailyFat

    val observableCalorieProgress: MutableLiveData<Int?>
        get() = caloriesProgress

    val observableProteinProgress: LiveData<Int?>
        get() = proteinProgress

    val observableCalorieFraction: LiveData<String>
        get() = calorieFraction

    val observableProteinFraction: LiveData<String>
        get() = proteinFraction

    val observablemMcroTotals: LiveData<List<PieEntry>>
        get() = macroTotals


    init {
        load()
    }

    fun load() {
        macroList.value = MacroCountManager.findAll()
    }

    fun runCalculations() {
        calcDailyCalories()
        calcDailyProtein()
        calcDailyCarbs()
        calcDailyFat()
        calcCalorieGoal()
        calcProteinGoal()
        concatProteinFraction()
        concatCalorieFraction()
        calcCaloriesProgress()
        calcProteinProgress()
    }

    fun getUser(id: Long) {
         user.value = UserManager.findById(id)
    }

    fun calcDailyCalories() {
        dailyCalories.value = macroList.value!!.sumOf {it.calories.toInt()}
    }

    fun calcDailyProtein() {
        dailyProtein.value = macroList.value!!.sumOf {it.protein.toInt()}
    }

    fun calcDailyCarbs() {
        dailyCarbs.value = macroList.value!!.sumOf {it.carbs.toInt()}
    }

    fun calcDailyFat() {
        dailyFat.value = macroList.value!!.sumOf {it.fat.toInt()}
    }

    fun calcCalorieGoal() {
        calorieGoal.value = calcBmr(
            user.value?.weight?.toInt()!!,
            user.value?.height?.toInt()!!,
            user.value?.age?.toInt()!!,
            user.value?.goal!!
        )
    }

    fun calcProteinGoal() {
        proteinGoal.value = calcProtein(
            user.value?.weight?.toInt()!!,
            user.value?.goal!!
        )
    }

    fun concatProteinFraction() {
        proteinFraction.value = "${dailyProtein.value}/${proteinGoal.value}"
    }

    fun concatCalorieFraction() {
        calorieFraction.value = "${dailyCalories.value}/${calorieGoal.value}"
    }

    fun calcCaloriesProgress() {
        val progress = ((dailyCalories.value?.toDouble()?.div(calorieGoal.value?.toDouble()!!))?.times(
            100
        ))?.roundToInt()
        caloriesProgress.value = progress
    }

    fun calcProteinProgress() {
        val progress = ((dailyProtein.value?.toDouble()?.div(proteinGoal.value?.toDouble()!!))?.times(
            100
        ))?.roundToInt()
        proteinProgress.value = progress
    }

}