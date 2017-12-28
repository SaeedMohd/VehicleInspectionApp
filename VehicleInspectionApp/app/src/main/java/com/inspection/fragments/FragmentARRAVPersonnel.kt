package com.inspection.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVPersonnel.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPersonnel : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravpersonnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //inputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//

        var personTypeArray = arrayOf("Assistant Manager",  "Body Shop Manager", "Cashier",  "Chief Executive Officer",  "Chief Financial Officer", "Controller",  "Delivery Driver", "Director", "Fixed Ops Director", "Floating Manager",  "Foreman", "General Manager", "General Partner", "General Service", "Limited Partner", "Manager", "Managing Member", "Marketing Manager", "Member", "Office Manager", "Owner", "Partner", "Parts and Service Director", "Parts Manager", "Porter", "President", "Registered Agent", "Retail Manager", "Secretary Service", "Director", "Service Manager", "Service Writer", "Shop Foreman", "Store Manager", "Supervisor", "Technician", "Treasurer", "Vice President")
        var personTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personTypeArray)
        personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personType_textviewVal.adapter = personTypeAdapter

        var statesArray = arrayOf("Alabama ","Alaska ","Arizona ","Arkansas ","California ","Colorado ","Connecticut ","Delaware ","Florida ","Georgia ","Hawaii ","Idaho ","Illinois","Indiana ","Iowa ","Kansas ","Kentucky ","Louisiana ","Maine ","Maryland ","Massachusetts ","Michigan ","Minnesota ","Mississippi ","Missouri ","Montana","Nebraska ","Nevada ","New Hampshire ","New Jersey ","New Mexico ","New York ","North Carolina ","North Dakota ","Ohio ","Oklahoma ","Oregon ","Pennsylvania","Rhode Island ","South Carolina ","South Dakota ","Tennessee ","Texas ","Utah ","Vermont ","Virginia ","Washington ","West Virginia ","Wisconsin ","Wyoming")
        var statesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, statesArray)
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coSignerStateVal.adapter = statesAdapter


    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVPersonnel.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVPersonnel {
            val fragment = FragmentARRAVPersonnel()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
