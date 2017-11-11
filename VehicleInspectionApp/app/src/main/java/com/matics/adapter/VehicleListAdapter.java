package com.matics.adapter;

import java.util.List;

import com.matics.LivePhoneReadings;
import com.matics.R;
import com.matics.model.VehicleProfileModel;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

public class VehicleListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private final Context context;
	protected List<VehicleProfileModel> values = null;
	public VehicleListAdapter(Context context, List<VehicleProfileModel> result) {
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
		vi = inflater.inflate(R.layout.profile1_row, null);
		TextView VehicleID = (TextView)vi.findViewById(R.id.textViewVehicleid);
		TextView textView_header = (TextView)vi.findViewById(R.id.textView_header);
		final EditText Year = (EditText)vi.findViewById(R.id.editTextYear);
		final EditText make = (EditText)vi.findViewById(R.id.editTextmake);
		final EditText model = (EditText)vi.findViewById(R.id.editTextmodel);
		final EditText mileage = (EditText)vi.findViewById(R.id.editTextmileage);
		final Button Update = (Button)vi.findViewById(R.id.buttonUpdate);
		final TableRow li_num = (TableRow)vi.findViewById(R.id.tableRow_num);
		final TableRow li_state = (TableRow)vi.findViewById(R.id.tableRow_state);
		final EditText Li_number = (EditText)vi.findViewById(R.id.editTextLicence_num);
		final EditText Li_states = (EditText)vi.findViewById(R.id.editTextLicence_State);
		VehicleID.setTag(position);
		Year.setTag(position);
		make.setTag(position);
		model.setTag(position);
		mileage.setTag(position);
		Update.setTag(position);
		li_num.setTag(position);
		li_state.setTag(position);
		Li_number.setTag(position);
		Li_states.setTag(position);
		
//--------making Typeface and applying to Edittext and Textview for custom font type
		Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/gill_sans.ttf");
		Year.setTypeface(tf);
		make.setTypeface(tf);
		model.setTypeface(tf);
		mileage.setTypeface(tf);
		Update.setTypeface(tf);
		Li_number.setTypeface(tf);
		Li_states.setTypeface(tf);
		if(values.get(position).VIN.length()!=17){
			textView_header.setText("MAC ID :");
			VehicleID.setText(values.get(position).BtID);
			li_num.setVisibility(0);
			li_state.setVisibility(0);
		}
		else{
			VehicleID.setText(values.get(position).VIN);
			li_num.setVisibility(8);
			li_state.setVisibility(8);
		}
		Year.setText(""+values.get(position).MFYear);
		make.setText(""+values.get(position).Make);
		model.setText(""+values.get(position).Model);
		mileage.setText(""+values.get(position).Mileage);
		if(values.get(position).License_Num.contains("null")){
			Li_number.setText("");
		}else{
			Li_number.setText(""+values.get(position).License_Num);
		}
		if(values.get(position).License_State.contains("null")){
			Li_states.setText("");
		}else{
			Li_states.setText(""+values.get(position).License_State);
		}
		if(!values.get(position).VinRetrievable){
//		Year.setEnabled(true);
//		make.setEnabled(true);
//		model.setEnabled(true);
//			Year.setEnabled(false);
//			make.setEnabled(false);
//			model.setEnabled(false);
//			mileage.setEnabled(true);
		}
		else{
		mileage.setEnabled(false);
		}
		Update.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int Tag =  (Integer) v.getTag();
				VehicleProfileModel pm = values.get(Tag);
				DataBaseHelper DB = new DataBaseHelper(context);
//				DB.updateprofile(new VehicleProfileModel(pm.Vin, pm.lastupdate,Integer.valueOf(Year.getText().toString()), make.getText().toString(), model.getText().toString(), mileage.getText().toString(), pm.Device_Id,"",Li_number.getText().toString(),Li_states.getText().toString(),""));
			}
		});
		try {
			VehicleProfileModel pm = values.get(position);
			if(pm.VIN.equalsIgnoreCase(LivePhoneReadings.getVin())){
				Update.setVisibility(0);
//				Year.setEnabled(true);
//				make.setEnabled(true);
//				model.setEnabled(true);
				mileage.setEnabled(true);
//				Li_number.setEnabled(true);
//				Li_states.setEnabled(true);
			}
			else{
				Update.setVisibility(8);
				Year.setEnabled(false);
				make.setEnabled(false);
				model.setEnabled(false);
				mileage.setEnabled(false);
				Li_number.setEnabled(false);
				Li_states.setEnabled(false);
				Update.setVisibility(8);
				mileage.setEnabled(true);
				
			}
		} 
		catch (Exception e) {
			Update.setVisibility(8);
			Year.setEnabled(false);
			make.setEnabled(false);
			model.setEnabled(false);
			mileage.setEnabled(false);
			Li_number.setEnabled(false);
			Li_states.setEnabled(false);
		}
		
		return vi;
	}
}
