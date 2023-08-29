package com.example.listatareas.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tarea (
    @PrimaryKey(autoGenerate = true) val id:Int,
    var tarea:String,
    var realizada:Boolean
)
