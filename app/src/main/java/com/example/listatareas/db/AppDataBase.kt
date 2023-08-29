package com.example.listatareas.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities= [Tarea::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
        abstract fun tareaDao(): TareaDao

        companion object {

            @Volatile
            private var BASE_DATOS: AppDataBase? =null
            fun getInstance(contexto: Context):AppDataBase {
                return BASE_DATOS ?: synchronized(this){
                    Room.databaseBuilder(
                        contexto.applicationContext,
                        AppDataBase::class.java,
                        "tareas.bd"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { BASE_DATOS=it }
                }
            }
        }

}