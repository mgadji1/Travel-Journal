package com.example.traveljournal.fragments

import java.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.traveljournal.R
import com.example.traveljournal.db.AppDatabase
import com.example.traveljournal.db.Trip
import com.example.traveljournal.db.TripDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class AddNewTripFragment : Fragment() {
    private lateinit var imagePreview : ImageView
    private var selectedImageUri : Uri? = null

    private lateinit var db : AppDatabase
    private lateinit var tripDao : TripDao

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri : Uri? ->
        uri?.let {
            selectedImageUri = it
            imagePreview.setImageURI(it)
        }
    }

    private lateinit var btnAddTrip : Button
    private lateinit var edName : EditText
    private lateinit var edDescription : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.new_trip_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.add_new_trip)

        db = AppDatabase.getDatabase(requireContext())
        tripDao = db.tripDao()

        val btnLocation = view.findViewById<Button>(R.id.btnLocation)
        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)

        btnLocation.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, PickLocationFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        var lat: Double? = null
        var lon: Double? = null

        parentFragmentManager.setFragmentResultListener(
            LOCATION_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            lat = bundle.getDouble(LATITUDE_KEY)
            lon = bundle.getDouble(LONGITUDE_KEY)

            tvLocation.text = "📍 ${String.format("%.3f, %.3f", lat, lon)}"
        }

        imagePreview = view.findViewById(R.id.imagePreview)
        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnAddTrip = view.findViewById(R.id.btnAddTrip)
        edName = view.findViewById(R.id.edName)
        edDescription = view.findViewById(R.id.edDescription)

        val currentDate = getCurrentDate()

        btnAddTrip.setOnClickListener {
            createNewTrip(lat, lon, currentDate)
        }
    }

    private fun getCurrentDate() : String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = formatter.format(Date())

        return formattedDate
    }

    private fun createNewTrip(lat : Double?, lon : Double?, currentDate : String) {
        if (edName.text.isNotEmpty()
            && edDescription.text.isNotEmpty()
            && selectedImageUri != null
            && lon != null
            && lat != null) {
            val trip = Trip(
                imageUri = selectedImageUri.toString(),
                name = edName.text.toString(),
                date = currentDate,
                description = edDescription.text.toString(),
                latitude = lat,
                longitude = lon
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                tripDao.insertTrip(trip)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddNewTripFragment()
    }
}