package com.inspection.fragments


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import kotlinx.android.synthetic.main.fragment_safety_check_search_customer.view.*
import org.json.JSONException
import org.json.JSONObject
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.inspection.MainActivity
import com.inspection.model.UserProfileModel
import com.inspection.serverTasks.GetShopUsersTask
import kotlinx.android.synthetic.main.fragment_safety_check_search_customer.*
import java.util.ArrayList


/**
 * Created by sheri on 5/26/2017.
 */
class FragmentSafetyCheckSearchForCustomerVehicle : Fragment() {

    var userProfileModels = ArrayList<UserProfileModel>()
    var customerNames = ArrayList<String>()
    var searchResultNames = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater?.inflate(R.layout.fragment_safety_check_search_customer, container, false)

        startloadingUsers()

        myView!!.searchView.setOnQueryTextFocusChangeListener(View.OnFocusChangeListener{ view: View, b: Boolean ->
            if (b){
                myView.topImage!!.visibility = View.GONE
            }else{
                myView.topImage!!.visibility = View.VISIBLE
            }
        })
        myView!!.searchView.queryHint = "Search Customer Name..."
        myView!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.v("text submitted","text submitted with: "+p0)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                searchResultNames = customerNames.filter { it.contains(query, true) } as ArrayList<String>
                val myAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchResultNames)
                searchCustomerSafetyCheckListView.adapter = myAdapter
                searchCustomerSafetyCheckListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, position: Int, l: Long ->
                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    for (item in userProfileModels){
                        if (customerNames.get(position).equals(item.firstName + " " + item.lastName)){
                            var fragment = Fragment()
                            fragment = FragmentSafetyCheckSelectVehicle()
                            fragment.selectedUserName = item.userName
                            fragment.selectecMobileUserProfileID = item.mobileUserProfileId
                            val fragmentManagerSC = fragmentManager
                            val ftSC = fragmentManagerSC!!.beginTransaction()
                            ftSC.replace(R.id.fragment, fragment)
                            ftSC.commit()
                            break
                        }
                    }
                })
                return true

            }

        })

        (activity as MainActivity).supportActionBar!!.title = ApplicationPrefs.getInstance(context).safetyCheckProgramName
        return myView
    }

    private fun startloadingUsers() {
        val progress = ProgressDialog(context)
        progress.isIndeterminate = true
        progress.setCancelable(false)
        progress.setMessage("Loading...")
        progress.show()
        object : GetShopUsersTask(context!!, ApplicationPrefs.getInstance(context).userProfilePref.accountID) {
            override fun onTaskCompleted(result: String) {
                progress.dismiss()
                if (result.contains("IsSuccess\":true")) {
                    try {
                        val jObject = JSONObject(result.toString())
                        val profileResult = jObject
                                .getJSONObject("ShopUsersByAccountIDResult")

                        val usersOfShopResult = profileResult
                                .getJSONArray("ShopUserProfiles")

                        //Log.dMainActivity.TAG, usersOfShopResult.toString())

                        userProfileModels = Gson().fromJson<ArrayList<UserProfileModel>>(usersOfShopResult.toString(), object : TypeToken<ArrayList<UserProfileModel>>() {
                        }.type)

                        for (item in userProfileModels){
                            customerNames.add(item.firstName + " " + item.lastName)
                        }

                        val myAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, customerNames)
                        searchCustomerSafetyCheckListView.adapter = myAdapter
                        searchCustomerSafetyCheckListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, position: Int, l: Long ->
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            for (item in userProfileModels){
                                if (customerNames.get(position).equals(item.firstName + " " + item.lastName)){
                                    var fragment = Fragment()
                                    fragment = FragmentSafetyCheckSelectVehicle()
                                    fragment.selectedUserName = item.userName
                                    fragment.selectecMobileUserProfileID = item.mobileUserProfileId
                                    val fragmentManagerSC = fragmentManager
                                    val ftSC = fragmentManagerSC!!.beginTransaction()
                                    ftSC.replace(R.id.fragment, fragment)
                                    ftSC.commit()
                                    break
                                }
                            }
                        })


                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                        //Log.dMainActivity.TAG, jsonException.message)
                    }

                } else {

                }
            }
        }.execute()
    }



}