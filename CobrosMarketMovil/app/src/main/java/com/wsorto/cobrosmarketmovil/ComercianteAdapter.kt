package com.wsorto.cobrosmarketmovil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante

class ComercianteAdapter(
    private var comerciantes: List<Comerciante>,
    private val onVerCobrosClick: (Comerciante) -> Unit
) : RecyclerView.Adapter<ComercianteAdapter.ComercianteViewHolder>() {

    class ComercianteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreComercianteLista)
        val tvPuesto: TextView = view.findViewById(R.id.tvPuestoLista)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefonoLista)
        val btnVerCobros: Button = view.findViewById(R.id.btnVerCobros)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComercianteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comerciante, parent, false)
        return ComercianteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComercianteViewHolder, position: Int) {
        val comerciante = comerciantes[position]

        holder.tvNombre.text = comerciante.nombre_comerciante
        holder.tvPuesto.text = "📍 Puesto: ${comerciante.numero_puesto}"
        holder.tvTelefono.text = if (comerciante.telefono_comerciante.isNullOrEmpty()) {
            "📞 Sin teléfono"
        } else {
            "📞 ${comerciante.telefono_comerciante}"
        }

        holder.btnVerCobros.setOnClickListener {
            onVerCobrosClick(comerciante)
        }
    }

    override fun getItemCount() = comerciantes.size

    fun actualizarDatos(nuevosComerciantes: List<Comerciante>) {
        comerciantes = nuevosComerciantes
        notifyDataSetChanged()
    }
}