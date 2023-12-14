package org.wit.macrocount.models
import androidx.lifecycle.MutableLiveData
import org.wit.macrocount.api.MacroCountClient
import org.wit.macrocount.main.MainApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import timber.log.Timber.Forest.i
import java.time.LocalDate
import java.util.Random

var lastId = 0L

fun generateId(): Long {
    return Random().nextLong()
}

internal fun getId(): Long {
    return lastId++
}
object MacroCountManager: MacroCountStore {

    val macroCounts = ArrayList<MacroCountModel>()

    //private lateinit var app: MainApp

//    override fun findAll(): List<MacroCountModel> {
//        return macroCounts
//    }

    override fun findAll(macroList: MutableLiveData<List<MacroCountModel>>) {

        val call = MacroCountClient.getApi().getall()

        call.enqueue(object : Callback<List<MacroCountModel>> {
            override fun onResponse(call: Call<List<MacroCountModel>>,
                                    response: Response<List<MacroCountModel>>
            ) {
                Timber.i("response: $response")
                macroList.value = response.body() as ArrayList<MacroCountModel>
                Timber.i("Retrofit JSON = ${response.body()}")
            }

            override fun onFailure(call: Call<List<MacroCountModel>>, t: Throwable) {
                Timber.i("Retrofit Error : $t.message")
            }
        })
    }
    override fun findByUserId(id: Long): List<MacroCountModel> {
        return macroCounts.filter { m -> m.userId == id }
    }

    override fun findById(id: Long): MacroCountModel? {
        return macroCounts.find { m -> m.id == id }
    }

    override fun findByIds(ids: List<String>): List<MacroCountModel?> {
        var foundMacros = mutableListOf<MacroCountModel?>()
        ids.forEach { it -> foundMacros.add(macroCounts.find { m -> m.id == it.toLong() })}
        return foundMacros
    }

    override fun findByTitle(title: String): MacroCountModel {
        val foundMacros = macroCounts.filter { m -> m.title == title }
        if (foundMacros.isEmpty()) {
            throw NoSuchElementException("No item with title $title found")
        } else if (foundMacros.size > 1) {
            throw IllegalStateException("Multiple items with title $title found")
        }
        return foundMacros[0]
    }


    override fun create(macroCount: MacroCountModel) {
        macroCount.id = generateId()
        macroCounts.add(macroCount)

        //app.days.addMacroId(macroCount.id, macroCount.userId, LocalDate.now())

        logAll()
    }



    override fun update(macroCount: MacroCountModel) {
        var foundMacroCount: MacroCountModel? = macroCounts.find { m -> m.id == macroCount.id }
        if (foundMacroCount != null) {
            foundMacroCount.title = macroCount.title
            foundMacroCount.description = macroCount.description
            foundMacroCount.calories = macroCount.calories
            foundMacroCount.carbs = macroCount.carbs
            foundMacroCount.protein = macroCount.protein
            foundMacroCount.fat = macroCount.fat

            logAll()
        }
    }

    override fun delete(macroCount: MacroCountModel) {
        var foundMacroCount: MacroCountModel? = macroCounts.find { m -> m.id == macroCount.id }
        if (foundMacroCount != null) {
            macroCounts.remove(macroCount)
        }
    }

    override fun index(macroCount: MacroCountModel): Int {
        var foundMacroCount: MacroCountModel? = macroCounts.find { m -> m.id == macroCount.id }
        if (foundMacroCount != null) {
            return macroCounts.indexOf(macroCount)
        } else {
            return -1
        }
    }

    override fun isUniqueTitle(title: String): Boolean {
        return macroCounts.none { it.title == title }
    }

    private fun logAll() {
        macroCounts.forEach{ i("$it")}
    }
}