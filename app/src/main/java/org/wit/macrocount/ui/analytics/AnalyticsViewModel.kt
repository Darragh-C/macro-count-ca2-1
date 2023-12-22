package org.wit.macrocount.ui.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.firebase.FirebaseDayManager
import org.wit.macrocount.firebase.FirebaseMacroManager
import org.wit.macrocount.firebase.FirebaseProfileManager
//import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel

import org.wit.macrocount.models.UserModel
import org.wit.macrocount.utils.calcBmr
import org.wit.macrocount.utils.calcProtein
import timber.log.Timber
import java.time.LocalDate
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

    private val calculationsStatus = MutableLiveData<Boolean>()

    private val userid = FirebaseAuth.getInstance().currentUser!!.uid

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    val observableUser: LiveData<UserModel>
        get() = user

    val observableCalculationsStatus: MutableLiveData<Boolean>
        get() = calculationsStatus

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

    val observableCalorieGoal: MutableLiveData<Int>
        get() = calorieGoal

    val observableProteinGoal: MutableLiveData<Int>
        get() = proteinGoal

    val observablemMacroTotals: LiveData<List<PieEntry>>
        get() = macroTotals


    init {
        Timber.i("init block called")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            getUser(currentUser.uid)
            loadDayMacros(currentUser.uid)

        } else {
            Timber.i("User not authenticated")
        }
    }

    fun loadDayMacros(userid: String) {
        Timber.i("analytics loadDayMacros block called")
        //Timber.i("loadDayMacros snapshotCheck state called: ${observableSnapshotCheck.value}")
        Timber.i("analytics loadDayMacros macroList state called: ${macroList.value}")

        Timber.i("analytics Loading macros for $userid")
        FirebaseDayManager.findByUserDate(userid, LocalDate.now()) { dayResult ->
            Timber.i("analytics loadDayMacros findByUserDate Result : $dayResult")
            val todayMacroIds = dayResult?.userMacroIds
            Timber.i("analytics loadDayMacros findByUserDate Result macro ids: $todayMacroIds")

            if (todayMacroIds.isNullOrEmpty()) {
                Timber.i("analytics No macros today")
                macroList.value = ArrayList<MacroCountModel>()
                Timber.i("analytics No macros today list: ${macroList.value}")
            } else {
                val todayMacros = ArrayList<MacroCountModel>()
                Timber.i("analytics Macro ids found for today: $todayMacroIds")
                todayMacroIds.forEach { m ->
                    val macro = MutableLiveData<MacroCountModel>()
                    Timber.i("analytics loadDayMacros finding macro by id: $m")
                    FirebaseMacroManager.asyncFindById(userid, m, macro) {
                            result ->
                        if (result) {
                            Timber.i("analytics loadDayMacros adding found macro to todayMacros: macro: ${macro}, macro.value: ${macro.value}")
                            macro.value?.let {
                                Timber.i("what is it: $it")
                                todayMacros.add(it)
                            }
                            Timber.i("analytics todayMacros: ${todayMacros}")
                            macroList.value = todayMacros
                            Timber.i("analytics loadDayMacros Success : ${macroList.value}")
                        } else {
                            Timber.i("analytics loadDayMacros failed to find macro by id: $m")
                        }
                    }
                }

            }
        }
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
        calculationsStatus.value = true
    }

    fun getUser(userid: String) {
         FirebaseProfileManager.findById(userid) {
             user.value = it
             Timber.i("user.value at analytics = ${user.value}")
         }
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