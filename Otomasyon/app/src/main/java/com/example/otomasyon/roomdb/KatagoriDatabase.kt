package com.example.otomasyon.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.otomasyon.models.Katagori

@Database(entities = [Katagori::class], version = 1)
abstract class KatagoriDatabase : RoomDatabase(){
    abstract fun katagoriDAO():KatagoriDAO
}