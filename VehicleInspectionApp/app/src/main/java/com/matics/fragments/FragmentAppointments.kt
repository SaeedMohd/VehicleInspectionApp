package com.matics.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.app.ProgressDialog
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.matics.MainActivity
import com.matics.R
import com.matics.Utils.ApplicationPrefs
import com.matics.adapter.AppointmentsRecyclerViewAdapter
import com.matics.adapter.SafetyCheckShopInitialRecyclerViewAdapter
import com.matics.model.AppointmentModel
import com.matics.model.SafetyCheckItemModel
import com.matics.model.SafetyCheckReportModel
import com.matics.serverTasks.GenericServerTask
import kotlinx.android.synthetic.main.fragment_appointments.*
import kotlinx.android.synthetic.main.fragment_appointments.view.*
import kotlinx.android.synthetic.main.fragment_safety_check_shop_initial.*
import kotlinx.android.synthetic.main.fragment_safety_check_shop_initial.view.*
import kotlinx.android.synthetic.main.fragment_safety_check_view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by sheri on 5/26/2017.
 */
class FragmentAppointments : Fragment() {

//    var appointmentRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val myView = inflater?.inflate(R.layout.fragment_appointments, container, false)
        (activity as MainActivity).supportActionBar!!.title = "Appointments"

        return myView as View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        appointmentRecyclerView.layoutManager = LinearLayoutManager(context)
        appointmentRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop){
            appointmentRecyclerView.visibility = View.GONE
            appointmentsMessageTextView.text = "This page displays Customer's Appointments with scheduled for their Vehicles. \nAs a shop user, nothing to display."
            appointmentsMessageTextView.visibility = View.VISIBLE
        }else {
            loadAppointments()
        }

    }

    fun loadAppointments(){
        val progressDialog = ProgressDialog(context)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("loading... ")
        progressDialog.show()

        var email = ApplicationPrefs.getInstance(context).userProfilePref.email
        var accountId = ApplicationPrefs.getInstance(context).userProfilePref.accountID
        var phoneNumber = ApplicationPrefs.getInstance(context).userProfilePref.phoneNumber
        var phoneNumber2 = ApplicationPrefs.getInstance(context).userProfilePref.phoneNumber2
        var phoneNumber3 = ApplicationPrefs.getInstance(context).userProfilePref.phoneNumber3

        if (email==null){
            email = ""
        }

        if (accountId==null){
            accountId = 0
        }

        if (phoneNumber==null){
            phoneNumber = ""
        }

        if (phoneNumber2==null){
            phoneNumber2 = ""
        }

        if (phoneNumber3==null){
            phoneNumber3  = ""
        }


        object: GenericServerTask(context, getString(R.string.appointmentsUrl), arrayOf("email", "accountId", "homePhone", "workPhone", "cellPhone"), arrayOf(email, ""+accountId, phoneNumber, phoneNumber2, phoneNumber3)){
            override fun onTaskCompleted(result: String) {
                progressDialog.dismiss()
                try {
                val jsonObject = JSONObject(result.toString())
                val jsonArray = jsonObject.getJSONArray("getAppointmentsListResult")
                val appointmentsList = Gson().fromJson<ArrayList<AppointmentModel>>(jsonArray.toString(), object : TypeToken<ArrayList<AppointmentModel>>() {

                }.type)


                    
                    if (appointmentsList.size > 0) {
                        appointmentRecyclerView.adapter = AppointmentsRecyclerViewAdapter(context, this@FragmentAppointments, appointmentRecyclerView, appointmentsList)
                    }else{
                        appointmentRecyclerView.visibility = View.GONE
                        appointmentsMessageTextView.text = "No Appointments To Show"
                        appointmentsMessageTextView.visibility = View.VISIBLE
                    }
                }catch (exp: Exception){
                    print(exp.printStackTrace())
                    appointmentsMessageTextView.text = "No Appointments To Show"
                    appointmentsMessageTextView.visibility = View.VISIBLE
                }
            }
        }.execute()
    }

}