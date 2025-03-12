package com.example.pcxlogin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView

class ShopFragment : Fragment() {

    private lateinit var cardProcessor: CardView
    private lateinit var cardMotherboard: CardView
    private lateinit var cardGraphicsCard: CardView
    private lateinit var cardMemory: CardView
    private lateinit var cardSSD: CardView
    private lateinit var cardMonitor: CardView
    private lateinit var cardKeyboard: CardView
    private lateinit var cardMouse: CardView
    private lateinit var cardPcCase: CardView
    private lateinit var cardLaptops: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        // Initialize cards
        cardProcessor = view.findViewById(R.id.card_processor)
        cardMotherboard = view.findViewById(R.id.card_motherboard)
        cardGraphicsCard = view.findViewById(R.id.card_graphics_card)
        cardMemory = view.findViewById(R.id.card_memory)
        cardSSD = view.findViewById(R.id.card_ssd)
        cardMonitor = view.findViewById(R.id.card_monitor)
        cardKeyboard = view.findViewById(R.id.card_keyboard)
        cardMouse = view.findViewById(R.id.card_mouse)
        cardPcCase = view.findViewById(R.id.card_pc_case)
        cardLaptops = view.findViewById(R.id.card_laptops)

        // Set click listeners for each card
        cardProcessor.setOnClickListener {
            startActivity(Intent(requireActivity(), ProcessorActivity::class.java))
        }

        cardMotherboard.setOnClickListener {
            startActivity(Intent(requireActivity(), MotherboardActivity::class.java))
        }

        cardGraphicsCard.setOnClickListener {
            startActivity(Intent(requireActivity(), GraphicsCardActivity::class.java))
        }

        cardMemory.setOnClickListener {
            startActivity(Intent(requireActivity(), MemoryActivity::class.java))
        }

        cardSSD.setOnClickListener {
            startActivity(Intent(requireActivity(), SSDActivity::class.java))
        }

        cardMonitor.setOnClickListener {
            startActivity(Intent(requireActivity(), MonitorActivity::class.java))
        }

        cardKeyboard.setOnClickListener {
            startActivity(Intent(requireActivity(), KeyboardActivity::class.java))
        }

        cardMouse.setOnClickListener {
            startActivity(Intent(requireActivity(), MouseActivity::class.java))
        }

        cardPcCase.setOnClickListener {
            startActivity(Intent(requireActivity(), PcCaseActivity::class.java))
        }

//        cardLaptops.setOnClickListener {
//            startActivity(Intent(requireActivity(), LaptopsActivity::class.java))
//        }

        return view
    }
}
