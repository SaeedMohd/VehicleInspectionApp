package com.matics.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.matics.R;
import com.matics.SecondLogin;

public class FragmentConnectionMain extends Fragment implements OnClickListener {

	private Button connectButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_connect, container,
				false);
		connectButton = (Button) view.findViewById(R.id.connectToShopButton);
		connectButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.connectToShopButton:
			//Log.e("", "Connect is clicked");
			Fragment fragmentUser= new SecondLogin();
			FragmentManager fragmentManagerUser = getFragmentManager();
			FragmentTransaction ftUser = fragmentManagerUser.beginTransaction();
			ftUser.replace(R.id.fragment, fragmentUser);
			ftUser.commit();
			
			break;
		}

	}
}
