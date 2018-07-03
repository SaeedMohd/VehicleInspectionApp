package com.inspection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.inspection.model.FacilityDataModel
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
        val textView3 = view.itemTextView
        val checkBoxItem = view.itemCheckBox

        textView3.text = namesList.get(position).LangTypeName
        //   checkBoxItem.isEnabled=false


        for (model in TypeTablesModel.getInstance().LanguageType) {

            for (model2 in FacilityDataModel.getInstance().tblLanguage){

                if (namesList.get(position).LangTypeID==model2.LangTypeID){

                    checkBoxItem.isChecked=true


                }
            }

        }
        if (checkBoxItem.isChecked==true){

            checkBoxItemnum++
            langArray.add(namesList.get(position).LangTypeID)



        }



checkBoxItem.setOnClickListener(View.OnClickListener {


    if (checkBoxItem.isChecked==true){

        checkBoxItemnum++
        langArray.add(namesList.get(position).LangTypeID)
        Toast.makeText(context,langArray.toString(),Toast.LENGTH_SHORT).show()


    }else
        langArray.remove(namesList.get(position).LangTypeID)
    checkBoxItemnum--
   // langArray.remove(namesList.get(position).LangTypeID)



})



        return view
    }
companion object {

    var checkBoxItemnum=0
    val langArray= ArrayList<String>()


}
}

