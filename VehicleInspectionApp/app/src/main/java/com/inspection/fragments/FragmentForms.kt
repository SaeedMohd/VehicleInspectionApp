package com.inspection.fragments


import android.app.AlertDialog
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.Constants
import kotlinx.android.synthetic.main.fragment_forms.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


class FragmentForms : androidx.fragment.app.Fragment(), OnClickListener {

    var formsStringsArray = arrayOf("Visitation Planning", "APP / Ad Hoc Visitation", "My Performance")

    //another added code for frag testing > sherif yousry
   // var fragment2: VehiclesFragmentInScopeOfServicesView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        (activity as MainActivity).supportActionBar!!.title = "ACE AAR Inspection"
        return inflater.inflate(R.layout.fragment_forms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitationPlanningButton.setOnClickListener {
            var service = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

            var enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true

            if (!enabled) {
                var alertBuilder = AlertDialog.Builder(activity);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("GPS Location is required")
                alertBuilder.setMessage("GPS location is required within this app. ");
                alertBuilder.setPositiveButton("Agree") { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                alertBuilder.setNegativeButton("Disagree") { dialog, which ->

                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                (activity as MainActivity).supportActionBar!!.title = "Visitation Planning"
                var fragment = VisitationPlanningFragment()
                fragment!!.isVisitationPlanning = true
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment, fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
            }
        }

        adHocVisitationButton.setOnClickListener {
            var service = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            var enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true

            if (!enabled) {
                var alertBuilder = AlertDialog.Builder(activity);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("GPS Location is required")
                alertBuilder.setMessage("GPS location is required within this app. ");
                alertBuilder.setPositiveButton("Agree") { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                alertBuilder.setNegativeButton("Disagree") { dialog, which ->

                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                (activity as MainActivity).supportActionBar!!.title = "APP / Ad Hoc Visitation"
                var fragment = AppAdHockVisitationFilterFragment()
                fragment!!.isVisitationPlanning = false
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment, fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
            }
        }

        myPerformanceButton.setOnClickListener {
            val client = OkHttpClient()//.newBuilder().connectTimeout(50, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS)
            val request = Request.Builder()
                    .url("https://api-uat.national.aaa.com/common/oauth2/token?client_id=5d5f4i99gmj45pf5qpcnhuvr07&client_secret=1ifminse1q98jifo5qauk9207r01q2a9gvvku074bot5v560mdjb")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                    .addHeader("Authorization", "Bearer eyJraWQiOiJGMld5M2tKT3BDdDlBa1o2cWdiR1JuVGtIWlM4YldpanhTRkJJWnh1elh3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1ZDVmNGk5OWdtajQ1cGY1cXBjbmh1dnIwNyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoicmVzLWF1dG9tb3RpdmUtdWF0XC9yc3AtcHJveHkiLCJhdXRoX3RpbWUiOjE2ODc4MTIzMTMsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX084dVRUSXQxaCIsImV4cCI6MTY4NzgxNTkxMywiaWF0IjoxNjg3ODEyMzEzLCJ2ZXJzaW9uIjoyLCJqdGkiOiJkNjM4NzJmMi04MDZhLTRjZjItYjRlZS04ODJmZGRhYzdkMjYiLCJjbGllbnRfaWQiOiI1ZDVmNGk5OWdtajQ1cGY1cXBjbmh1dnIwNyJ9.dKlBvu-RP-NGIPE2xljnN30A7IUA1QlSkxuGPN9BfDXin2PjKZ2TZrVP50DMa6Qr1Ze9ysQwjntaM8i8TMQaskA6Ai0347oddbYgRAfOdkJVvnTpPe72aCuCmAfkudWC-1m8sty6ZUYcYTyh1rxFE2lj5xIUcojxlnMxp3MnA557gEb7Nhg_OdhK4Mk8ySnexdbIaV2Sza0KeFlx91Be2nBYrmkxFwVoXdxjTzHmKo43V-7-uZGr0EE7hm2aYL10VnJGv3avTBxseCWtjWmLarm-cJtTmFdo6xCiNBLdoC9MXqE4UBrhQkfw0-ENjnJZImlMLGhMjwfN51l--GoIWQ")
//                    .addHeader("Cookie", "incap_ses_188_2617556=C6hDZrXul1EMN84seumbAsD4mWQAAAAAY6lz3NR0oS55nCN9yowEGg==; nlbi_2617556_2600297=A8xdU7mGPBquGfyudOi6ugAAAABHABE40aQKY1pavAFmF4Da; nlbi_2617556_2795788=pOd8MYLDDBSKyW+ndOi6ugAAAADF94XM3G007nF3A24IYozk; visid_incap_2400341=L40Sp9/SRKWOyleCzvTfvXJFgmMAAAAAQUIPAAAAAAAO96pQC0Hi7+uSzubCW8Wl; visid_incap_2617556=SbLkk389QqSn5OD9+J8UkuE7dmQAAAAAQUIPAAAAAABn4f3tlNKK0dj6wKeEg2eG; XSRF-TOKEN=fa307f3b-24bf-4bfb-a823-2be0c6e43ea0")
                    .build()
            val response = client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("TOKEN --> ",e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.v("TOKEN --> ",response.toString())
                }
            })

            (activity as MainActivity).supportActionBar!!.title = "My Performance"
//            var fragment = PDFGenerateFragment()
//            val fragmentManagerSC = fragmentManager
//            val ftSC = fragmentManagerSC!!.beginTransaction()
//            ftSC.replace(R.id.fragment, fragment)
//            ftSC.addToBackStack("frag")
//            ftSC.commit()
        }
        //button added for fragments testing only > sherif yousry
//        fragmentTester.setOnClickListener {
//            fragment2 = VehiclesFragmentInScopeOfServicesView()
//          //  fragment!!.isVisitationPlanning = false
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment2)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
////                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//        }

//        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, formsStringsArray)
//        formsListView.adapter = arrayAdapter
//
//        formsListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
//            if (i == 0){
////                val fragment: android.support.v4.app.Fragment
////                fragment = FragmentAnnualVisitationPager()
////                val fragmentManagerSC = fragmentManager
////                val ftSC = fragmentManagerSC!!.beginTransaction()
////                ftSC.replace(R.id.fragment,fragment)
////                ftSC.addToBackStack("")
////                ftSC.commit()
////                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//
//                fragment = VisitationPlanningFragment()
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//            }
//        })

    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub

    }

    override fun onDestroy() {
        super.onDestroy()
    }



}
