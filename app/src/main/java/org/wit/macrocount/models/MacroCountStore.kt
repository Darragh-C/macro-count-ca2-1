package org.wit.macrocount.models

import androidx.lifecycle.MutableLiveData

interface MacroCountStore {
    fun findAll(macroList: MutableLiveData<List<MacroCountModel>>)
    fun create(macroCount: MacroCountModel)
    fun update(macroCount: MacroCountModel)
    fun delete(id: String)
    fun index(macroCount: MacroCountModel): Int
    fun findByUserId(id: Long): List<MacroCountModel>
    fun findById(id: Long): MacroCountModel?
    fun findByTitle(title: String): MacroCountModel
    fun isUniqueTitle(title: String): Boolean
    fun findByIds(ids: List<String>): List<MacroCountModel?>
}
