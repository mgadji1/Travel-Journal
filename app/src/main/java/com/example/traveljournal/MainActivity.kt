package com.example.traveljournal

import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView = findViewById<BottomNavigationView>(R.id.navView)

        setUpNavViewRoutes(navView)
    }

    private fun setUpNavViewRoutes(navView : BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tripsFragment -> {
                    openFragment(TripsFragment.newInstance())
                    true
                }
                R.id.mapFragment -> {
                    openFragment(MapFragment.newInstance())
                    true
                }
                R.id.addFragment -> {
                    openFragment(AddNewTripFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment : Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}