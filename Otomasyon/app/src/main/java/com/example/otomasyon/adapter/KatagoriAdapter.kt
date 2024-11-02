package com.example.otomasyon.adapter

import android.database.DataSetObserver
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.otomasyon.databinding.FragmentKatagorilerBinding
import com.example.otomasyon.databinding.KatagoriItemBinding
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.view.EditKatagoriFragmentDirections
import com.example.otomasyon.view.KatagorilerFragment
import com.example.otomasyon.view.KatagorilerFragmentDirections

class KatagoriAdapter(val adapterKullanimYeri :String,val katagoriListesi : List<Katagori>): RecyclerView.Adapter<KatagoriAdapter.KatagoriHolder>(){


    class KatagoriHolder(val binding: KatagoriItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KatagoriHolder {
        val rowBinding = KatagoriItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return KatagoriHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return katagoriListesi.size
    }

    override fun onBindViewHolder(holder: KatagoriHolder, position: Int) {
        val bitmap = BitmapFactory.decodeByteArray(katagoriListesi[position].katagoriGorseli,0,katagoriListesi[position].katagoriGorseli.size)
        holder.binding.itemKatagoriImageView.setImageBitmap(bitmap)
        holder.binding.itemKatagoriName.setText(katagoriListesi[position].katagoriName)
        if (adapterKullanimYeri =="EditKatagori"){
            holder.itemView.setOnClickListener {
                val action = EditKatagoriFragmentDirections.actionEditKatagoriFragmentToLastEditKatagori(katagoriListesi[position].id)
                Navigation.findNavController(it).navigate(action)
            }
        }else{
            holder.itemView.setOnClickListener{
                val action = KatagorilerFragmentDirections.actionKatagorilerFragmentToUrunlerFragment(katagoriListesi[position].katagoriName.toString())
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

}