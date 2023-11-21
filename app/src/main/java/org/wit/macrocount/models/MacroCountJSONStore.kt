package org.wit.macrocount.models
import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.wit.macrocount.helpers.exists
import org.wit.macrocount.helpers.read
import org.wit.macrocount.helpers.write
import org.wit.macrocount.main.MainApp
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDate
import java.util.Random

private const val JSON_FILE = "macrocounts.json"
private val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
private val listType: Type = object : TypeToken<ArrayList<MacroCountModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class MacroCountJSONStore(private val context: Context) : MacroCountStore {

    var macroCounts = mutableListOf<MacroCountModel>()
    private val app: MainApp by lazy {
        context.applicationContext as MainApp
    }


    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<MacroCountModel> {
        logAll()
        return macroCounts
    }

    override fun findByUserId(id: Long): List<MacroCountModel> {
        return macroCounts.filter { m -> m.userId == id }
    }

    override fun findById(id: Long): MacroCountModel? {
        return macroCounts.find { m -> m.id == id }
    }

    override fun findByIds(ids: List<String>): List<MacroCountModel> {
        var foundMacros = mutableListOf<MacroCountModel?>()
        ids.forEach { it -> foundMacros.add(macroCounts.find { m -> m.id == it.toLong() })}
        return foundMacros.filterNotNull().toList()
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
        macroCount.id = generateRandomId()
        macroCounts.add(macroCount)
        serialize()
        val today = LocalDate.now()
        app.days.addMacroId(macroCount.id, macroCount.userId, today)
        Timber.i("Created date on $today")
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
            foundMacroCount.userId = macroCount.userId
            foundMacroCount.image = macroCount.image

            serialize()

            //logAll()
        }
    }

    override fun delete(macroCount: MacroCountModel) {
        var foundMacroCount: MacroCountModel? = macroCounts.find { m -> m.id == macroCount.id }
        if (foundMacroCount != null) {
            macroCounts.remove(macroCount)
        }
        serialize()
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

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(macroCounts, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        macroCounts = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        macroCounts.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}