package com.example.otomasyon.models

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Katagori(

    @ColumnInfo(name = "katagoriGorseli")
    var katagoriGorseli:ByteArray,
    @ColumnInfo(name="katagoriName")
    var katagoriName : String

) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    override fun toString(): String {
        return katagoriName
    }


}