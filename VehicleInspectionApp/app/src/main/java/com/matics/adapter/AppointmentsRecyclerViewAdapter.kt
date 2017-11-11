package com.matics.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.matics.R
import com.matics.RecyclerViewClickListener

import java.util.ArrayList

import com.matics.fragments.FragmentAppointments
import com.matics.model.AppointmentModel

/**
 * Created by sheri on 5/8/2017.
 */

class AppointmentsRecyclerViewAdapter(private val context: Context, private val parentView: FragmentAppointments, private val appointmentRecyclerView: RecyclerView, private val appointmentsArrayList: ArrayList<AppointmentModel>?) : RecyclerView.Adapter<AppointmentsRecyclerViewAdapter.ViewHolder>() {

    companion object {
        private var itemListener: RecyclerViewClickListener? = null
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.appointment_item_layout, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
      holder!!.appointmentDateTextView!!.text = appointmentsArrayList!!.get(position).AppointmentsDateTime
      holder!!.appointmentNotesTextView!!.text = appointmentsArrayList!!.get(position).Notes
      holder!!.appointmentStatusNotesTextView!!.text = appointmentsArrayList!!.get(position).StatusNotes
    }

    override fun getItemCount(): Int {
        return appointmentsArrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var appointmentDateTextView: TextView? = null
        var appointmentNotesTextView: TextView? = null
        var appointmentStatusNotesTextView: TextView? = null

        init{
            appointmentDateTextView = itemView.findViewById<TextView>(R.id.appointmentDateTextView)
            appointmentNotesTextView = itemView.findViewById<TextView>(R.id.appointmentNotes)
            appointmentStatusNotesTextView = itemView.findViewById<TextView>(R.id.appointmentStatusNotes)
        }
    }
}