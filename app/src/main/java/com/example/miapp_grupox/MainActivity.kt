
package com.example.miapp_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.miapp_grupox.ui.theme.MiAppGrupoXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MiAppGrupoXTheme {
                // Aquí debe permanecer tu pantalla o navegación actual.
            }
        }
    }
}