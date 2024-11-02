package com.example.otomasyon.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.otomasyon.R
import com.example.otomasyon.adapter.KatagoriAdapter
import com.example.otomasyon.databinding.FragmentKatagorilerBinding
import com.example.otomasyon.databinding.FragmentUrunlerBinding
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.roomdb.KatagoriDAO
import com.example.otomasyon.roomdb.KatagoriDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class KatagorilerFragment : Fragment() {
    private  var _binding: FragmentKatagorilerBinding? = null
    private val binding get() = _binding!!

    private  val mDisposable = CompositeDisposable()

    private lateinit var db: KatagoriDatabase
    private lateinit var katagoriDao : KatagoriDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(requireContext(),KatagoriDatabase::class.java,"Katagoriler").build()
        katagoriDao = db.katagoriDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKatagorilerBinding.inflate(inflater, container, false)
        val view = binding.root
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewKatagoriler.layoutManager = LinearLayoutManager(requireContext())
        KatogorileriListele()

    }
    private fun  KatogorileriListele(){
        mDisposable.add(
            katagoriDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse)
        )
    }
    private fun handleResponse(katagoriler : List<Katagori>){
        val adapter = KatagoriAdapter("UrunlerKatagori",katagoriler)
        binding.recyclerViewKatagoriler.adapter = adapter

    }


    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }


}