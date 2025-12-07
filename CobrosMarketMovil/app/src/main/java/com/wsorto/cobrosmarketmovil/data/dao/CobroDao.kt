package com.wsorto.cobrosmarketmovil.data.dao

import androidx.room.*
import com.wsorto.cobrosmarketmovil.data.entities.Cobro

@Dao
interface CobroDao {

    @Insert
    suspend fun insertar(cobro: Cobro): Long

    @Update
    suspend fun actualizar(cobro: Cobro)

    @Delete
    suspend fun eliminar(cobro: Cobro)

    @Query("SELECT * FROM cobros WHERE id_cobro = :id")
    suspend fun obtenerPorId(id: Int): Cobro?

    @Query("SELECT * FROM cobros ORDER BY fecha_cobro DESC")
    suspend fun obtenerTodos(): List<Cobro>

    @Query("SELECT * FROM cobros WHERE fecha_cobro = :fecha ORDER BY id_cobro DESC")
    suspend fun obtenerPorFecha(fecha: String): List<Cobro>

    @Query("SELECT * FROM cobros WHERE fecha_cobro BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha_cobro DESC")
    suspend fun obtenerPorRangoFechas(fechaInicio: String, fechaFin: String): List<Cobro>

    @Query("SELECT * FROM cobros WHERE id_comerciante = :idComerciante ORDER BY fecha_cobro DESC")
    suspend fun obtenerPorComerciante(idComerciante: Int): List<Cobro>

    @Query("SELECT * FROM cobros WHERE id_usuario = :idUsuario ORDER BY fecha_cobro DESC")
    suspend fun obtenerPorUsuario(idUsuario: Int): List<Cobro>

    @Query("SELECT SUM(monto_cobrado) FROM cobros WHERE fecha_cobro = :fecha AND estado = 'completado'")
    suspend fun obtenerTotalDia(fecha: String): Double?

    @Query("SELECT SUM(monto_cobrado) FROM cobros WHERE fecha_cobro BETWEEN :fechaInicio AND :fechaFin AND estado = 'completado'")
    suspend fun obtenerTotalRango(fechaInicio: String, fechaFin: String): Double?
}