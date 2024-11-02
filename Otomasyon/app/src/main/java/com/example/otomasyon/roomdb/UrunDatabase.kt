package com.example.otomasyon.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.models.Urun

@Database(entities = [Urun::class], version = 1)
abstract class UrunDatabase : RoomDatabase(){
    abstract fun urunDAO():UrunDAO
}