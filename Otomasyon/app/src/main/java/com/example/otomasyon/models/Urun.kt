package com.example.otomasyon.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Urun(
    @ColumnInfo(name="urunAdi")
    var urunAdi : String,
    @ColumnInfo(name="urunGorseli")
    var urunGorseli : ByteArray,
    @ColumnInfo(name = "urunStokMiktari")
    var urunStokMiktari :Int,
    @ColumnInfo(name="urunKatagoriName")
    var urunKatagoriName : String,
    @ColumnInfo(name = "katagoriID")
    var urunKatagoriID: Int)
{
    @PrimaryKey(autoGenerate = true)
    var urunID = 0


}