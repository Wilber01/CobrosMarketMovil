package com.wsorto.cobrosmarketmovil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

class ListaCobrosActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var rvCobros: RecyclerView
    private lateinit var tvTotalRecaudado: TextView
    private lateinit var tvTotalCobros: TextView
    private lateinit var tvFechaActual: TextView
    private lateinit var layoutVacio: LinearLayout
    private lateinit var adapter: CobroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cobros)

        database = AppDatabase.getDatabase(this)

        rvCobros = findViewById(R.id.rvCobros)
        tvTotalRecaudado = findViewById(R.id.tvTotalRecaudado)
        tvTotalCobros = findViewById(R.id.tvTotalCobros)
        tvFechaActual = findViewById(R.id.tvFechaActual)
        layoutVacio = findViewById(R.id.layoutVacio)

        rvCobros.layoutManager = LinearLayoutManager(this)
        adapter = CobroAdapter(emptyList()) { cobroConComerciante ->
            abrirDetalleCobro(cobroConComerciante)
        }
        rvCobros.adapter = adapter

        supportActionBar?.title = "Cobros del Día"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvFechaActual.text = "📅 Hoy: ${obtenerFechaActual()}"

        cargarCobrosDelDia()
    }

    private fun cargarCobrosDelDia() {
        lifecycleScope.launch {
            try {
                val fechaHoy = obtenerFechaActual()
                val cobros = database.cobroDao().obtenerPorFecha(fechaHoy)

                // Obtener comerciantes asociados
                val cobrosConComerciantesList = mutableListOf<CobroConComerciante>()

                for (cobro in cobros) {
                    val comerciante = database.comercianteDao().obtenerPorId(cobro.id_comerciante)
                    if (comerciante != null) {
                        cobrosConComerciantesList.add(
                            CobroConComerciante(cobro, comerciante)
                        )
                    }
                }

                val total = database.cobroDao().obtenerTotalDia(fechaHoy) ?: 0.0

                runOnUiThread {
                    if (cobrosConComerciantesList.isEmpty()) {
                        rvCobros.visibility = View.GONE
                        layoutVacio.visibility = View.VISIBLE
                        tvTotalRecaudado.text = "$0.00"
                        tvTotalCobros.text = "0"
                    } else {
                        rvCobros.visibility = View.VISIBLE
                        layoutVacio.visibility = View.GONE
                        adapter.actualizarDatos(cobrosConComerciantesList)

                        // Usar el total que ya calculamos arriba
                        tvTotalRecaudado.text = String.format("$%.2f", total)
                        tvTotalCobros.text = cobrosConComerciantesList.size.toString()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvTotalRecaudado.text = "$0.00"
                    tvTotalCobros.text = "0"
                }
            }
        }
    }

    private fun abrirDetalleCobro(cobroConComerciante: CobroConComerciante) {
        val intent = Intent(this, DetalleCobroActivity::class.java)
        intent.putExtra("ID_COBRO", cobroConComerciante.cobro.id_cobro)
        startActivity(intent)
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    override fun onResume() {
        super.onResume()
        cargarCobrosDelDia()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}