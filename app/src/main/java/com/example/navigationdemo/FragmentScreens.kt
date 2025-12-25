package com.example.navigationdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.navigator.api.Navigator
import com.example.navigator.api.NavigatorProvider

class FragmentHomeScreen : Fragment() {

    private val navigator: Navigator
        get() = (requireActivity() as NavigatorProvider).navigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnDetails).setOnClickListener {
            navigator.navigate(AppRoutes.FragmentDetails("ORD-12345"), "fragmentHome_click")
        }

        view.findViewById<Button>(R.id.btnCompose).setOnClickListener {
            navigator.navigate(AppRoutes.ComposeHome, "fragmentHome_to_compose")
        }

        view.findViewById<Button>(R.id.btnLegacy).setOnClickListener {
            navigator.navigate(AppRoutes.LegacyActivity, "fragmentHome_to_legacy")
        }
    }
}

class FragmentDetailsScreen : Fragment() {

    private val navigator: Navigator
        get() = (requireActivity() as NavigatorProvider).navigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = arguments?.getString("orderId") ?: "Unknown"
        view.findViewById<TextView>(R.id.orderIdText).text = "Order ID: $orderId"

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            navigator.back("fragmentDetails_back")
        }
    }
}
