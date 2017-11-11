package com.matics.fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.matics.R;
import com.matics.model.VehicleProfileModel;
import com.matics.model.VehicleServicesModel;
import com.matics.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentUpcomingServices extends Fragment {

    private TextView vehicleDetails, noUpcomingServicesTextView;
    private ListView upcomingServicesListView;
    private VehicleProfileModel vehicleProfileModel;
    private ArrayList<VehicleServicesModel> vehicleServicesModelArrayList = new ArrayList<>();;
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

        View view = inflater.inflate(R.layout.fragment_vehicle_upcoming_services, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.upcomingServicesProgressBar);
        vehicleDetails = (TextView) view.findViewById(R.id.vehicleDetailsTextView);
        vehicleDetails.setText(vehicleProfileModel.getMake() + " " + vehicleProfileModel.getModel() + " " + vehicleProfileModel.getYear());

        noUpcomingServicesTextView = (TextView) view.findViewById(R.id.noUpcomingServicesTextView);

        upcomingServicesListView = (ListView) view.findViewById(R.id.upcomingServicesListView);

        new GetUpcomingServicesTask().execute();

        return view;
    }


    private class GetUpcomingServicesTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            getUpcomingServicesRequest();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            if(vehicleServicesModelArrayList.size()==0){
                noUpcomingServicesTextView.setVisibility(View.VISIBLE);
            }else {
                upcomingServicesListView.setAdapter(new VehicleUpcomingServicesAdapter());
                upcomingServicesListView.setVisibility(View.VISIBLE);
            }
        }

        private String getUpcomingServicesRequest() {
            String result = null;

            try {
                ContentValues values = new ContentValues();
                values.put("VINID", vehicleProfileModel.getVINID());

                result = Utility.postRequest(getString(R.string.vehicle_upcoming_services_url), values);

                JSONObject jObject = new JSONObject(result.toString());
                JSONObject ProfileResult = jObject.getJSONObject("GetVehicleUpcomingServiceResult");
                JSONArray j2 = ProfileResult.getJSONArray("VehicleUpcomingService");
                Gson gson = new Gson();
                VehicleServicesModel vehicleServicesModel;
                for (int i = 0; i < j2.length(); i++) {
                    vehicleServicesModel = gson.fromJson(j2.get(i).toString(), VehicleServicesModel.class);
                    vehicleServicesModelArrayList.add(vehicleServicesModel);
                }
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


    private class VehicleUpcomingServicesAdapter extends BaseAdapter {

        LayoutInflater inflater;

        VehicleUpcomingServicesAdapter() {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public int getCount() {
            return vehicleServicesModelArrayList.size();
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

            vehicleServiceDescription.setText(vehicleServicesModelArrayList.get(position).getServiceDesc());
            vehicleServiceDate.setText(vehicleServicesModelArrayList.get(position).getServiceDate());


            return view;

        }
    }

}
