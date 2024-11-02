package com.example.otomasyon.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.otomasyon.R
import com.example.otomasyon.adapter.KatagoriAdapter
import com.example.otomasyon.databinding.FragmentAnaMenuBinding
import com.example.otomasyon.databinding.FragmentUrunEkleBinding
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.models.Urun
import com.example.otomasyon.roomdb.KatagoriDAO
import com.example.otomasyon.roomdb.KatagoriDatabase
import com.example.otomasyon.roomdb.UrunDAO
import com.example.otomasyon.roomdb.UrunDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class UrunEkleFragment : Fragment() {
    private  var _binding: FragmentUrunEkleBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissonLauncher : ActivityResultLauncher<String>
    private  lateinit var  activityResultLauncher: ActivityResultLauncher<Intent>
    private  var secilenGorsel : Uri? = null
    private  var secilenBitmap : Bitmap? = null
    private  val mDisposable = CompositeDisposable()

    private lateinit var db: UrunDatabase
    private lateinit var urunDao : UrunDAO

    private lateinit var dbKatagori: KatagoriDatabase
    private lateinit var katagoriDao : KatagoriDAO
    private var secilenKatagoriID : Int? = null
    private var secilenKatagoriName : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        db = Room.databaseBuilder(requireContext(),UrunDatabase::class.java,"Urunler").build()
        urunDao = db.urunDAO()
        dbKatagori = Room.databaseBuilder(requireContext(),KatagoriDatabase::class.java,"Katagoriler").build()
        katagoriDao = dbKatagori.katagoriDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUrunEkleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.urunImageView.setOnClickListener { GorselSec(it) }
        GetKatagoriName()
        binding.urunKaydetButton.setOnClickListener { UrunEkle(it) }


    }
    private fun GetKatagoriName(){
        mDisposable.add(
            katagoriDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponseForGetKatagoriName)
        )
    }
    private fun handleResponseForGetKatagoriName(katagori: List<Katagori>){
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, katagori)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter=adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Seçilen kategori nesnesini al
                val selectedKatagori = parent.getItemAtPosition(position) as Katagori
                secilenKatagoriName = selectedKatagori.katagoriName
                secilenKatagoriID = selectedKatagori.id



            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun UrunEkle(view: View){
        try {
            val urunName = binding.urunNameText.text.toString()
            val urunStokMiktari = binding.urunStokText.text.toString().toInt()?:0
            Toast.makeText(requireContext(),secilenKatagoriName.toString(),Toast.LENGTH_LONG).show()
            if (secilenBitmap != null){
                val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                val byteDizisi = outputStream.toByteArray()
                val yeniUrun = Urun(urunName,byteDizisi,urunStokMiktari,secilenKatagoriName!!,secilenKatagoriID!!)

                mDisposable.add(
                    urunDao.InsertUrun(yeniUrun)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseForInsertUrun))

                Toast.makeText(requireContext(),"Urun Eklendi",Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(),"Eklenen Urunnun id: " +secilenKatagoriID!!,Toast.LENGTH_LONG).show()
            }


        }catch (e:Exception){
             Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG).show()
        }

    }
    private fun handleResponseForInsertUrun(){
        findNavController().popBackStack()
    }
    fun GorselSec(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                    //snackbar göstermemiz lazım,
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin ver Butonu",View.OnClickListener {
                            permissonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                }else{
                    permissonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                val intentToGalery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }

        }
        else{
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //snackbar göstermemiz lazım,
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin ver Butonu",View.OnClickListener {
                            permissonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                }else{
                    permissonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGalery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }
        }


    }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode== AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    secilenGorsel=intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >=28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.urunImageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.urunImageView.setImageBitmap(secilenBitmap)
                        }
                    }catch (e:Exception){
                        println(e.localizedMessage)
                    }


                }
            }

        }

        permissonLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                val intentToGalery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }
            else{
                //izin verilmedi
                Toast.makeText(requireContext(),"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun kucukBitmapOlustur(kullanicininSectigiBitmap : Bitmap, maximumBoyut:Int): Bitmap {
        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height
        var bitmapOrani:Double = width.toDouble() / height.toDouble()
        if (bitmapOrani > 1){
            width = maximumBoyut
            val kisaltilmisYukseklik = width / bitmapOrani
            height = kisaltilmisYukseklik.toInt()
        }
        else{
            height = maximumBoyut
            val kisaltilmisGenislik = height / bitmapOrani
            width = kisaltilmisGenislik.toInt()
        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }

}