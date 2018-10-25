package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aarav_software.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVSoftware.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVSoftware.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVSoftware : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aarav_software, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ReyCheck.text = "Reynolds & Reynolds"
    }


    fun onADBRadioButtonClicked (view : View) {

    }

    fun onALLDATARadioButtonClicked (view : View){

    }

    fun onArkonnaRadioButtonClicked (view : View){

    }

    fun onAutomakeRadioButtonClicked (view : View){

    }

    fun onAutosoftRadioButtonClicked (view : View){

    }

    fun onChiltonRadioButtonClicked (view : View){

    }

    fun onDelphiRadioButtonClicked (view : View){

    }

    fun onGarageRadioButtonClicked (view : View){

    }
    fun onInvoRadioButtonClicked (view : View){

    }
    fun onManufaturerRadioButtonClicked (view : View){

    }
    fun onMitchellRadioButtonClicked (view : View){

    }

    fun onNAPARadioButtonClicked (view : View){

    }
    fun onReyRadioButtonClicked (view : View){

    }
    fun onRORadioButtonClicked (view : View){

    }
    fun onScottRadioButtonClicked (view : View){

    }
    fun onOtherRadioButtonClicked (view : View){

    }
    fun onchargeTypeRadioButtonClicked (view : View){

    }
    fun onShopRadioButtonClicked (view : View){

    }
    fun onShopchargeTypeRadioButtonClicked (view : View){

    }

    fun onInPersonRadioButtonClicked (view : View){

    }

    fun onIntegratedRadioButtonClicked (view : View){

    }

    fun onOnLineRadioButtonClicked (view : View){

    }
    fun onTeleRadioButtonClicked (view : View){

    }fun onOrderOthersRadioButtonClicked (view : View){

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVSoftware.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVSoftware().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
