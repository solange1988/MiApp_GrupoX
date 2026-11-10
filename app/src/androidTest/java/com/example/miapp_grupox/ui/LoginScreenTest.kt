package com.example.miapp_grupox.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.miapp_grupox.model.FlusingEntity
import com.example.miapp_grupox.ui.theme.FlusingScreen
import com.example.miapp_grupox.ui.theme.MiAppGrupoXTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FlushingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val registro = FlusingEntity(
        id = 7,
        granja = "Granja Sur",
        galpon = "Galpon 3",
        linea = "Linea 3",
        responsable = "Pedro Rojo",
        motivo = "Mantenimiento",
        observaciones = "todo normal",
        fecha = "09/11/2026 17:31"
    )

    @Test
    fun historial_muestraElRegistroGuardado() {
        composeRule.setContent {
            MiAppGrupoXTheme {
                FlusingScreen(
                    registros = listOf(registro),
                    onGuardar = {},
                    onEliminar = {},
                    onActualizarObservacion = { _, _ -> },
                    onVolver = {}
                )
            }
        }

        // El historial está debajo del formulario: se baja hasta encontrarlo
        composeRule.onNodeWithTag("lista_flushing")
            .performScrollToNode(hasText("Granja Sur · Galpon 3"))

        composeRule.onNodeWithText("Granja Sur · Galpon 3").assertIsDisplayed()
    }

    @Test
    fun eliminar_confirmaYAvisaConElIdDelRegistro() {
        var idEliminado: Int? = null

        composeRule.setContent {
            MiAppGrupoXTheme {
                FlusingScreen(
                    registros = listOf(registro),
                    onGuardar = {},
                    onEliminar = { idEliminado = it },
                    onActualizarObservacion = { _, _ -> },
                    onVolver = {}
                )
            }
        }

        composeRule.onNodeWithTag("lista_flushing")
            .performScrollToNode(hasTestTag("boton_eliminar_7"))
        composeRule.onNodeWithTag("boton_eliminar_7").performClick()
        composeRule.onNodeWithTag("confirmar_eliminar").performClick()

        composeRule.runOnIdle {
            assertEquals(7, idEliminado)
        }
    }
}

