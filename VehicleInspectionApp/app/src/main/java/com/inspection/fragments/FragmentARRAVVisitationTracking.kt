package com.inspection.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.inspection.MainActivity

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arrav_visitation_tracking.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVVisitationTracking.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVVisitationTracking.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVVisitationTracking : Fragment() {

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

        takePhotoButton.setOnClickListener {
                if (ContextCompat.checkSelfPermission((activity as MainActivity),
                                Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((activity as MainActivity),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    val simpleAlert = AlertDialog.Builder(context)
                    simpleAlert.setTitle("Options")
                    simpleAlert.setItems(arrayOf("Blank Canvas", "Take Photo", "Pick Photo")) { dialog, which ->
                        if (which == 1) {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(takePictureIntent, 66)
                        } else if (which == 2) {
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)
                        }
                    }
                    simpleAlert.show()
                }else{
                Toast.makeText(context, "Please make sure camera and storage permissions are granted", Toast.LENGTH_LONG).show()
            }
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
