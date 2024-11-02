package com.example.otomasyon.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.otomasyon.models.Katagori
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface KatagoriDAO {

    @Query("SELECT * FROM Katagori")
    fun getAll(): Flowable<List<Katagori>>

    @Query("SELECT * FROM Katagori WHERE id= :id")
    fun findByIdKatagori(id:Int):Flowable<Katagori>


    @Insert
    fun InsertKatagori(katagori:Katagori):Completable

    @Delete
    fun DeleteKatagori(katagori: Katagori):Completable
}