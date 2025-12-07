package com.wsorto.cobrosmarketmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.database.AppDatabase
import com.wsorto.cobrosmarketmovil.data.entities.Usuario

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        crearUsuarioPorDefecto()

        findViewById<CardView>(R.id.cardRegistrarCobro).setOnClickListener {
            startActivity(Intent(this, RegistroCobroActivity::class.java))
        }

        findViewById<CardView>(R.id.cardConsultarCobros).setOnClickListener {
            startActivity(Intent(this, ListaCobrosActivity::class.java))
        }

        findViewById<CardView>(R.id.cardReportes).setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        findViewById<CardView>(R.id.cardGestionarComerciantes).setOnClickListener {
            startActivity(Intent(this, GestionComerciantesActivity::class.java))
        }
    }

    private fun crearUsuarioPorDefecto() {
        lifecycleScope.launch {
            try {
                val usuarios = database.usuarioDao().obtenerTodos()
                if (usuarios.isEmpty()) {
                    // Crear usuario por defecto
                    database.usuarioDao().insertar(
                        Usuario(
                            nombre_usuario = "Rosa Martinez",
                            cargo = "cobrador",
                            fecha_creacion = java.text.SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date())
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}