package com.inspection.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.inspection.R
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.adapter.VehicleTypesListAdapter
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_vehicles_fragment_in_scope_of_services_view.*
import java.util.*

//vehicleType_textviewVal

/**
 * A simple [Fragment] subclass.
 */
class VehiclesFragmentInScopeOfServicesView : Fragment() {


    var DomesticVehiclesListView: ExpandableHeightGridView? = null
    internal var arrayAdapter: VehicleTypesListAdapter? = null
        var domesticVehiclesListItems=ArrayList<TypeTablesModel.vehicleMakesType>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_vehicles_fragment_in_scope_of_services_view, container, false)
        DomesticVehiclesListView = view.findViewById(R.id.DomesticVehiclesListView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (progressbarVehicleServices != null) {
//            progressbarVehicleServices.visibility = View.VISIBLE
//        }

        setServices()


    }



    private fun setServices() {


        for (model in TypeTablesModel.getInstance().VehicleMakesType) {

            for (model2 in TypeTablesModel.getInstance().VehiclesMakesCategoryType){


                //****** nothin to relate between two arrays vehicle category and vehicle make name , cuz ids type are not same *****//


                if (model.VehMakeTypeId==model2.VehCategoryID){

                    if (model2.VehCategoryName.toString().contains("Domest")) {

                        domesticVehiclesListItems.add(model)
                    }
                    Toast.makeText(context,domesticVehiclesListItems.size.toString(),Toast.LENGTH_SHORT).show()



                }
            }

        }






        arrayAdapter = VehicleTypesListAdapter(context!!, R.layout.vehicle_services_item, domesticVehiclesListItems)

        DomesticVehiclesListView?.adapter = arrayAdapter
        DomesticVehiclesListView?.isExpanded=true


//
//
//            arrayAdapter5 = DatesListAdapter(context!!, R.layout.vehicle_services_item, GlassServicesListItems)
//
//        GlassServicesListView?.adapter = arrayAdapter5
//        GlassServicesListView?.isExpanded=true
//
//
//
//
//            arrayAdapter6 = DatesListAdapter(context!!, R.layout.vehicle_services_item, OtherServicesListItems)
//
//        OtherServicesListView?.adapter = arrayAdapter6
//        OtherServicesListView?.isExpanded=true
//
//

        //  vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehicleServicesListItems)

        //   prepareView()
//        if (progressbarVehicleServices != null) {
//
//            progressbarVehicleServices.visibility = View.INVISIBLE
//        }
    }

    fun spinnerFilling(){

        var vehiclesTypesArray = ArrayList<String>()
        for (fac in TypeTablesModel.getInstance().VehiclesType) {


            vehiclesTypesArray.add(fac.VehiclesTypeName)
        }
        var programsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, vehiclesTypesArray)
        programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleType_textviewVal.adapter = programsAdapter

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
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): VehiclesFragmentInScopeOfServicesView {
            val fragment = VehiclesFragmentInScopeOfServicesView()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
