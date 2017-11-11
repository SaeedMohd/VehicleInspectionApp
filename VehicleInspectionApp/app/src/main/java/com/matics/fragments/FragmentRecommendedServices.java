package com.matics.fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.matics.MainActivity;
import com.matics.R;
import com.matics.model.VehicleProfileModel;
import com.matics.model.VehicleServicesModel;
import com.matics.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentRecommendedServices extends Fragment {

    private TextView vehicleDetails, noRecommendedServicesTextView;
    private ListView recommendedServicesListView;
    private VehicleProfileModel vehicleProfileModel;
    private ArrayList<VehicleServicesModel> vehicleRecommendedServicesModelArrayList= new ArrayList<>();;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vehicleProfileModel = (VehicleProfileModel) getArguments().getSerializable("vehicleProfile");

        View view = inflater.inflate(R.layout.fragment_vehicle_recommended_services, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.recommendedServicesProgressBar);
        vehicleDetails = (TextView) view.findViewById(R.id.vehicleDetailsTextView);
        vehicleDetails.setText(vehicleProfileModel.getMake() + " " + vehicleProfileModel.getModel() + " " + vehicleProfileModel.getYear());

        noRecommendedServicesTextView = (TextView) view.findViewById(R.id.noRecommendedServicesTextView);

        recommendedServicesListView = (ListView) view.findViewById(R.id.recommendedServicesListView);

        new GetRecommendedServicesTask().execute();

        return view;
    }


    private class GetRecommendedServicesTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            getRecommendedServicesRequest();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            if(vehicleRecommendedServicesModelArrayList.size()==0){
                noRecommendedServicesTextView.setVisibility(View.VISIBLE);
            }else {
                recommendedServicesListView.setAdapter(new VehicleRecommendedServicesAdapter());
                recommendedServicesListView.setVisibility(View.VISIBLE);
            }
        }

        private String getRecommendedServicesRequest() {
            String result = null;

            try {
                ContentValues values = new ContentValues();
                values.put("VINID", vehicleProfileModel.getVINID());

                result = Utility.postRequest(getString(R.string.vehicle_recommended_services_url), values);

                JSONObject jObject = new JSONObject(result.toString());
                JSONObject ProfileResult = jObject.getJSONObject("GetVehicleRecommendedServiceResult");
                JSONArray j2 = ProfileResult.getJSONArray("VehicleRecommendedService");


                VehicleServicesModel vehicleRecommendedServicesModel;
                for (int i = 0; i < j2.length(); i++) {
                    vehicleRecommendedServicesModel = new VehicleServicesModel();
                    vehicleRecommendedServicesModel.setServiceDesc(j2.getJSONObject(i).getString("ServiceDesc"));
                    vehicleRecommendedServicesModel.setServiceDate(j2.getJSONObject(i).getString("ServiceDate"));

                    vehicleRecommendedServicesModelArrayList.add(vehicleRecommendedServicesModel);
                }

                //Log.dMainActivity.TAG, ""+vehicleRecommendedServicesModelArrayList.get(0).getServiceDesc());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private class VehicleRecommendedServicesAdapter extends BaseAdapter {

        LayoutInflater inflater;

        VehicleRecommendedServicesAdapter() {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public int getCount() {
            return vehicleRecommendedServicesModelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;


            if (view == null) {
                view = inflater.inflate(R.layout.vehicle_service_item, null);
            }

            final TextView vehicleServiceDescription = (TextView) view.findViewById(R.id.vehicleServiceDescription);
            final TextView vehicleServiceDate = (TextView) view.findViewById(R.id.vehicleServiceDate);

            vehicleServiceDescription.setText(vehicleRecommendedServicesModelArrayList.get(position).getServiceDesc());
            vehicleServiceDate.setText(vehicleRecommendedServicesModelArrayList.get(position).getServiceDate());


            return view;

        }
    }

}
