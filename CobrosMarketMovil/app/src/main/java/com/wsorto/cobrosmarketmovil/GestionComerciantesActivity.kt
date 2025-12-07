package com.wsorto.cobrosmarketmovil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante
import com.wsorto.cobrosmarketmovil.ComercianteAdapter

class GestionComerciantesActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var rvComerciantes: RecyclerView
    private lateinit var etBuscar: TextInputEditText
    private lateinit var btnBuscar: Button
    private lateinit var tvTotalComerciantes: TextView
    private lateinit var layoutVacio: LinearLayout
    private lateinit var adapter: ComercianteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_comerciantes)

        database = AppDatabase.getDatabase(this)

        rvComerciantes = findViewById(R.id.rvComerciantes)
        etBuscar = findViewById(R.id.etBuscar)
        btnBuscar = findViewById(R.id.btnBuscar)
        tvTotalComerciantes = findViewById(R.id.tvTotalComerciantes)
        layoutVacio = findViewById(R.id.layoutVacio)

        rvComerciantes.layoutManager = LinearLayoutManager(this)
        adapter = ComercianteAdapter(emptyList()) { comerciante ->
            mostrarCobrosComerciante(comerciante)
        }
        rvComerciantes.adapter = adapter

        supportActionBar?.title = "Gestión de Comerciantes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnBuscar.setOnClickListener {
            buscarComerciante()
        }

        cargarComerciantes()
    }

    private fun cargarComerciantes() {
        lifecycleScope.launch {
            try {
                val comerciantes = database.comercianteDao().obtenerTodos()

                runOnUiThread {
                    if (comerciantes.isEmpty()) {
                        rvComerciantes.visibility = View.GONE
                        layoutVacio.visibility = View.VISIBLE
                        tvTotalComerciantes.text = "📊 Total: 0 comerciantes"
                    } else {
                        rvComerciantes.visibility = View.VISIBLE
                        layoutVacio.visibility = View.GONE
                        adapter.actualizarDatos(comerciantes)
                        tvTotalComerciantes.text = "📊 Total: ${comerciantes.size} comerciantes"
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@GestionComerciantesActivity,
                        "Error al cargar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun buscarComerciante() {
        val termino = etBuscar.text.toString().trim()

        if (termino.isEmpty()) {
            cargarComerciantes()
            return
        }

        lifecycleScope.launch {
            try {
                val comerciantes = database.comercianteDao().buscarPorNombre(termino)

                runOnUiThread {
                    if (comerciantes.isEmpty()) {
                        Toast.makeText(
                            this@GestionComerciantesActivity,
                            "No se encontraron comerciantes",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.actualizarDatos(comerciantes)
                        tvTotalComerciantes.text = "📊 Encontrados: ${comerciantes.size}"
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@GestionComerciantesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mostrarCobrosComerciante(comerciante: Comerciante) {
        lifecycleScope.launch {
            try {
                val cobros = database.cobroDao().obtenerPorComerciante(comerciante.id_comerciante)
                val totalCobrado = cobros.filter { it.estado == "completado" }
                    .sumOf { it.monto_cobrado }

                runOnUiThread {
                    AlertDialog.Builder(this@GestionComerciantesActivity)
                        .setTitle("📊 Cobros de ${comerciante.nombre_comerciante}")
                        .setMessage(
                            """
                            📍 Puesto: ${comerciante.numero_puesto}
                            📞 Teléfono: ${comerciante.telefono_comerciante ?: "N/A"}
                            
                            📋 Total de cobros: ${cobros.size}
                            💰 Total cobrado: ${"$%.2f".format(totalCobrado)}
                            """.trimIndent()
                        )
                        .setPositiveButton("Cerrar", null)
                        .show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@GestionComerciantesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
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