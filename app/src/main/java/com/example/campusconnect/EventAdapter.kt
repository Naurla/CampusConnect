package com.example.campusconnect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnect.databinding.ItemEventBinding
import java.util.Locale

class EventAdapter(
    private var eventList: MutableList<Event>,
    private val onItemClick: (Event) -> Unit // Function to handle clicks
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // copy of the list for filtering
    private var filteredList: MutableList<Event> = ArrayList(eventList)

    inner class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = filteredList[position]

        holder.binding.apply {
            tvEventTitle.text = event.title
            tvEventDate.text = event.date
            tvEventLocation.text = event.location

            // Handle "Interested" Button Appearance
            if (event.isAttending) {
                btnInterested.text = "Attending"
                btnInterested.setBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                btnInterested.text = "Interested?"
                btnInterested.setBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            }

            // Button Click Logic
            btnInterested.setOnClickListener {
                event.isAttending = !event.isAttending
                notifyItemChanged(position) // Refresh just this row
            }

            // Whole Row Click (View Details)
            root.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    override fun getItemCount(): Int = filteredList.size

    // Search Filter Logic
    fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(eventList)
        } else {
            val resultList = ArrayList<Event>()
            for (row in eventList) {
                if (row.title.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                    row.category.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
                    resultList.add(row)
                }
            }
            resultList
        }
        notifyDataSetChanged()
    }

    // Chip Filter Logic (Upcoming vs Past, etc)
    fun applyCategoryFilter(category: String) {
        filteredList = if (category == "All") {
            ArrayList(eventList)
        } else {
            val resultList = ArrayList<Event>()
            for (row in eventList) {
                if (row.category.contains(category)) {
                    resultList.add(row)
                }
            }
            resultList
        }
        notifyDataSetChanged()
    }
}