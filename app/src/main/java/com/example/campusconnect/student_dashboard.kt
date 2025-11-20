package com.example.campusconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusconnect.databinding.ActivityStudentDashboardBinding

class StudentDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var eventAdapter: EventAdapter
    private var eventList = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Setup Dummy Data (Later this comes from MySQL)
        loadDummyData()

        // 2. Setup RecyclerView
        setupRecyclerView()

        // 3. Setup Search
        setupSearch()

        // 4. Setup Filters
        setupFilters()

        // 5. Notification Click
        binding.ivNotifications.setOnClickListener {
            Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDummyData() {
        eventList.add(Event(1, "Science Fair", "2025-11-25", "Main Hall", "Annual Science Project Showcase", "Academic"))
        eventList.add(Event(2, "Basketball Finals", "2025-11-28", "Gymnasium", "Lions vs Tigers", "Sports"))
        eventList.add(Event(3, "Coding Bootcamp", "2025-12-01", "Lab 3", "Learn Kotlin in a day", "Academic"))
        eventList.add(Event(4, "Christmas Party", "2025-12-15", "Quadrangle", "Food, Music and Fun", "Social"))
    }

    private fun setupRecyclerView() {
        // Initialize Adapter with a Click Listener for "View Details"
        eventAdapter = EventAdapter(eventList) { selectedEvent ->
            // Code to open Event Details Activity goes here
            Toast.makeText(this, "Clicked: ${selectedEvent.title}", Toast.LENGTH_SHORT).show()

            // Example Intent (Uncomment when you create EventDetailsActivity):
            /*
            val intent = Intent(this, EventDetailsActivity::class.java)
            intent.putExtra("EVENT_TITLE", selectedEvent.title)
            intent.putExtra("EVENT_DESC", selectedEvent.description)
            startActivity(intent)
            */
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = eventAdapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                eventAdapter.filterList(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                when (chipId) {
                    R.id.chipAll -> eventAdapter.applyCategoryFilter("All")
                    R.id.chipAcademic -> eventAdapter.applyCategoryFilter("Academic")
                    R.id.chipSports -> eventAdapter.applyCategoryFilter("Sports")
                }
            }
        }
    }
}