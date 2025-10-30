package com.example.pcxlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.example.pcxlogin.ProcessorActivity

class ShopFragment : Fragment() {
    private lateinit var cardProcessor: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        cardProcessor = view.findViewById(R.id.card_processor)
        cardProcessor.setOnClickListener {
            val intent = Intent(activity, ProcessorActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}