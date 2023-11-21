package org.wit.macrocount.models
import java.time.LocalDate
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class DayModel(
    var date: String = "",
    var userId: Long = 0,
    var userMacroIds: List<String> = emptyList<String>()) : Parcelable