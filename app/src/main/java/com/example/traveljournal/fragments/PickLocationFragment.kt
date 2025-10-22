package com.example.traveljournal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.traveljournal.R
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

const val LOCATION_RESULT_KEY = "location_result"

const val LATITUDE_KEY = "latitude"
const val LONGITUDE_KEY = "longitude"

class PickLocationFragment : Fragment() {
    private lateinit var mapView : MapView
    private var tempMarker : Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm_prefs", 0))
        Configuration.getInstance().userAgentValue = ctx.packageName

        mapView = view.findViewById(R.id.pickLocationMapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)

        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                addTempMarker(p)
                sendCoordinatesBack(p.latitude, p.longitude)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        val overlay = MapEventsOverlay(receiver)
        mapView.overlays.add(overlay)
    }

    private fun addTempMarker(point : IGeoPoint) {
        tempMarker?.let {
            mapView.overlays.remove(it)
        }

        tempMarker = Marker(mapView).apply {
            position = GeoPoint(point.latitude, point.longitude)
            title = "You chose this point"
            mapView.overlays.add(this)
        }

        mapView.invalidate()
    }

    private fun sendCoordinatesBack(lat : Double, lon : Double) {
        parentFragmentManager.setFragmentResult(
            LOCATION_RESULT_KEY,
            bundleOf(
                LATITUDE_KEY to lat,
                LONGITUDE_KEY to lon
            )
        )
        parentFragmentManager.popBackStack()
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
        fun newInstance() = PickLocationFragment()
    }
}