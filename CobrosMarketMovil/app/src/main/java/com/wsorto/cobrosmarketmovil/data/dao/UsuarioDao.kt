package com.wsorto.cobrosmarketmovil.data.dao

import androidx.room.*
import com.wsorto.cobrosmarketmovil.data.entities.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Delete
    suspend fun eliminar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE cargo = :cargo")
    suspend fun obtenerPorCargo(cargo: String): List<Usuario>
}