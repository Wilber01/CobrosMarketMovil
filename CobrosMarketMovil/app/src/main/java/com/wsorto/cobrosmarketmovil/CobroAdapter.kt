package com.wsorto.cobrosmarketmovil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wsorto.cobrosmarketmovil.R
import com.wsorto.cobrosmarketmovil.data.entities.Cobro
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante

data class CobroConComerciante(
    val cobro: Cobro,
    val comerciante: Comerciante
)

class CobroAdapter(
    private var cobros: List<CobroConComerciante>,
    private val onItemClick: (CobroConComerciante) -> Unit
) : RecyclerView.Adapter<CobroAdapter.CobroViewHolder>() {

    class CobroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPuesto: TextView = view.findViewById(R.id.tvPuesto)
        val tvNombreComerciante: TextView = view.findViewById(R.id.tvNombreComerciante)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val tvDineroRecibido: TextView = view.findViewById(R.id.tvDineroRecibido)
        val tvVuelto: TextView = view.findViewById(R.id.tvVuelto)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CobroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cobro, parent, false)
        return CobroViewHolder(view)
    }

    override fun onBindViewHolder(holder: CobroViewHolder, position: Int) {
        val item = cobros[position]
        val cobro = item.cobro
        val comerciante = item.comerciante

        holder.tvPuesto.text = "Puesto ${comerciante.numero_puesto}"
        holder.tvNombreComerciante.text = comerciante.nombre_comerciante
        holder.tvMonto.text = String.format("$%.2f", cobro.monto_cobrado)
        holder.tvDineroRecibido.text = String.format("$%.2f", cobro.dinero_recibido)
        holder.tvVuelto.text = String.format("$%.2f", cobro.vuelto_entregado)
        holder.tvFecha.text = cobro.fecha_cobro

        when (cobro.estado) {
            "completado" -> {
                holder.tvEstado.text = "✅ Completado"
                holder.tvEstado.setBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_green_dark)
                )
            }
            "pendiente" -> {
                holder.tvEstado.text = "⏳ Pendiente"
                holder.tvEstado.setBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_orange_dark)
                )
            }
            "anulado" -> {
                holder.tvEstado.text = "❌ Anulado"
                holder.tvEstado.setBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_red_dark)
                )
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = cobros.size

    fun actualizarDatos(nuevosCobros: List<CobroConComerciante>) {
        cobros = nuevosCobros
        notifyDataSetChanged()
    }
}