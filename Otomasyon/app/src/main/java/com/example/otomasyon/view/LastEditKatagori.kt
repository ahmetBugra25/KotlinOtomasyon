package com.example.otomasyon.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.otomasyon.databinding.FragmentEditKatagoriBinding
import com.example.otomasyon.databinding.FragmentLastEditKatagoriBinding
import com.example.otomasyon.models.Katagori
import com.example.otomasyon.roomdb.KatagoriDAO
import com.example.otomasyon.roomdb.KatagoriDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class LastEditKatagori : Fragment() {

    private  var _binding: FragmentLastEditKatagoriBinding? = null
    private val binding get() = _binding!!

    private  val mDisposable = CompositeDisposable()
    private lateinit var permissonLauncher : ActivityResultLauncher<String>
    private  lateinit var  activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var db: KatagoriDatabase
    private lateinit var katagoriDao : KatagoriDAO
    private var secilenKatagori : Katagori?= null
    private  var secilenGorsel : Uri? = null
    private  var secilenBitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        db = Room.databaseBuilder(requireContext(), KatagoriDatabase::class.java,"Katagoriler").build()
        katagoriDao = db.katagoriDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLastEditKatagoriBinding.inflate(inflater, container, false)
        val view = binding.root
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val id = LastEditKatagoriArgs.fromBundle(it).katagoriID
            mDisposable.add(
                katagoriDao.findByIdKatagori(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse)
            )
        }
        binding.katagoriImageView.setOnClickListener{
            goreselSec(it)
        }
        binding.button.setOnClickListener { KatagoriSil(it) }
        binding.katagoriKaydetButton.setOnClickListener { KatagoriGuncelle(it) }

    }
    private fun handleResponse(katagori : Katagori){
        val bitmap = BitmapFactory.decodeByteArray(katagori.katagoriGorseli,0,katagori.katagoriGorseli.size)
        binding.katagoriImageView.setImageBitmap(bitmap)
        binding.editKatagoriName.setText(katagori.katagoriName)
        secilenKatagori = katagori

    }
    private fun KatagoriSil(view: View){
        if (secilenKatagori !=null){
            mDisposable.add(
                katagoriDao.DeleteKatagori(secilenKatagori!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponseForUpdate)

            )
            Toast.makeText(requireContext(),"Katagori Silindi",Toast.LENGTH_SHORT).show()
        }
    }
    private fun KatagoriGuncelle(view: View){
        try {
            if (secilenKatagori !=null){
                mDisposable.add(
                    katagoriDao.DeleteKatagori(katagori = secilenKatagori!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                )
                val katagoriName = binding.editKatagoriName.text.toString()
                if(secilenBitmap != null){
                    val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
                    val outputStream = ByteArrayOutputStream()
                    kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    val byteDizisi = outputStream.toByteArray()
                    val katagori = Katagori(byteDizisi,katagoriName)

                    mDisposable.add(
                        katagoriDao.InsertKatagori(katagori)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::handleResponseForUpdate)
                    )
                    Toast.makeText(requireContext(),"Katagori Güncellendi",Toast.LENGTH_SHORT).show()
                }
            }


        }catch (e:Exception){
            Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG).show()
        }

    }
    private fun handleResponseForUpdate(){
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
                        "İzin ver Butonu",View.OnClickListener {
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
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
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