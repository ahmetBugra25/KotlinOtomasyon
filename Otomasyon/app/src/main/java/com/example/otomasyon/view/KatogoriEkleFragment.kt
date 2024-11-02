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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.otomasyon.R
import com.example.otomasyon.databinding.FragmentKatogoriEkleBinding
import com.example.otomasyon.databinding.FragmentUrunEkleBinding
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.roomdb.KatagoriDAO
import com.example.otomasyon.roomdb.KatagoriDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class KatogoriEkleFragment : Fragment() {
    private  var _binding: FragmentKatogoriEkleBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissonLauncher : ActivityResultLauncher<String>
    private  lateinit var  activityResultLauncher: ActivityResultLauncher<Intent>
    private  var secilenGorsel : Uri? = null
    private  var secilenBitmap : Bitmap? = null
    private  val mDisposable = CompositeDisposable()

    private lateinit var db:KatagoriDatabase
    private lateinit var katagoriDao : KatagoriDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        db = Room.databaseBuilder(requireContext(),KatagoriDatabase::class.java,"Katagoriler").build()
        katagoriDao = db.katagoriDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKatogoriEkleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.katagoriImageView.setOnClickListener{
             goreselSec(it)
        }
        binding.katagoriKaydetButton.setOnClickListener {
            KatogoriEkle(it)
        }
    }

    private fun KatogoriEkle(view: View){
        try {
            val katagoriName = binding.editKatagoriName.text.toString()
            if (katagoriName != "" ){
                if (secilenBitmap != null){
                    val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
                    val outputStream = ByteArrayOutputStream()
                    kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    val byteDizisi = outputStream.toByteArray()
                    val Katagori = Katagori(byteDizisi,katagoriName)

                    mDisposable.add(
                        katagoriDao.InsertKatagori(Katagori)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::handleResponseForKatagoriInsert)
                    )
                    Toast.makeText(requireContext(),"Kayıt Başarılı",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(),"Kayıt Yapılamadı",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),"Katagori İsmini Boş Bırakmayınız...!",Toast.LENGTH_SHORT).show()
            }

        }catch (e:Exception){
            Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT).show()
        }


    }
    private fun handleResponseForKatagoriInsert(){
        findNavController().popBackStack()
    }

    fun goreselSec(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                    //snackbar göstermemiz lazım,
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin ver",View.OnClickListener {
                            permissonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                }else{
                    permissonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                            binding.katagoriImageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.katagoriImageView.setImageBitmap(secilenBitmap)
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

    private fun kucukBitmapOlustur(kullanicininSectigiBitmap : Bitmap,maximumBoyut:Int):Bitmap{
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