package com.example.listatareas.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {
    @Query("Select * from tarea order by realizada")
    fun getAll():List<Tarea>

    @Query("Select count(*) from tarea")
    fun contar():Int

    @Insert
    fun insertar(tarea:Tarea):Long

    @Update
    fun actualizar(tarea:Tarea)

    @Delete
    fun eliminar(tarea:Tarea)
}