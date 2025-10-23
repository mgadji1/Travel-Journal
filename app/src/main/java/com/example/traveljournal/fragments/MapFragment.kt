package com.example.traveljournal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.traveljournal.R
import com.example.traveljournal.db.AppDatabase
import com.example.traveljournal.db.Trip
import com.example.traveljournal.db.TripDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {
    private lateinit var mapView : MapView
    private lateinit var db : AppDatabase
    private lateinit var tripDao : TripDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm_prefs", 0))
        Configuration.getInstance().userAgentValue = ctx.packageName

        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        db = AppDatabase.getDatabase(requireContext())
        tripDao = db.tripDao()

        loadTripsAndAddMarkers()
    }

    private fun loadTripsAndAddMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val trips = withContext(Dispatchers.IO) {
                tripDao.getAllTrips()
            }

            addMarkers(trips)
        }
    }

    private fun addMarkers(trips : List<Trip>) {
        val mapController = mapView.controller
        if (trips.isEmpty()) return

        val firstTrip = trips.firstOrNull()!!

        mapController.setZoom(12.0)
        mapController.setCenter(GeoPoint(firstTrip.latitude, firstTrip.longitude))

        for (trip in trips) {
            val lat = trip.latitude
            val lon = trip.longitude

            val marker = Marker(mapView)
            marker.position = GeoPoint(lat, lon)
            marker.title = trip.title
            marker.subDescription = trip.date
            marker.setOnMarkerClickListener { _, _ ->
                showTripDetailsBottomSheet(trip)
                true
            }

            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    private fun showTripDetailsBottomSheet(trip : Trip) {
        val bottomSheet = TripDetailsFragment.newInstance(trip)
        bottomSheet.show(parentFragmentManager, BOTTOM_SHEET_TAG)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}