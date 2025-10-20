package com.example.traveljournal.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.db.AppDatabase
import com.example.traveljournal.db.Trip
import com.example.traveljournal.db.TripDao
import com.example.traveljournal.trips.OnTripClickListener
import com.example.traveljournal.trips.TripAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val BOTTOM_SHEET_TAG = "TripBottomSheet"

class TripsFragment : Fragment() {
    private lateinit var db : AppDatabase
    private lateinit var tripsDao : TripDao

    private lateinit var adapter : TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setHasOptionsMenu(true)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = TripAdapter(layoutInflater, requireContext(), object : OnTripClickListener {
            override fun onClick(trip : Trip) {
                val bottomSheet = TripDetailsFragment.newInstance(trip)
                bottomSheet.show(parentFragmentManager, BOTTOM_SHEET_TAG)
            }
        })

        recyclerView.adapter = adapter

        db = AppDatabase.getDatabase(requireContext())
        tripsDao = db.tripDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val trips = withContext(Dispatchers.IO) {
                tripsDao.getAllTrips()
            }
            adapter.setTrips(trips)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.search_trip)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    performSearch(it)
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun performSearch(query : String) {
        val searchQuery = "%$query%"
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val trips = tripsDao.searchByQuery(searchQuery)
            launch(Dispatchers.Main) {
                adapter.setTrips(trips)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }

            R.id.action_filter -> {
                showFilterMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterMenu() {
        val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)

        val popUpMenu = PopupMenu(requireContext(), toolbar, Gravity.END)

        popUpMenu.menu.add(getString(R.string.new_ones_first))
        popUpMenu.menu.add(getString(R.string.old_ones_first))

        popUpMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                getString(R.string.new_ones_first) -> sortTripsByDate(false)
                getString(R.string.old_ones_first) -> sortTripsByDate(true)
            }
            true
        }

        popUpMenu.show()
    }

    private fun sortTripsByDate(ascending : Boolean) {
        val sortedTrips = adapter.getTrips().sortedBy { it.date }
        if (ascending) adapter.setTrips(sortedTrips)
        else adapter.setTrips(sortedTrips.reversed())
    }

    companion object {
        @JvmStatic
        fun newInstance() = TripsFragment()
    }
}