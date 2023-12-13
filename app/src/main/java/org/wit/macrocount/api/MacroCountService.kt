package org.wit.macrocount.api

import org.wit.macrocount.models.MacroCountModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MacroCountService {

    @GET("/macrocounts")
    fun getall(): Call<List<MacroCountModel>>

    @GET("/macrocounts/{id}")
    fun get(@Path("id") id: String): Call<MacroCountModel>

    @DELETE("/macrocounts/{id}")
    fun delete(@Path("id") id: String): Call<MacroCountWrapper>

    @POST("/macrocounts")
    fun post(@Body macroCount: MacroCountModel): Call<MacroCountWrapper>

    @PUT("/macrocounts/{id}")
    fun put(@Path("id") id: String,
            @Body macroCount: MacroCountModel
    ): Call<MacroCountWrapper>

}
