package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.inspection.R
import com.inspection.Utils.apiToAppFormat
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.fragment_arrav_visitation_tracking.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVVisitationTracking.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVVisitationTracking.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVVisitationTracking : Fragment() {

    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_visitation_tracking   , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FacilityDataModel.getInstance().tblVisitationTracking[0].apply{
            //visitationDateButton.text = DatePerformed
     //       performedByButton.text = performedBy
            aarSignEditText.setText(AARSigns)
            certificateOfApprovalEditText.setText(CertificateOfApproval)
            memberBenefitsPosterEditText.setText(MemberBenefitPoster)
            qualityControlProcessEditText.setText(QualityControl)
            staffTrainingProcessEditText.setText(StaffTraining)
        }
        performed_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                performed_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        recieved_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                recieved_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        entered_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                entered_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        performedBy_dropdown.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName +" " + s.LastName}.distinct())
        enteredBydropdown.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName +" " + s.LastName}.distinct())
        newVisitTrackingAddBtn.setOnClickListener({
            var validProgram = true
//            for (fac in facilityProgramsList) {
//                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
//                    context!!.toast("Program Name cannot be duplicated")
//                    validProgram=false
//                }
//            }
            if (validProgram) {
                var item = FacilityDataModel.TblVisitationTracking()
                item.performedBy =performedBy_dropdown.selectedItem.toString()
                Toast.makeText(context,performedBy_dropdown.selectedItem.toString(),Toast.LENGTH_SHORT).show()
                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.DatePerformed = if (performed_date_textviewVal.text.equals("SELECT DATE")) "" else performed_date_textviewVal.text.toString()
//                item.expDate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString()
//                item.Comments=comments_editTextVal.text.toString()
                FacilityDataModel.getInstance().tblVisitationTracking.add(item)
                //  BuildProgramsList()

                addTheLatestRowOfPortalAdmin()

            }
        })

        fillPortalTrackingTableView();


//        takePhotoButton.setOnClickListener {
//                if (ContextCompat.checkSelfPermission((activity as MainActivity),
//                                Manifest.permission.CAMERA)
//                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((activity as MainActivity),
//                                Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_GRANTED) {
//                    val simpleAlert = AlertDialog.Builder(context)
//                    simpleAlert.setTitle("Options")
//                    simpleAlert.setItems(arrayOf("Blank Canvas", "Take Photo", "Pick Photo")) { dialog, which ->
//                        if (which == 1) {
//                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                            startActivityForResult(takePictureIntent, 66)
//                        } else if (which == 2) {
//                            val intent = Intent()
//                            intent.type = "image/*"
//                            intent.action = Intent.ACTION_GET_CONTENT
//                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)
//                        }
//                    }
//                    simpleAlert.show()
//                }else{
//                context!!.toast("Please make sure camera and storage permissions are granted")
//            }
//        }

    }
    fun fillPortalTrackingTableView() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        FacilityDataModel.getInstance().tblVisitationTracking.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                textView.text = get(it).DatePerformed.apiToAppFormat()
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).performedBy
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
//                textView.text = get(it).EnteredDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
              //  textView.text = get(it).ClearedDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            //  textView.text = get(it).Comments
                tableRow.addView(textView)


                deficiencyTableLayout.addView(tableRow)
            }
        }
    }

    fun addTheLatestRowOfPortalAdmin() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        FacilityDataModel.getInstance().tblVisitationTracking[FacilityDataModel.getInstance().tblVisitationTracking.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = DatePerformed
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = performedBy
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
          //  textView.text = EnteredDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        //    textView.text = ClearedDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        //    textView.text = Comments
            tableRow.addView(textView)


            deficiencyTableLayout.addView(tableRow)

        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        fun newInstance(param1: String, param2: String): FragmentARRAVVisitationTracking {
            val fragment = FragmentARRAVVisitationTracking()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
