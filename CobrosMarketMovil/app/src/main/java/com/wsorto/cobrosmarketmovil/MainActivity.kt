package com.wsorto.cobrosmarketmovil

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase
import com.wsorto.cobrosmarketmovil.data.entities.Usuario
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante
import com.wsorto.cobrosmarketmovil.data.entities.Cobro
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar base de datos
        database = AppDatabase.getDatabase(this)

        // Botón de prueba
        findViewById<Button>(R.id.btnPrueba).setOnClickListener {
            pruebasCRUD()
        }
    }

    private fun pruebasCRUD() {
        lifecycleScope.launch {
            try {
                // 1. INSERTAR USUARIO
                val usuario = Usuario(
                    nombre_usuario = "Rosa Martinez",
                    cargo = "cobrador",
                    fecha_creacion = obtenerFechaActual()
                )
                val idUsuario = database.usuarioDao().insertar(usuario)
                mostrarMensaje("Usuario insertado con ID: $idUsuario")

                // 2. INSERTAR COMERCIANTE
                val comerciante = Comerciante(
                    numero_puesto = "A-001",
                    nombre_comerciante = "Juan Pérez",
                    telefono_comerciante = "7777-7777",
                    fecha_registro = obtenerFechaActual()
                )
                val idComerciante = database.comercianteDao().insertar(comerciante)
                mostrarMensaje("Comerciante insertado con ID: $idComerciante")

                // 3. INSERTAR COBRO
                val cobro = Cobro(
                    id_comerciante = idComerciante.toInt(),
                    id_usuario = idUsuario.toInt(),
                    fecha_cobro = obtenerFechaActual(),
                    monto_cobrado = 5.00,
                    dinero_recibido = 10.00,
                    vuelto_entregado = 5.00,
                    estado = "completado"
                )
                val idCobro = database.cobroDao().insertar(cobro)
                mostrarMensaje("Cobro insertado con ID: $idCobro")

                // 4. CONSULTAR TODOS LOS COBROS
                val cobros = database.cobroDao().obtenerTodos()
                mostrarMensaje("Total de cobros: ${cobros.size}")

                // 5. CONSULTAR TOTAL DEL DÍA
                val total = database.cobroDao().obtenerTotalDia(obtenerFechaActual())
                mostrarMensaje("Total recaudado hoy: $$total")

            } catch (e: Exception) {
                mostrarMensaje("Error: ${e.message}")
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }
}