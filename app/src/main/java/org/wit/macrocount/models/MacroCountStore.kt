package org.wit.macrocount.models

interface MacroCountStore {
    fun findAll(): List<MacroCountModel>
    fun create(macroCount: MacroCountModel)
    fun update(macroCount: MacroCountModel)
    fun delete(macroCount: MacroCountModel)
    fun index(macroCount: MacroCountModel): Int
    fun findByUserId(id: Long): List<MacroCountModel>
    fun findById(id: Long): MacroCountModel?
    fun findByTitle(title: String): MacroCountModel
    fun isUniqueTitle(title: String): Boolean
    fun findByIds(ids: List<String>): List<MacroCountModel?>
}