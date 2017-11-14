package com.inspection.adapter;

import java.util.List;

import com.inspection.R;
import com.inspection.Utils.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivityListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;

	private final Context context;
	protected List<String> values = null;
	public MainActivityListAdapter(Context context, List<String> result) {
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
		vi = inflater.inflate(R.layout.row_main_activity, null);
		TextView txtTitle=(TextView)vi.findViewById(R.id.row_mainactivity_textview);
		txtTitle.setTypeface(Utility.fonttypeface(context));
		ProgressBar progress=(ProgressBar)vi.findViewById(R.id.row_mainactivity_progress);
		progress.setVisibility(View.GONE);
		txtTitle.setText(values.get(position));
		return vi;
	}

	private void alertDialog(String msg) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage(msg);
		builder1.setCancelable(false);
		builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}
}
