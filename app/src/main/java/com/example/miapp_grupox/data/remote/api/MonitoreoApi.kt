package com.example.miapp_grupox.data.remote.api

import com.example.miapp_grupox.data.remote.dto.AlertaDto
import com.example.miapp_grupox.data.remote.dto.MedicionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MonitoreoApi {

    @GET("api/mediciones")
    suspend fun listarMediciones(): List<MedicionDto>

    @GET("api/mediciones/linea/{lineaId}")
    suspend fun historico(@Path("lineaId") lineaId: Long): List<MedicionDto>

    @POST("api/mediciones")
    suspend fun crearMedicion(@Body medicion: MedicionDto): MedicionDto

    @PUT("api/mediciones/{id}")
    suspend fun actualizarMedicion(@Path("id") id: Long, @Body medicion: MedicionDto): MedicionDto

    @DELETE("api/mediciones/{id}")
    suspend fun eliminarMedicion(@Path("id") id: Long): Response<Unit>

    @GET("api/alertas")
    suspend fun alertas(@Query("nivel") nivel: String? = null): List<AlertaDto>
}