package com.example.miapp_grupox.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    // ---------- estado de una línea ----------

    @Test
    fun estadoLinea_segunTemperatura() {
        assertEquals("Normal", linea(26.9).estado)
        assertEquals("Advertencia", linea(27.0).estado)
        assertEquals("Crítico", linea(30.0).estado)
    }

    // ---------- rangos de temperatura ----------

    @Test
    fun rangos_etiquetaSegunTemperatura() {
        val etiquetas = RangosTemperatura.etiquetas
        assertEquals(etiquetas[0], RangosTemperatura.etiquetaDe(18.0))
        assertEquals(etiquetas[1], RangosTemperatura.etiquetaDe(23.0))
        assertEquals(etiquetas[2], RangosTemperatura.etiquetaDe(28.5))
        assertEquals(etiquetas[3], RangosTemperatura.etiquetaDe(32.0))
    }

    @Test
    fun rangos_valorDeEtiquetaValidaEInvalida() {
        assertEquals(23.0, RangosTemperatura.valorDe(RangosTemperatura.etiquetas[1])!!, 0.0)
        assertNull(RangosTemperatura.valorDe("no existe"))
    }

    // ---------- validador de temperatura ----------

    @Test
    fun validadorTemperatura_sinRango_esInvalido() {
        val resultado = ValidadorTemperatura.validar("")
        assertFalse(resultado.valido)
        assertTrue(resultado.errores.containsKey("rango"))
    }

    @Test
    fun validadorTemperatura_conRango_esValido() {
        assertTrue(ValidadorTemperatura.validar(RangosTemperatura.etiquetas[3]).valido)
    }

    // ---------- validador de flushing ----------

    @Test
    fun validadorFlushing_datosCompletos_esValido() {
        val resultado = ValidadorFlushing.validar("Pedro Rojo", "Mantenimiento", "Todo normal", true)
        assertTrue(resultado.valido)
    }

    @Test
    fun validadorFlushing_vacio_marcaTodosLosCampos() {
        val resultado = ValidadorFlushing.validar("", "", "", false)
        assertEquals(
            setOf("responsable", "motivo", "observaciones", "foto"),
            resultado.errores.keys
        )
    }

    @Test
    fun validadorFlushing_nombreConNumeros_esInvalido() {
        val resultado = ValidadorFlushing.validar("Pedro123", "Mantenimiento", "Todo normal", true)
        assertEquals("El nombre solo puede tener letras", resultado.errores["responsable"])
    }

    @Test
    fun validadorFlushing_observacionCorta_esInvalida() {
        assertEquals(
            "La observación debe tener al menos 5 caracteres",
            ValidadorFlushing.validarObservacion("ok")
        )
    }

    // ---------- login ----------

    @Test
    fun login_operarioCorrecto_ingresaConPerfilOperario() {
        val resultado = LoginValidator.validar("operario", "1234")
        assertTrue(resultado.exitoso)
        assertEquals("operario", resultado.perfil)
    }

    @Test
    fun login_supervisorCorrecto_ingresaConPerfilSupervisor() {
        assertEquals("supervisor", LoginValidator.validar("Supervisor ", "1234").perfil)
    }

    @Test
    fun login_claveIncorrecta_noIngresa() {
        val resultado = LoginValidator.validar("operario", "9999")
        assertFalse(resultado.exitoso)
        assertEquals("Usuario o contraseña incorrectos", resultado.mensaje)
    }

    @Test
    fun login_camposVacios_marcaErrores() {
        val resultado = LoginValidator.validar("", "")
        assertEquals("Ingresa tu usuario", resultado.errorUsuario)
        assertEquals("Ingresa tu contraseña", resultado.errorClave)
    }

    private fun linea(temperatura: Double) =
        LineBebedero(1, "Granja", "Galpón", "Línea", temperatura, "fecha")
}
