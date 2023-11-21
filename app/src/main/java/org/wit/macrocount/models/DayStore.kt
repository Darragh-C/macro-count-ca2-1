package org.wit.macrocount.models

import java.time.LocalDate

interface DayStore {

    fun findAll(): List<DayModel>
    fun findByUserId(id: Long): List<DayModel>
    fun create(day: DayModel)
    fun addMacroId(macroId: Long, userId: Long, date: LocalDate)
    fun findByUserDate(id: Long, date: LocalDate): DayModel?
    fun update(day: DayModel)
    fun removeMacro(userId: Long, date: String, macroId: String)
}