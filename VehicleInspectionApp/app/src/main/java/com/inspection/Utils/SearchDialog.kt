package com.inspection.Utils

import android.app.Dialog
import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
import com.inspection.model.AAAFacilityComplete
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.search_dialog.*
import kotlinx.android.synthetic.main.search_dialog.view.*
import java.util.ArrayList



/**
 * Created by devsherif on 3/17/18.
 */
class SearchDialog(context: Context?, var arrayList: ArrayList<String>) : Dialog(context!!), View.OnClickListener {

    var searchResultArrayList: ArrayList<String>? = null
    var selectedString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.search_dialog)

        searchResultArrayList = arrayList

        searchDialogListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchResultArrayList!!)

        searchDialogEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchResultArrayList = ArrayList<String>(arrayList.filter { obj -> obj.contains(s.toString().trim(), true) })
                searchDialogListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchResultArrayList!!)
            }
        })

        searchDialogListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            selectedString = searchResultArrayList!!.get(i)
            dismiss()
        })
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    override fun onClick(v: View?) {
        selectedString = when (v!!.id) {
            R.id.searchDialogListView -> {
                "Eshta"
            }
            else -> {
                "Eshtaaaaaa"
            }
        }
        dismiss()
    }

}