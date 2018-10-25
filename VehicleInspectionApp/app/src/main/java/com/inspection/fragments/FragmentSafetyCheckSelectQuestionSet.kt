package com.inspection.fragments


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.serverTasks.GenericServerTask
import org.json.JSONException
import org.json.JSONObject
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.inspection.MainActivity
import com.inspection.model.SafetyCheckQuestionSetModel
import kotlinx.android.synthetic.main.fragment_safety_check_search_customer.*
import java.util.ArrayList


/**
 * Created by sheri on 5/26/2017.
 */
class FragmentSafetyCheckSelectQuestionSet : Fragment() {

    var safetyCheckQuestionSetModels = ArrayList<SafetyCheckQuestionSetModel>()
    var selectedMobileUserProfileID = 0
    var selectedVehicleID = -1
    var questionSetNames = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater?.inflate(R.layout.fragment_safety_check_select_question_set, container, false)
        startloadingQuestionSets()


        (activity as MainActivity).supportActionBar!!.title = ApplicationPrefs.getInstance(context).safetyCheckProgramName
        return myView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView.visibility = View.GONE
        selectVehicleHeaderTextView.text = "Select Questions Set"
        selectVehicleHeaderTextView.visibility = View.VISIBLE
    }

    private fun startloadingQuestionSets() {
        val progress = ProgressDialog(context)
        progress.isIndeterminate = true
        progress.setCancelable(false)
        progress.setMessage("Loading...")
        progress.show()
        object : GenericServerTask(context!!, context!!.getString(R.string.GetSafetyCheckQuestionSetsForAccount), arrayOf("accountID"), arrayOf(""+ApplicationPrefs.getInstance(context).userProfilePref.accountID)) {
            override fun onTaskCompleted(result: String) {
                progress.dismiss()
                if (result.contains("questionSetName")) {
                    try {
                        val jObject = JSONObject(result.toString())
                        val profileResult = jObject
                                .getJSONArray("GetSafetyCheckQuestionSetsForAccountResult")



                        safetyCheckQuestionSetModels = Gson().fromJson<ArrayList<SafetyCheckQuestionSetModel>>(profileResult.toString(), object : TypeToken<ArrayList<SafetyCheckQuestionSetModel>>() {

                        }.type)


                        for (item in safetyCheckQuestionSetModels){
                            questionSetNames.add(item.questionSetName)
                        }

                        val myAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, questionSetNames)
                        searchCustomerSafetyCheckListView.adapter = myAdapter
                        searchCustomerSafetyCheckListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, position: Int, l: Long ->
                            Log.v("item clicked ", "item clicked'");
                            for (item in safetyCheckQuestionSetModels){
                                if (safetyCheckQuestionSetModels.get(position).questionSetName.equals(item.questionSetName)){
                                    val fragment = FragmentSafetyCheckItems()
                                    fragment.selectedMobileUserProfileID = selectedMobileUserProfileID
                                    fragment.selectVehicleID = selectedVehicleID
                                    fragment.safetyCheckReportID = -1
                                    fragment.selectedQuestionSetID = safetyCheckQuestionSetModels.get(position).id
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