package com.example.miapp_grupox.data.remote.api

import com.example.miapp_grupox.data.remote.dto.FlushingDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FlushingApi {

    @GET("api/flushing")
    suspend fun listar(): List<FlushingDto>

    @GET("api/flushing/{id}")
    suspend fun obtener(@Path("id") id: Long): FlushingDto

    @POST("api/flushing")
    suspend fun crear(@Body registro: FlushingDto): FlushingDto

    @PUT("api/flushing/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body registro: FlushingDto): FlushingDto

    @DELETE("api/flushing/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}