package com.wsorto.cobrosmarketmovil

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase
import com.wsorto.cobrosmarketmovil.CobroAdapter
import com.wsorto.cobrosmarketmovil.CobroConComerciante
import java.text.SimpleDateFormat
import java.util.*

class ReportesActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var btnFechaInicio: Button
    private lateinit var btnFechaFin: Button
    private lateinit var btnGenerarReporte: Button
    private lateinit var cardResultados: CardView
    private lateinit var cardDetalle: CardView
    private lateinit var tvPeriodo: TextView
    private lateinit var tvTotalCobrosReporte: TextView
    private lateinit var tvTotalRecaudadoReporte: TextView
    private lateinit var rvReporte: RecyclerView
    private lateinit var adapter: CobroAdapter

    private var fechaInicio: String? = null
    private var fechaFin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        database = AppDatabase.getDatabase(this)

        btnFechaInicio = findViewById(R.id.btnFechaInicio)
        btnFechaFin = findViewById(R.id.btnFechaFin)
        btnGenerarReporte = findViewById(R.id.btnGenerarReporte)
        cardResultados = findViewById(R.id.cardResultados)
        cardDetalle = findViewById(R.id.cardDetalle)
        tvPeriodo = findViewById(R.id.tvPeriodo)
        tvTotalCobrosReporte = findViewById(R.id.tvTotalCobrosReporte)
        tvTotalRecaudadoReporte = findViewById(R.id.tvTotalRecaudadoReporte)
        rvReporte = findViewById(R.id.rvReporte)

        rvReporte.layoutManager = LinearLayoutManager(this)
        adapter = CobroAdapter(emptyList()) { /* No hay acción en reporte */ }
        rvReporte.adapter = adapter

        supportActionBar?.title = "Reportes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configurarListeners()
    }

    private fun configurarListeners() {
        btnFechaInicio.setOnClickListener {
            mostrarDatePicker { fecha ->
                fechaInicio = fecha
                btnFechaInicio.text = "📅 Inicio: $fecha"
            }
        }

        btnFechaFin.setOnClickListener {
            mostrarDatePicker { fecha ->
                fechaFin = fecha
                btnFechaFin.text = "📅 Fin: $fecha"
            }
        }

        btnGenerarReporte.setOnClickListener {
            generarReporte()
        }
    }

    private fun mostrarDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val fecha = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                onDateSelected(fecha)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun generarReporte() {
        if (fechaInicio == null || fechaFin == null) {
            Toast.makeText(this, "❌ Seleccione ambas fechas", Toast.LENGTH_SHORT).show()
            return
        }

        if (fechaInicio!! > fechaFin!!) {
            Toast.makeText(
                this,
                "❌ La fecha inicio debe ser anterior o igual a la fecha fin",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                val cobros = database.cobroDao().obtenerPorRangoFechas(fechaInicio!!, fechaFin!!)

                val cobrosConComerciantesList = mutableListOf<CobroConComerciante>()

                for (cobro in cobros) {
                    val comerciante = database.comercianteDao().obtenerPorId(cobro.id_comerciante)
                    if (comerciante != null) {
                        cobrosConComerciantesList.add(
                            CobroConComerciante(cobro, comerciante)
                        )
                    }
                }

                val total = database.cobroDao().obtenerTotalRango(fechaInicio!!, fechaFin!!) ?: 0.0

                runOnUiThread {
                    cardResultados.visibility = View.VISIBLE
                    tvPeriodo.text = "$fechaInicio - $fechaFin"
                    tvTotalCobrosReporte.text = cobrosConComerciantesList.size.toString()
                    tvTotalRecaudadoReporte.text = String.format("$%.2f", total)

                    if (cobrosConComerciantesList.isNotEmpty()) {
                        cardDetalle.visibility = View.VISIBLE
                        adapter.actualizarDatos(cobrosConComerciantesList)
                    } else {
                        cardDetalle.visibility = View.GONE
                        Toast.makeText(
                            this@ReportesActivity,
                            "No hay cobros en el período seleccionado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ReportesActivity,
                        "❌ Error al generar reporte: ${e.message}",
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