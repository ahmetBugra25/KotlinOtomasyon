package com.example.otomasyon.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.otomasyon.databinding.FragmentUrunlerBinding
import com.example.otomasyon.databinding.UrunItemBinding
import com.example.otomasyon.models.Urun
import com.example.otomasyon.view.UrunlerFragment
import com.example.otomasyon.view.UrunlerFragmentDirections

class UrunAdapter(val urunListesi: List<Urun> ): RecyclerView.Adapter<UrunAdapter.UrunHolder>() {

    class UrunHolder(val binding: UrunItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunHolder {
        val recViewBinding = UrunItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UrunHolder(recViewBinding)
    }

    override fun getItemCount(): Int {
       return urunListesi.size
    }

    override fun onBindViewHolder(holder: UrunHolder, position: Int) {
       val bitmap =BitmapFactory.decodeByteArray(urunListesi[position].urunGorseli,0,urunListesi[position].urunGorseli.size)
        holder.binding.urunImageView.setImageBitmap(bitmap)
        holder.binding.urunName.setText(urunListesi[position].urunAdi)
        holder.itemView.setOnClickListener {
         val action = UrunlerFragmentDirections.actionUrunlerFragmentToEditUrun(urunListesi[position].urunID)
         Navigation.findNavController(it).navigate(action)
        }
    }

}