package com.example.pcxlogin

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment() {



    private lateinit var viewPager: ViewPager2
    private val imageList = listOf(
        R.drawable.ad1,    // Make sure these exist in res/drawable
        R.drawable.ad2,
        R.drawable.ad3
    )

    private val sliderHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.slider)
        viewPager.adapter = context?.let { SliderAdapter(it, imageList) }

        // Auto-slide logic
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % imageList.size
                viewPager.setCurrentItem(nextItem, true)
                sliderHandler.postDelayed(this, 3000) // Change every 3 sec
            }
        }
        sliderHandler.postDelayed(runnable, 3000)

        // Handle View Product button clicks
        val viewProductBtn1 = view.findViewById<Button>(R.id.viewProductBtn1)
        val viewProductBtn2 = view.findViewById<Button>(R.id.viewProductBtn2)
        val viewProductBtn3 = view.findViewById<Button>(R.id.viewProductBtn3)
        val viewProductBtn4 = view.findViewById<Button>(R.id.viewProductBtn4)
        val viewProductBtn5 = view.findViewById<Button>(R.id.viewProductBtn5)
        val viewProductBtn8 = view.findViewById<Button>(R.id.viewProductBtn8)
        val viewProductBtn9 = view.findViewById<Button>(R.id.viewProductBtn9)
        val viewProductBtn10 = view.findViewById<Button>(R.id.viewProductBtn10)
        val viewProductBtn12 = view.findViewById<Button>(R.id.viewProductBtn12)
        val viewProductBtn13 = view.findViewById<Button>(R.id.viewProductBtn13)
        val viewProductBtn14 = view.findViewById<Button>(R.id.viewProductBtn14)
        val viewProductBtn15 = view.findViewById<Button>(R.id.viewProductBtn15)
        val viewProductBtn16 = view.findViewById<Button>(R.id.viewProductBtn16)
        val viewProductBtn17 = view.findViewById<Button>(R.id.viewProductBtn17)
        val viewProductBtn18 = view.findViewById<Button>(R.id.viewProductBtn18)
        val viewProductBtn19 = view.findViewById<Button>(R.id.viewProductBtn19)
        val viewProductBtn20 = view.findViewById<Button>(R.id.viewProductBtn20)

        viewProductBtn1.setOnClickListener {
            showProductDialog(R.layout.dialog_msimag)
        }

        viewProductBtn2.setOnClickListener {
            showProductDialog(R.layout.dialog_asrock)
        }

        viewProductBtn3.setOnClickListener {
            showProductDialog(R.layout.dialog_rogstrix)
        }

        viewProductBtn4.setOnClickListener {
            showProductDialog(R.layout.dialog_asusprime)
        }

        viewProductBtn5.setOnClickListener {
            showProductDialog(R.layout.dialog_rogstrix2)
        }

        viewProductBtn8.setOnClickListener {
            showProductDialog(R.layout.dialog_rtx4060)
        }

        viewProductBtn9.setOnClickListener {
            showProductDialog(R.layout.dialog_gtx1650)
        }

        viewProductBtn10.setOnClickListener {
            showProductDialog(R.layout.dialog_gigabyte4080)
        }

        viewProductBtn12.setOnClickListener {
            showProductDialog(R.layout.dialog_palit)
        }

        viewProductBtn13.setOnClickListener {
            showProductDialog(R.layout.dialog_galax4070ti)
        }

        viewProductBtn14.setOnClickListener {
            showProductDialog(R.layout.dialog_laptop)
        }

        viewProductBtn15.setOnClickListener {
            showProductDialog(R.layout.dialog_monitor)
        }

        viewProductBtn16.setOnClickListener {
            showProductDialog(R.layout.dialog_mouse)
        }

        viewProductBtn17.setOnClickListener {
            showProductDialog(R.layout.dialog_keyboard)
        }

        viewProductBtn18.setOnClickListener {
            showProductDialog(R.layout.dialog_laptop2)
        }

        viewProductBtn19.setOnClickListener {
            showProductDialog(R.layout.dialog_mouse2)
        }

        viewProductBtn20.setOnClickListener {
            showProductDialog(R.layout.dialog_monitor2)
        }

        return view
    }
    private fun showProductDialog(layoutResId: Int) {
        val dialogView = layoutInflater.inflate(layoutResId, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onDestroyView() {
        sliderHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }


}