package com.matics.serverTasks


import java.util.ArrayList
import java.util.HashMap
import org.json.JSONArray
import org.json.JSONObject

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.matics.MainActivity
import com.matics.R
import com.matics.SecondLogin
import com.matics.adapter.AutocompleteAdapter
import com.matics.adapter.MyObject
import com.matics.model.UserAccountModel
import com.matics.Utils.GPSTracker
import com.matics.Utils.MyService
import com.matics.Utils.Utility

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText


class GetAllAccountDataTask(private val mContext: Context, private val facilityName: EditText, private val editTextPincode: EditText, internal var mapView: MapView, internal var googleMap: GoogleMap) : AsyncTask<String, Int, Any>() {
    private val mProgressDialog: ProgressDialog?
    private val VIn: String? = null
    private val make: String? = null
    private val model: String? = null
    private val year: String? = null
    private val IsSuccess = "true"
    //	private ArrayAdapter<String> adapter;
    private val adapter: AutocompleteAdapter? = null
    internal var gps: GPSTracker? = null
    // adapter for auto-complete
    internal var myAdapter: ArrayAdapter<MyObject>? = null
    internal var aList: MutableList<HashMap<String, String>>
    internal var latitute: Double? = 0.0
    internal var current_latitude: Double? = 0.0
    internal var curent_longitude: Double? = 0.0
    internal var longitude: Double? = 0.0

    init {
        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.setMessage("Please wait...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setCancelable(false)
        aList = ArrayList<HashMap<String, String>>()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (mProgressDialog != null && !mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    override fun doInBackground(vararg params: String): Any? {
        GetAllAccountData()
        return null
    }

    override fun onPostExecute(result: Any) {
        super.onPostExecute(result)
        if (mProgressDialog != null && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }

        //---------------Custom AUto complete
        //        String[] from = {"txt","address","url"};
        //        int[] to = {com.matics.R.id.title,com.matics.R.id.textview_address,com.matics.R.id.icon};
        //        com.matics.adapter.SimpleAdapter adapter = new com.matics.adapter.SimpleAdapter(mContext, aList, com.matics.R.layout.drawer_list_item, from, to);
        //        /** Defining an itemclick event listener for the autocompletetextview */
        //        OnItemClickListener itemClickListener = new OnItemClickListener() {
        //        	@Override
        //        	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        //        		/** Each item in the adapter is a HashMap object.
        //        		 *  So this statement creates the currently clicked hashmap object
        //        		 * */
        //        		HashMap<String, String> hm = (HashMap<String, String>) arg0.getAdapter().getItem(position);
        //        		/** Getting a reference to the TextView of the layout file activity_main to set Currency */
        ////        		TextView tvCurrency = (TextView) findViewById(R.id.tv_currency) ;
        //        		/** Getting currency from the HashMap and setting it to the textview */
        ////        		tvCurrency.setText("Currency : " + hm.get("cur"));
        //        		//Log.e("", "selected");
        //        		//Log.e("", "address is:" +Utility.getlatlong(hm.get("address"),mContext));
        //        		setCurrentMarker(Utility.getlatlong(hm.get("address"), mContext));
        //        	}
        //		};
        //        /** Setting the adapter to the listView */
        //        facilityName.setAdapter(adapter);
        //        facilityName.setOnItemClickListener(itemClickListener);
        //
        //        editTextPincode.setAdapter(adapter);

        //		SecondLogin.facilityArray = new String[1];
        //		SecondLogin.facilityArray[0] = "About Service Automotive Repair Center";
        //		adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,SecondLogin.facilityArray);

        // set the custom ArrayAdapter

        //		final MyObject[] onj =new MyObject[SecondLogin.facilityArray.length];
        //
        //		for(int i=0;i<SecondLogin.facilityArray.length;i++){
        //			onj[i] = new MyObject(SecondLogin.facilityArray[i]);
        //		}
        //        myAdapter = new AutocompleteCustomArrayAdapter(mContext,onj);
        //        facilityName.setAdapter(myAdapter);
        //		adapter = new AutocompleteAdapter(mContext, SecondLogin.facilityArray);
        //		facilityName.setThreshold(1);
        //		facilityName.setOnItemSelectedListener(new OnItemSelectedListener() {
        //			@Override
        //			public void onItemSelected(AdapterView<?> parent, View view,
        //					int position, long id) {
        //				// TODO Auto-generated method stub
        //				//Log.e("", "Position is :"+position);
        //				facilityName.setText(""+onj[position]);
        //			}
        //			@Override
        //			public void onNothingSelected(AdapterView<?> parent) {
        //				// TODO Auto-generated method stub
        //
        //			}
        //		});
        //		facilityName.setAdapter(adapter);
    }

    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }

    private fun GetAllAccountData(): Any? {
        //		Utility.VehiclePofileData.clear();
        var result: String? = null
        val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAllAccounts?"
        try {
            val values = ContentValues()
            if (!Utility.isMyServiceRunning(mContext)) {
                values.put("BtId", "".toString())
            } else {
                values.put("BtId", MyService.devString.toString())
            }
            result = Utility.postRequest(getServerPath, values)

            //------------------------------------Parsing

            val jObject = JSONObject(result!!.toString())
            val j1 = jObject.getJSONObject("GetAllAccountsResult")
            val j2 = j1.getJSONArray("AccountUsers")
            getAllprofilledata = ArrayList<UserAccountModel>()
            val gson = Gson()
            var userAccountModel = UserAccountModel()
            userAccountModel = gson.fromJson(j1.toString(), UserAccountModel::class.java)
            getAllprofilledata.add(userAccountModel)

            for (i in getAllprofilledata.indices) {
                //Log.e("", "Accoun is :" + getAllprofilledata[i].accountFullName)
                val hm = HashMap<String, String>()
                hm.put("txt", getAllprofilledata[i].accountFullName)
                hm.put("address", getAllprofilledata[i].address.trim { it <= ' ' }.toString() + ",\n" + getAllprofilledata[i].city.trim { it <= ' ' }.toString() + ",\n" + getAllprofilledata[i].zip.trim { it <= ' ' }.toString() + ",\n" + getAllprofilledata[i].phone.trim { it <= ' ' }.toString())
                hm.put("url", getAllprofilledata[i].photoUrl)
                aList.add(hm)
            }
            SecondLogin.facilityArray = arrayOfNulls<String>(getAllprofilledata.size)
            for (i in getAllprofilledata.indices) {
                SecondLogin.facilityArray[i] = getAllprofilledata[i].accountFullName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun setCurrentMarker(Address: String) {
        // TODO Auto-generated method stub
        try {


            gps = GPSTracker(MainActivity.mContext)
            if (gps!!.canGetLocation()) {
                current_latitude = gps!!.latitude
                curent_longitude = gps!!.longitude
            } else {
                gps!!.showSettingsAlert()
            }


            var add: Array<String>? = null
            if (Address.contains(",")) {
                add = Address.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                latitute = java.lang.Double.parseDouble(add[0])
                longitude = java.lang.Double.parseDouble(add[1])
            } else {

            }
            try {

                googleMap.clear()
                val cameraPosition = CameraPosition.Builder().target(LatLng(java.lang.Double.parseDouble("33.127115"), java.lang.Double.parseDouble("-117.101726"))).zoom(16f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.map_icon)
                googleMap.addMarker(MarkerOptions()
                        .icon(icon)
                        .position(LatLng(java.lang.Double.parseDouble(add!![0]), java.lang.Double.parseDouble(add[1]))).title(""))
                googleMap.uiSettings.isZoomControlsEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false


                googleMap.setOnMarkerClickListener {
                    val intent = Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="
                                    + latitute + ","
                                    + longitude
                                    + "&daddr=" + current_latitude + "," + curent_longitude))

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
                    mContext.startActivity(intent)

                    false
                    // TODO Auto-generated method stub
                }

                googleMap.setOnMapClickListener {
                    // TODO Auto-generated method stub
                    val intent = Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="
                                    + latitute + ","
                                    + longitude
                                    + "&daddr=" + current_latitude + "," + curent_longitude))

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity")
                    mContext.startActivity(intent)
                }


            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }

        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    companion object {
        lateinit var getAllprofilledata: ArrayList<UserAccountModel>
    }

}
