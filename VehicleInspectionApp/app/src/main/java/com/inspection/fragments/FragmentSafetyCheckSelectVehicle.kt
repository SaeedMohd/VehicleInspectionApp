package com.inspection.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import org.json.JSONException
import org.json.JSONObject
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.inspection.model.VehicleProfileModel
import com.inspection.serverTasks.GetVINsByUserTask
import kotlinx.android.synthetic.main.fragment_safety_check_search_customer.*
import java.util.ArrayList


/**
 * Created by sheri on 5/26/2017.
 */
class FragmentSafetyCheckSelectVehicle : Fragment() {

    var selectedUserName = ""
    var selectecMobileUserProfileID = -1
    var vehicleProfileModels = ArrayList<VehicleProfileModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater?.inflate(R.layout.fragment_safety_check_select_vehicle, container, false)

        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView.visibility = View.GONE
        selectVehicleHeaderTextView.visibility = View.VISIBLE
        startLoadingVehiclesForUser(selectedUserName)
    }

    private fun startLoadingVehiclesForUser(userName: String) {
        object : GetVINsByUserTask(context!!, userName) {
            override fun onTaskCompleted(result: String) {
                try {
                    val aObject = JSONObject(result.toString())
                    val jObject = aObject
                            .getJSONObject("GetVinsByUserResult")
                    val vehicleProfilesArray = jObject
                            .getJSONArray("VehicleProfiles")

                    vehicleProfileModels = Gson().fromJson<ArrayList<VehicleProfileModel>>(vehicleProfilesArray.toString(), object : TypeToken<ArrayList<VehicleProfileModel>>() {

                    }.type)

                    val vinsArray = arrayOfNulls<String>(vehicleProfileModels.size)
                    for (x in vinsArray.indices) {
                        if (vehicleProfileModels[x].make != null
                                && vehicleProfileModels[x].make.trim { it <= ' ' } != ""
                                && vehicleProfileModels[x].make != "0") {
                            vinsArray[x] = vehicleProfileModels[x].make + "-" + vehicleProfileModels[x].model + "-" + vehicleProfileModels[x].year
                        } else {
                            vinsArray[x] = vehicleProfileModels[x].vin
                        }
                    }

                    Log.v("number of vins", "number of vins = "+vinsArray.size)
                    var myAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, vinsArray)
                    searchCustomerSafetyCheckListView.adapter = myAdapter
                    searchCustomerSafetyCheckListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                        var fragment = Fragment()
                        fragment =  FragmentSafetyCheckSelectQuestionSet()
                        fragment.selectedMobileUserProfileID = selectecMobileUserProfileID
                        fragment.selectedVehicleID = vehicleProfileModels.get(i).vinid.toInt()
                        val fragmentManagerSC = fragmentManager
                        val ftSC = fragmentManagerSC!!.beginTransaction()
                        ftSC.replace(R.id.fragment, fragment)
                        ftSC.commit()
                    })

                } catch (e1: JSONException) {

                    e1.printStackTrace()
                }

            }
        }.execute()
    }

}