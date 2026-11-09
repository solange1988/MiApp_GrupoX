package com.example.miapp_grupox.data.remote.api

import com.example.miapp_grupox.data.remote.dto.LineaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InstalacionesApi {

    @GET("api/lineas")
    suspend fun listar(
        @Query("granja") granja: String? = null,
        @Query("galpon") galpon: String? = null
    ): List<LineaDto>

    @GET("api/lineas/{id}")
    suspend fun obtener(@Path("id") id: Long): LineaDto

    @GET("api/lineas/granjas")
    suspend fun granjas(): List<String>

    @GET("api/lineas/galpones")
    suspend fun galpones(@Query("granja") granja: String): List<String>

    @POST("api/lineas")
    suspend fun crear(@Body linea: LineaDto): LineaDto

    @PUT("api/lineas/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body linea: LineaDto): LineaDto

    @DELETE("api/lineas/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}