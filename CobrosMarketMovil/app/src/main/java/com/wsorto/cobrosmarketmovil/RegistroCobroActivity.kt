package com.wsorto.cobrosmarketmovil

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase
import com.wsorto.cobrosmarketmovil.data.entities.Cobro
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante
import java.text.SimpleDateFormat
import java.util.*

class RegistroCobroActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etNumeroPuesto: TextInputEditText
    private lateinit var etNombreComerciante: TextInputEditText
    private lateinit var etMontoCobrado: TextInputEditText
    private lateinit var etDineroRecibido: TextInputEditText
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var tvVuelto: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var btnBuscarComerciante: Button
    private lateinit var btnCapturarUbicacion: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnGuardar: Button

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var idComercianteSeleccionado: Int? = null
    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cobro)

        database = AppDatabase.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        inicializarVistas()

        configurarListeners()

        supportActionBar?.title = "Registrar Cobro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun inicializarVistas() {
        etNumeroPuesto = findViewById(R.id.etNumeroPuesto)
        etNombreComerciante = findViewById(R.id.etNombreComerciante)
        etMontoCobrado = findViewById(R.id.etMontoCobrado)
        etDineroRecibido = findViewById(R.id.etDineroRecibido)
        etObservaciones = findViewById(R.id.etObservaciones)
        tvVuelto = findViewById(R.id.tvVuelto)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        btnBuscarComerciante = findViewById(R.id.btnBuscarComerciante)
        btnCapturarUbicacion = findViewById(R.id.btnCapturarUbicacion)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun configurarListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calcularVuelto()
            }
        }

        etMontoCobrado.addTextChangedListener(textWatcher)
        etDineroRecibido.addTextChangedListener(textWatcher)

        btnBuscarComerciante.setOnClickListener {
            buscarComerciante()
        }

        btnCapturarUbicacion.setOnClickListener {
            capturarUbicacion()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarCobro()
        }
    }

    private fun calcularVuelto() {
        try {
            val monto = etMontoCobrado.text.toString().toDoubleOrNull() ?: 0.0
            val recibido = etDineroRecibido.text.toString().toDoubleOrNull() ?: 0.0
            val vuelto = recibido - monto

            if (vuelto >= 0) {
                tvVuelto.text = String.format("$%.2f", vuelto)
                tvVuelto.setTextColor(getColor(android.R.color.holo_green_dark))
            } else {
                tvVuelto.text = String.format("$%.2f", vuelto)
                tvVuelto.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        } catch (e: Exception) {
            tvVuelto.text = "$0.00"
        }
    }

    private fun buscarComerciante() {
        val numeroPuesto = etNumeroPuesto.text.toString().trim()

        if (numeroPuesto.isEmpty()) {
            Toast.makeText(this, "Ingrese un número de puesto", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val comerciante = database.comercianteDao().obtenerPorPuesto(numeroPuesto)

                if (comerciante != null) {
                    runOnUiThread {
                        etNombreComerciante.setText(comerciante.nombre_comerciante)
                        etNombreComerciante.isEnabled = false
                        idComercianteSeleccionado = comerciante.id_comerciante
                        Toast.makeText(
                            this@RegistroCobroActivity,
                            "✅ Comerciante encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        AlertDialog.Builder(this@RegistroCobroActivity)
                            .setTitle("Comerciante No Encontrado")
                            .setMessage("El puesto $numeroPuesto no existe. ¿Desea registrar un nuevo comerciante?")
                            .setPositiveButton("Sí") { _, _ ->
                                etNombreComerciante.isEnabled = true
                                etNombreComerciante.requestFocus()
                                idComercianteSeleccionado = null
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegistroCobroActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun capturarUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitud = location.latitude
                longitud = location.longitude
                tvUbicacion.text = "📍 Lat: ${"%.6f".format(latitud)}, Lng: ${"%.6f".format(longitud)}"
                tvUbicacion.setBackgroundColor(getColor(android.R.color.holo_green_light))
                Toast.makeText(this, "✅ Ubicación capturada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCobro() {
        val numeroPuesto = etNumeroPuesto.text.toString().trim()
        val nombreComerciante = etNombreComerciante.text.toString().trim()
        val montoStr = etMontoCobrado.text.toString().trim()
        val recibidoStr = etDineroRecibido.text.toString().trim()
        val observaciones = etObservaciones.text.toString().trim()

        if (numeroPuesto.isEmpty()) {
            Toast.makeText(this, "❌ Ingrese el número de puesto", Toast.LENGTH_SHORT).show()
            return
        }

        if (nombreComerciante.isEmpty()) {
            Toast.makeText(this, "❌ Ingrese el nombre del comerciante", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "❌ Ingrese un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val recibido = recibidoStr.toDoubleOrNull()
        if (recibido == null || recibido < monto) {
            Toast.makeText(this, "❌ El dinero recibido debe ser mayor o igual al monto", Toast.LENGTH_SHORT).show()
            return
        }

        val vuelto = recibido - monto

        lifecycleScope.launch {
            try {
                var idComerciante = idComercianteSeleccionado

                if (idComerciante == null) {
                    val nuevoComercianteId = database.comercianteDao().insertar(
                        Comerciante(
                            numero_puesto = numeroPuesto,
                            nombre_comerciante = nombreComerciante,
                            fecha_registro = obtenerFechaActual()
                        )
                    )
                    idComerciante = nuevoComercianteId.toInt()
                }

                val idUsuario = 1

                val cobro = Cobro(
                    id_comerciante = idComerciante,
                    id_usuario = idUsuario,
                    fecha_cobro = obtenerFechaActual(),
                    monto_cobrado = monto,
                    dinero_recibido = recibido,
                    vuelto_entregado = vuelto,
                    latitud = latitud,
                    longitud = longitud,
                    observaciones = observaciones.ifEmpty { null },
                    estado = "completado"
                )

                database.cobroDao().insertar(cobro)

                runOnUiThread {
                    Toast.makeText(
                        this@RegistroCobroActivity,
                        "✅ Cobro registrado exitosamente",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegistroCobroActivity,
                        "❌ Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturarUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}