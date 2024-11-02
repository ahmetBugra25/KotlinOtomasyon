package com.example.otomasyon.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.otomasyon.R
import com.example.otomasyon.databinding.FragmentAnaMenuBinding

class AnaMenuFragment : Fragment() {
    private  var _binding: FragmentAnaMenuBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnaMenuBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.urunEkleButton.setBackgroundColor(Color.parseColor("#051666"))
        binding.urunlerButton.setBackgroundColor(Color.parseColor("#051666"))
        binding.katagoriEkleButton.setBackgroundColor(Color.parseColor("#051666"))
        binding.urunEkleButton.setTextColor(Color.WHITE)
        binding.urunlerButton.setTextColor(Color.WHITE)
        binding.katagoriEkleButton.setTextColor(Color.WHITE)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.urunlerButton.setOnClickListener { UrunlerButton(it) }
        binding.urunEkleButton.setOnClickListener { UrunEkleButton(it) }
        binding.katagoriEkleButton.setOnClickListener { KatagoriEkleButton(it) }
        binding.katagoriDuzenleButton.setOnClickListener { KatagoriDuzenleButton(it) }
    }
    private fun KatagoriDuzenleButton(view: View){
        val action = AnaMenuFragmentDirections.actionAnaMenuFragmentToEditKatagoriFragment()
        Navigation.findNavController(view).navigate(action)
    }

    private fun UrunEkleButton(view: View){
        val action = AnaMenuFragmentDirections.actionAnaMenuFragmentToUrunEkleFragment()
        Navigation.findNavController(view).navigate(action)
    }

    private fun UrunlerButton(view: View){
        val action = AnaMenuFragmentDirections.actionAnaMenuFragmentToKatagorilerFragment()
        Navigation.findNavController(view).navigate(action)

    }
    private  fun KatagoriEkleButton(view: View){
        val action = AnaMenuFragmentDirections.actionAnaMenuFragmentToKatogoriEkleFragment()
        Navigation.findNavController(view).navigate(action)

    }



}