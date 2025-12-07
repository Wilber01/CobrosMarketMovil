package com.wsorto.cobrosmarketmovil.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cobros",
    foreignKeys = [
        ForeignKey(
            entity = Comerciante::class,
            parentColumns = ["id_comerciante"],
            childColumns = ["id_comerciante"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fecha_cobro"]),
        Index(value = ["estado"]),
        Index(value = ["id_comerciante", "fecha_cobro"])
    ]
)
data class Cobro(
    @PrimaryKey(autoGenerate = true)
    val id_cobro: Int = 0,

    val id_comerciante: Int,

    val id_usuario: Int,

    val fecha_cobro: String,

    val monto_cobrado: Double,

    val dinero_recibido: Double,

    val vuelto_entregado: Double = 0.0,

    val latitud: Double? = null,

    val longitud: Double? = null,

    val observaciones: String? = null,

    val estado: String = "completado" // completado, pendiente, anulado
)