package com.inspection.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.model.FacilityDataModel
import com.inspection.model.TblLanguage
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.lang_checkbox_item.view.*
import java.util.ArrayList

class LanguageListAdapter(internal var context: Context, internal var recource: Int, objects: List<TypeTablesModel.languageType>) : ArrayAdapter<TypeTablesModel.languageType>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.languageType>

    init {
        this.namesList = objects
        namesList = objects
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(recource, parent, false)
//        val textView3 = view.itemTextView
        val checkBoxItem = view.itemCheckBox
        checkBoxItem.text = namesList.get(position).LangTypeName
        //   checkBoxItem.isEnabled=false


        for (model in TypeTablesModel.getInstance().LanguageType) {
            for (model2 in FacilityDataModel.getInstance().tblLanguage) {
                if (namesList.get(position).LangTypeID == model2.LangTypeID) {
                    checkBoxItem.isChecked = true
                }
            }
        }

        if (checkBoxItem.isChecked) {
            checkBoxItemnum++
            var lang = TblLanguage()
            lang.LangTypeID = namesList.get(position).LangTypeID
            if (langArray.filter { s -> s.LangTypeID.equals(lang.LangTypeID) }.size==0) {
                langArray.add(lang)

            }
        }

        checkBoxItem.setOnClickListener(View.OnClickListener {
            if (checkBoxItem.isChecked == true) {
                checkBoxItemnum++
                var lang = TblLanguage()
                lang.LangTypeID = namesList.get(position).LangTypeID
                langArray.add(lang)
            } else {
                langArray.removeIf { s -> s.LangTypeID == namesList.get(position).LangTypeID }
                checkBoxItemnum--
            }
            FacilityDataModel.getInstance().tblLanguage = langArray
            //// SAVE REQUIRED LOGIC REMAINING
        })
        return view
    }

    companion object {
        var checkBoxItemnum = 0
        val langArray = ArrayList<TblLanguage>()
    }
}

