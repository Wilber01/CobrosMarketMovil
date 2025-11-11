package com.wsorto.cobrosmarketmovil.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["cargo"])]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id_usuario: Int = 0,

    val nombre_usuario: String,

    val cargo: String, // "cobrador" o "administrador"

    val fecha_creacion: String = "" // Se llenará con timestamp actual
)