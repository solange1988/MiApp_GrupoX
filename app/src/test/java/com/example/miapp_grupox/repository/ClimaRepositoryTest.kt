package com.example.miapp_grupox.repository

import com.example.miapp_grupox.data.remote.api.ClimaApi
import com.example.miapp_grupox.data.remote.dto.ClimaActual
import com.example.miapp_grupox.data.remote.dto.ClimaResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ClimaRepositoryTest {

    @Test
    fun obtenerClimaMelipilla_consultaLasCoordenadasDeMelipilla() = runTest {
        val api = mockk<ClimaApi>()
        val actual = ClimaActual(time = "2026-11-10T00:15", temperatura = 16.9, humedad = 81)
        coEvery { api.obtenerClima(any(), any(), any()) } returns ClimaResponse(actual)
        val repo = ClimaRepository(api)

        val resultado = repo.obtenerClimaMelipilla()

        assertEquals(actual, resultado)
        coVerify { api.obtenerClima(-33.69, -71.21, any()) }
    }
}
