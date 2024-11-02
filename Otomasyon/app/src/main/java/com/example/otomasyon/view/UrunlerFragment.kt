package com.example.otomasyon.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.otomasyon.R
import com.example.otomasyon.adapter.UrunAdapter
import com.example.otomasyon.databinding.FragmentAnaMenuBinding
import com.example.otomasyon.databinding.FragmentUrunlerBinding
import com.example.otomasyon.models.Urun
import com.example.otomasyon.roomdb.UrunDAO
import com.example.otomasyon.roomdb.UrunDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class UrunlerFragment : Fragment() {

    private  var _binding: FragmentUrunlerBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: UrunDatabase
    private lateinit var urunDao : UrunDAO
    private  val mDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(requireContext(),UrunDatabase::class.java,"Urunler").build()
        urunDao = db.urunDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUrunlerBinding.inflate(inflater, container, false)
        val view = binding.root
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         binding.recyclerViewUrunler.layoutManager = LinearLayoutManager(requireContext())
        arguments?.let {
            val gelenDegisken= UrunlerFragmentArgs.fromBundle(it).katagoriName.toString()
            UrunleriListele(gelenDegisken)
        }


    }
    private fun UrunleriListele(katagorName:String){
        mDisposable.add(
            urunDao.findByIdUrunKatagori(katagorName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForGetUrun)
        )
    }
    private fun handleResponseForGetUrun( urun: List<Urun>){
        val adapterUrunler = UrunAdapter(urun)
        binding.recyclerViewUrunler.adapter = adapterUrunler
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }


}