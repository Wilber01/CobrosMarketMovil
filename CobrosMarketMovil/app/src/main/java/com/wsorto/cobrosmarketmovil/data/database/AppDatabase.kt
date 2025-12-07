package com.wsorto.cobrosmarketmovil.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wsorto.cobrosmarketmovil.data.dao.CobroDao
import com.wsorto.cobrosmarketmovil.data.dao.ComercianteDao
import com.wsorto.cobrosmarketmovil.data.dao.UsuarioDao
import com.wsorto.cobrosmarketmovil.data.entities.Cobro
import com.wsorto.cobrosmarketmovil.data.entities.Comerciante
import com.wsorto.cobrosmarketmovil.data.entities.Usuario

@Database(
    entities = [Usuario::class, Comerciante::class, Cobro::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun comercianteDao(): ComercianteDao
    abstract fun cobroDao(): CobroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cobros_mercado_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}