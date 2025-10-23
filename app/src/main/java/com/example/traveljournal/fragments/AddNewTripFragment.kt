package com.example.traveljournal.fragments

import android.content.Intent
import java.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
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
    private var editingTrip : Trip? = null

    private lateinit var imagePreview : ImageView
    private var selectedImageUri : Uri? = null

    private lateinit var db : AppDatabase
    private lateinit var tripDao : TripDao

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri : Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedImageUri = it
            imagePreview.setImageURI(it)
        }
    }

    private lateinit var btnAddTrip : Button
    private lateinit var edTitle : EditText
    private lateinit var edDescription : EditText
    private lateinit var tvLocation : TextView

    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            editingTrip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                 bundle.getParcelable(ARG_TRIP, Trip::class.java)
            } else {
                bundle.getParcelable(ARG_TRIP)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.new_trip_toolbar)
        showToolbar(toolbar)

        db = AppDatabase.getDatabase(requireContext())
        tripDao = db.tripDao()

        val btnLocation = view.findViewById<Button>(R.id.btnLocation)
        tvLocation = view.findViewById(R.id.tvLocation)

        btnAddTrip = view.findViewById(R.id.btnAddTrip)
        edTitle = view.findViewById(R.id.edTitle)
        edDescription = view.findViewById(R.id.edDescription)

        imagePreview = view.findViewById(R.id.imagePreview)
        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)

        recoverData()

        btnLocation.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, PickLocationFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        parentFragmentManager.setFragmentResultListener(
            LOCATION_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            lat = bundle.getDouble(LATITUDE_KEY)
            lon = bundle.getDouble(LONGITUDE_KEY)

            setLocation(lat!!, lon!!)
        }

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        val currentDate = getCurrentDate()

        btnAddTrip.setOnClickListener {
            createOrEditTrip(lat, lon, currentDate)
        }
    }

    private fun recoverData() {
        editingTrip?.let { trip ->
            lat = trip.latitude
            lon = trip.longitude
            edTitle.setText(trip.title)
            setLocation(trip.latitude, trip.longitude)
            selectedImageUri = trip.imageUri.toUri()
            imagePreview.setImageURI(selectedImageUri)
            edDescription.setText(trip.description)
            btnAddTrip.text = getString(R.string.edit_trip)
        }
    }

    private fun showToolbar(toolbar : Toolbar) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val title = if (editingTrip != null) getString(R.string.edit_trip) else getString(R.string.add_new_trip)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun getCurrentDate() : String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(Date())

        return formattedDate
    }

    private fun createOrEditTrip(lat : Double?, lon : Double?, currentDate : String) {
        if (editingTrip != null) {
            editTrip(lat, lon, editingTrip!!)
        } else {
            createNewTrip(lat, lon, currentDate)
        }
    }

    private fun createNewTrip(lat : Double?, lon : Double?, currentDate : String) {
        if (validateAllFields(lat, lon)) {
            val trip = Trip(
                imageUri = selectedImageUri.toString(),
                title = edTitle.text.toString(),
                date = currentDate,
                description = edDescription.text.toString(),
                latitude = lat!!,
                longitude = lon!!
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                tripDao.insertTrip(trip)
            }

            Toast.makeText(requireContext(), getString(R.string.trip_was_added), Toast.LENGTH_SHORT).show()

            clearAllFields()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.you_need_to_fill_all_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun editTrip(lat : Double?, lon : Double?, editingTrip : Trip) {
        if (validateAllFields(lat, lon)) {
            val trip = editingTrip.copy(
                imageUri = selectedImageUri.toString(),
                title = edTitle.text.toString(),
                date = editingTrip.date,
                description = edDescription.text.toString(),
                latitude = lat!!,
                longitude = lon!!
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                tripDao.updateTrip(trip)

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        getString(R.string.trip_was_updated), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.you_need_to_fill_all_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateAllFields(lat : Double?, lon : Double?) : Boolean {
        return edTitle.text.isNotEmpty()
                && edDescription.text.isNotEmpty()
                && selectedImageUri != null
                && lon != null
                && lat != null
    }

    private fun setLocation(lat : Double, lon : Double) {
        tvLocation.text = "📍 ${String.format("%.3f, %.3f", lat, lon)}"
    }

    private fun clearAllFields() {
        edTitle.text.clear()
        edDescription.text.clear()
        imagePreview.setImageDrawable(null)
        tvLocation.text = ""
    }

    companion object {
        private const val ARG_TRIP = "arg_trip"

        @JvmStatic
        fun newInstance(trip : Trip?) = AddNewTripFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_TRIP, trip)
            }
        }
    }
}