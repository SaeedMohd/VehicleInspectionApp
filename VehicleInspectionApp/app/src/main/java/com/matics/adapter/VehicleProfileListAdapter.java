package com.matics.adapter;

import java.util.List;
import java.util.Map;

import com.matics.model.VehicleProfileModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VehicleProfileListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;

	private final Context context;
	protected List<VehicleProfileModel> values = null;
	public VehicleProfileListAdapter(Context context, List<VehicleProfileModel> result) {
		super();
		this.values =  result;
		this.context = context;

	}
	@Override
	public int getCount() {
		return values.size();
	}
	@Override
	public Object getItem(int position) {
		return values.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		vi = inflater.inflate(R.layout.profile_row, null);
		//Log.e("", "In Getview");
//		TextView Device_Id = (TextView)vi.findViewById(R.id.textViewDeviceId);
//		TextView Vehicle_Id = (TextView)vi.findViewById(R.id.textViewProfile);
//		final EditText Year = (EditText)vi.findViewById(R.id.EdittextYear);
//		 EditText Make = (EditText)vi.findViewById(R.id.EdittextMake);
//		final EditText Model = (EditText)vi.findViewById(R.id.EdittextModel);
//		 EditText Mileage = (EditText)vi.findViewById(R.id.EdittextMileage);
//		 final EditText vehicle = (EditText)vi.findViewById(R.id.Edittext);
		  
//		 Button Update = (Button)vi.findViewById(R.id.buttonupdate);
		
//		Device_Id.setTag(position);
//		Vehicle_Id.setTag(position);
//		Year.setTag(position);
//		Make.setTag(position);
//		Model.setTag(position);
//		Mileage.setTag(position);
//		Update.setTag(position);
		
		
//		 VehicleProfileModel pm = values.get(position);
//		Device_Id.setText(pm.Device_Id);
//		Vehicle_Id.setText(pm.Vin);
//		Year.setText(""+values.get(position).year);
//		Make.setText("android");
//		Model.setText(pm.model);
//		Mileage.setText(pm.mileage);
//		
//		Update.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				int tag = (Integer) v.getTag();
//				//Log.e("", "Update Button is clicked and tag is :" + tag);
//				// Update Database 
//				DataBaseHelper DB = new DataBaseHelper(context);
//				VehicleProfileModel pro_up = new VehicleProfileModel(pm.Vin, "", Integer.valueOf(Year.getText().toString()), Make.getText().toString(), Model.getText().toString(), Mileage.getText().toString(), pm.Device_Id); 
//			}
//		});
		return vi;
	}

}
