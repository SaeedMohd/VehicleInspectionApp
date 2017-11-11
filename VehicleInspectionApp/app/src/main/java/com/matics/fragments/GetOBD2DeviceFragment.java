package com.matics.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.matics.MainActivity;
import com.matics.R;


public class GetOBD2DeviceFragment extends Fragment {
    View view;
    Button buyNowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Get OBD2 Device");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_get_obd2_device, container, false);

        buyNowButton = (Button) view.findViewById(R.id.iobd2BuyNowButton);

        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/uk/cgi-bin/webscr?business=jetcomcreative@gmail.com&" +
                        "notify_url=http://vehiclehealth.org/Purchase/PaypalNotify&return_url=http://vehiclehealth.org/Purchase/PaypalReturn&" +
                        "cmd=_xclick&item_name=iOBD2 Bluetooth double system&currency_code=USD&amount=55&item_number=1"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

}
