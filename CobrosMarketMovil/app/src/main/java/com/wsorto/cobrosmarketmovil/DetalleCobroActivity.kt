package com.wsorto.cobrosmarketmovil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase

class DetalleCobroActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var idCobro: Int = -1

    private lateinit var tvIdCobro: TextView
    private lateinit var tvDetalleNombreComerciante: TextView
    private lateinit var tvDetallePuesto: TextView
    private lateinit var tvDetalleMontoCobrado: TextView
    private lateinit var tvDetalleDineroRecibido: TextView
    private lateinit var tvDetalleVuelto: TextView
    private lateinit var tvDetalleFecha: TextView
    private lateinit var tvDetalleEstado: TextView
    private lateinit var tvDetalleUbicacion: TextView
    private lateinit var tvDetalleObservaciones: TextView
    private lateinit var cardUbicacion: CardView
    private lateinit var cardObservaciones: CardView
    private lateinit var btnEliminar: Button
    private lateinit var btnCerrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cobro)

        database = AppDatabase.getDatabase(this)

        idCobro = intent.getIntExtra("ID_COBRO", -1)

        if (idCobro == -1) {
            Toast.makeText(this, "Error: ID no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        inicializarVistas()

        configurarListeners()

        supportActionBar?.title = "Detalle del Cobro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cargarDetalleCobro()
    }

    private fun inicializarVistas() {
        tvIdCobro = findViewById(R.id.tvIdCobro)
        tvDetalleNombreComerciante = findViewById(R.id.tvDetalleNombreComerciante)
        tvDetallePuesto = findViewById(R.id.tvDetallePuesto)
        tvDetalleMontoCobrado = findViewById(R.id.tvDetalleMontoCobrado)
        tvDetalleDineroRecibido = findViewById(R.id.tvDetalleDineroRecibido)
        tvDetalleVuelto = findViewById(R.id.tvDetalleVuelto)
        tvDetalleFecha = findViewById(R.id.tvDetalleFecha)
        tvDetalleEstado = findViewById(R.id.tvDetalleEstado)
        tvDetalleUbicacion = findViewById(R.id.tvDetalleUbicacion)
        tvDetalleObservaciones = findViewById(R.id.tvDetalleObservaciones)
        cardUbicacion = findViewById(R.id.cardUbicacion)
        cardObservaciones = findViewById(R.id.cardObservaciones)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnCerrar = findViewById(R.id.btnCerrar)
    }

    private fun configurarListeners() {
        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }

        btnCerrar.setOnClickListener {
            finish()
        }
    }

    private fun cargarDetalleCobro() {
        lifecycleScope.launch {
            try {
                val cobro = database.cobroDao().obtenerPorId(idCobro)

                if (cobro == null) {
                    runOnUiThread {
                        Toast.makeText(
                            this@DetalleCobroActivity,
                            "Cobro no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    return@launch
                }

                val comerciante = database.comercianteDao().obtenerPorId(cobro.id_comerciante)

                runOnUiThread {
                    tvIdCobro.text = "ID: #${String.format("%05d", cobro.id_cobro)}"

                    tvDetalleNombreComerciante.text = comerciante?.nombre_comerciante ?: "N/A"
                    tvDetallePuesto.text = "📍 Puesto: ${comerciante?.numero_puesto ?: "N/A"}"

                    tvDetalleMontoCobrado.text = String.format("$%.2f", cobro.monto_cobrado)
                    tvDetalleDineroRecibido.text = String.format("$%.2f", cobro.dinero_recibido)
                    tvDetalleVuelto.text = String.format("$%.2f", cobro.vuelto_entregado)
                    tvDetalleFecha.text = cobro.fecha_cobro

                    when (cobro.estado) {
                        "completado" -> {
                            tvDetalleEstado.text = "✅ Completado"
                            tvDetalleEstado.setBackgroundColor(getColor(android.R.color.holo_green_dark))
                        }
                        "pendiente" -> {
                            tvDetalleEstado.text = "⏳ Pendiente"
                            tvDetalleEstado.setBackgroundColor(getColor(android.R.color.holo_orange_dark))
                        }
                        "anulado" -> {
                            tvDetalleEstado.text = "❌ Anulado"
                            tvDetalleEstado.setBackgroundColor(getColor(android.R.color.holo_red_dark))
                        }
                    }

                    if (cobro.latitud != null && cobro.longitud != null) {
                        cardUbicacion.visibility = View.VISIBLE
                        tvDetalleUbicacion.text = "Lat: ${"%.6f".format(cobro.latitud)}, Lng: ${"%.6f".format(cobro.longitud)}"
                    }

                    if (!cobro.observaciones.isNullOrEmpty()) {
                        cardObservaciones.visibility = View.VISIBLE
                        tvDetalleObservaciones.text = cobro.observaciones
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@DetalleCobroActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Confirmar Eliminación")
            .setMessage("¿Está seguro de eliminar este cobro? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCobro()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCobro() {
        lifecycleScope.launch {
            try {
                val cobro = database.cobroDao().obtenerPorId(idCobro)

                if (cobro != null) {
                    database.cobroDao().eliminar(cobro)

                    runOnUiThread {
                        Toast.makeText(
                            this@DetalleCobroActivity,
                            "✅ Cobro eliminado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@DetalleCobroActivity,
                        "❌ Error al eliminar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}