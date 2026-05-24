package com.inspection.Utils

import android.app.Dialog
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import com.inspection.databinding.ActivityFormsBinding
import com.inspection.databinding.SearchDialogBinding
import com.inspection.model.AAAFacilityComplete
//import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
//import kotlinx.android.synthetic.main.search_dialog.*
//import kotlinx.android.synthetic.main.search_dialog.view.*
import java.util.ArrayList



/**
 * Created by devsherif on 3/17/18.
 */
class SearchDialog(context: Context?, var arrayList: ArrayList<String>) : Dialog(context!!), View.OnClickListener {

    var searchResultArrayList: ArrayList<String>? = null
    var selectedString = ""
    private lateinit var binding: SearchDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SearchDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val halfWidth = (context?.resources?.displayMetrics?.widthPixels?.times(0.5))?.toInt()
            ?: ViewGroup.LayoutParams.WRAP_CONTENT
        window?.setLayout(halfWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.closeDialogBtn.setOnClickListener { dismiss() }

        searchResultArrayList = arrayList

        binding.searchDialogListView.adapter = ArrayAdapter<String>(context, R.layout.search_dialog_item, android.R.id.text1, searchResultArrayList!!)

        binding.searchDialogEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchResultArrayList = ArrayList<String>(arrayList.filter { obj -> obj.contains(s.toString().trim(), true) })
                binding.searchDialogListView.adapter = ArrayAdapter<String>(context, R.layout.search_dialog_item, android.R.id.text1, searchResultArrayList!!)
            }
        })

        binding.searchDialogListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
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