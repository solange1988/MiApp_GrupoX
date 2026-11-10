package com.example.miapp_grupox.repository

import android.util.Log
import com.example.miapp_grupox.api.FlushingApi
import com.example.miapp_grupox.api.FlushingDto
import com.example.miapp_grupox.api.toEntity
import com.example.miapp_grupox.model.FlusingDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MainRepositoryTest {

    private lateinit var dao: FlusingDao
    private lateinit var api: FlushingApi
    private lateinit var repo: MainRepository

    // Un registro de ejemplo (como lo manda el servidor)
    private val dtoEjemplo = FlushingDto(
        id = 5L,
        granja = "Granja Sur",
        galpon = "Galpon 3",
        linea = "Linea 3",
        responsable = "Pedro Rojo",
        motivo = "Mantenimiento",
        observaciones = "todo normal",
        fecha = "2026-11-09"
    )

    @Before
    fun preparar() {
        // Log.e de Android no existe en los tests, lo dejamos "falso"
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        dao = mockk(relaxed = true)
        api = mockk()
        repo = MainRepository(dao, api)
    }

    @After
    fun limpiar() {
        unmockkStatic(Log::class)
    }

    // ---------- guardar ----------

    @Test
    fun guardar_cuandoServidorResponde_guardaEnRoomYDevuelveTrue() = runTest {
        coEvery { api.crear(any()) } returns dtoEjemplo

        val resultado = repo.guardarRegistro(dtoEjemplo.toEntity())

        assertTrue(resultado)
        coVerify(exactly = 1) { dao.insertar(any()) }
    }

    @Test
    fun guardar_cuandoServidorFalla_noGuardaEnRoomYDevuelveFalse() = runTest {
        coEvery { api.crear(any()) } throws RuntimeException("sin conexion")

        val resultado = repo.guardarRegistro(dtoEjemplo.toEntity())

        assertFalse(resultado)
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    // ---------- actualizar ----------

    @Test
    fun actualizar_cuandoServidorResponde_actualizaRoomYDevuelveTrue() = runTest {
        coEvery { api.actualizar(any(), any()) } returns dtoEjemplo

        val resultado = repo.actualizarRegistro(dtoEjemplo.toEntity())

        assertTrue(resultado)
        coVerify(exactly = 1) { dao.actualizar(any()) }
    }

    @Test
    fun actualizar_cuandoServidorFalla_noTocaRoomYDevuelveFalse() = runTest {
        coEvery { api.actualizar(any(), any()) } throws RuntimeException("sin conexion")

        val resultado = repo.actualizarRegistro(dtoEjemplo.toEntity())

        assertFalse(resultado)
        coVerify(exactly = 0) { dao.actualizar(any()) }
    }

    // ---------- eliminar ----------

    @Test
    fun eliminar_cuandoServidorResponde_borraDeRoomYDevuelveTrue() = runTest {
        coEvery { api.eliminar(3L) } returns Response.success(Unit)

        val resultado = repo.eliminarRegistro(3)

        assertTrue(resultado)
        coVerify(exactly = 1) { dao.eliminar(3) }
    }

    @Test
    fun eliminar_cuandoServidorDice404_noBorraDeRoomYDevuelveFalse() = runTest {
        val respuestaError = mockk<Response<Unit>>()
        every { respuestaError.isSuccessful } returns false
        coEvery { api.eliminar(3L) } returns respuestaError

        val resultado = repo.eliminarRegistro(3)

        assertFalse(resultado)
        coVerify(exactly = 0) { dao.eliminar(any()) }
    }

    // ---------- sincronizar ----------

    @Test
    fun sincronizar_cuandoServidorResponde_reemplazaLaCopiaLocal() = runTest {
        coEvery { api.listar() } returns listOf(dtoEjemplo)

        val resultado = repo.sincronizar()

        assertTrue(resultado)
        coVerify(exactly = 1) { dao.eliminarTodos() }
        coVerify(exactly = 1) { dao.insertarTodos(any()) }
    }

    @Test
    fun sincronizar_cuandoServidorFalla_noTocaRoomYDevuelveFalse() = runTest {
        coEvery { api.listar() } throws RuntimeException("sin conexion")

        val resultado = repo.sincronizar()

        assertFalse(resultado)
        coVerify(exactly = 0) { dao.eliminarTodos() }
        coVerify(exactly = 0) { dao.insertarTodos(any()) }
    }
}