package com.wsorto.cobrosmarketmovil.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "comerciantes",
    indices = [
        Index(value = ["numero_puesto"], unique = true),
        Index(value = ["nombre_comerciante"])
    ]
)
data class Comerciante(
    @PrimaryKey(autoGenerate = true)
    val id_comerciante: Int = 0,

    val numero_puesto: String,

    val nombre_comerciante: String,

    val telefono_comerciante: String? = null,

    val fecha_registro: String = ""
)