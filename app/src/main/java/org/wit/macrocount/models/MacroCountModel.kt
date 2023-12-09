package org.wit.macrocount.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class MacroCountModel(var id: Long = 0,
                           var title: String = "",
                           var description: String = "",
                           var calories: String = "0",
                           var protein: String = "0",
                           var carbs: String = "0",
                           var fat: String = "0",
                           var userId: Long = 0,
                           var image: Uri = Uri.EMPTY ) : Parcelable
