package com.example.otomasyon.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.otomasyon.models.Urun
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface UrunDAO {
    @Query("SELECT * FROM Urun")
    fun getAll(): Flowable<List<Urun>>

    @Query("SELECT * FROM Urun WHERE urunID= :id")
    fun findByIdUrun(id:Int): Flowable<Urun>

    @Query("SELECT * FROM Urun WHERE urunKatagoriName = :KatagoriName")
    fun findByIdUrunKatagori(KatagoriName: String): Flowable<List<Urun>>


    @Insert
    fun InsertUrun(urun: Urun): Completable

    @Delete
    fun DeleteUrun(urun: Urun): Completable
}