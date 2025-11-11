package com.wsorto.cobrosmarketmovil.data.dao

import androidx.room.*
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante

@Dao
interface ComercianteDao {

    @Insert
    suspend fun insertar(comerciante: Comerciante): Long

    @Update
    suspend fun actualizar(comerciante: Comerciante)

    @Delete
    suspend fun eliminar(comerciante: Comerciante)

    @Query("SELECT * FROM comerciantes WHERE id_comerciante = :id")
    suspend fun obtenerPorId(id: Int): Comerciante?

    @Query("SELECT * FROM comerciantes")
    suspend fun obtenerTodos(): List<Comerciante>

    @Query("SELECT * FROM comerciantes WHERE numero_puesto = :numeroPuesto")
    suspend fun obtenerPorPuesto(numeroPuesto: String): Comerciante?

    @Query("SELECT * FROM comerciantes WHERE nombre_comerciante LIKE '%' || :nombre || '%'")
    suspend fun buscarPorNombre(nombre: String): List<Comerciante>
}