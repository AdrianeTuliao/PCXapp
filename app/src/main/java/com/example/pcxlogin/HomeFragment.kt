package com.example.pcxlogin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return view
    }

    override fun onDestroyView() {
        sliderHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}