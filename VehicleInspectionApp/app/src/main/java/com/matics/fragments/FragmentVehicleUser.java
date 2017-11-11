package com.matics.fragments;

import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.imageloader.Utils;
import com.matics.model.UserProfileModel;
import com.matics.serverTasks.EditProfileInsideRequestTask;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.Utility;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Fragment;
import android.graphics.Typeface;
import android.widget.LinearLayout;

public class FragmentVehicleUser extends Fragment implements OnClickListener {

	private EditText firstNameEditText, lastNameEditText, userNameEditText, phoneEditText,
			phone2EditText, phone3EditText, emailEditText, email2EditText,
			email3EditText, addressEditText, cityEditText, streetEditText,
			zipEditText, facebookEditText;

    private Button changePasswordButton, btn_Create;
	Typeface tf;
    private AlertDialog changePasswordDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ------------initializing variables
		View view = inflater.inflate(R.layout.fragment_vehicle_user, container,
				false);

		initialize(view);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("User Profile");

        ImageView Bgimage = (ImageView) view.findViewById(R.id.imagebg);
        Utils.setShopImage(getActivity(), Bgimage);

		try {
			UserProfileModel userProfileModel = ApplicationPrefs.getInstance(
					getActivity()).getUserProfilePref();
			firstNameEditText.setText(userProfileModel.getFirstName());
			lastNameEditText.setText(userProfileModel.getLastName());
			userNameEditText.setText(userProfileModel.getUserName());
			emailEditText.setText(userProfileModel.getEmail());
			email2EditText.setText(userProfileModel.getEmail2());
			email3EditText.setText(userProfileModel.getEmail3());
			phoneEditText.setText(userProfileModel.getPhoneNumber());
			phone2EditText.setText(userProfileModel.getPhoneNumber2());
			phone3EditText.setText(userProfileModel.getPhoneNumber3());
			addressEditText.setText(userProfileModel.getAddress());
			streetEditText.setText(userProfileModel.getState());
			zipEditText.setText(userProfileModel.getZip());
			facebookEditText.setText(userProfileModel.getFacebook());
			cityEditText.setText(userProfileModel.getCity());
		} catch (Exception e) {
			e.printStackTrace();
		}



		return view;
	}

	private boolean fieldValidation() {
		boolean flag = true;
		if (!Utility.validateString(firstNameEditText.getText().toString())) {
			flag = false;
			Utility.showMessageDialog(getActivity(), "",
					"Please Enter First Name");
		} else if (!Utility.validateString(lastNameEditText.getText()
				.toString())) {
			flag = false;
			Utility.showMessageDialog(getActivity(), "",
					"Please Enter Last Name");
		} else if (!Utility.validateString(phoneEditText.getText().toString())) {
			flag = false;
			Utility.showMessageDialog(getActivity(), "",
					"Please Enter Phone Number");
		} else if (!Utility.isEmailValid(emailEditText.getText().toString())) {
			flag = false;
			Utility.showMessageDialog(getActivity(), "",
					"Please Enter Valid Email Address");
		}
		return flag;
	}

	private void initialize(View view) {
		// TODO Auto-generated method stub
		firstNameEditText = (EditText) view.findViewById(R.id.editTextFName);
		lastNameEditText = (EditText) view.findViewById(R.id.editTextLName);
		userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
		phoneEditText = (EditText) view.findViewById(R.id.editTextPhone);
		phone2EditText = (EditText) view.findViewById(R.id.editTextPhone2);
		phone3EditText = (EditText) view.findViewById(R.id.editTextPhone3);
		emailEditText = (EditText) view.findViewById(R.id.editTextEmail);
		email2EditText = (EditText) view.findViewById(R.id.editTextEmail2);
		email3EditText = (EditText) view.findViewById(R.id.editTextEmail3);
		addressEditText = (EditText) view.findViewById(R.id.editTextAddress);
		streetEditText = (EditText) view.findViewById(R.id.editTextStreet);
		facebookEditText = (EditText) view.findViewById(R.id.editTextFacebook);
		zipEditText = (EditText) view.findViewById(R.id.editTextZip);
		cityEditText = (EditText) view.findViewById(R.id.editTextCity);
		
		btn_Create = (Button) view.findViewById(R.id.buttonCreate);
        btn_Create.setOnClickListener(this);

        changePasswordButton = (Button) view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(this);

		tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/gill_sans.ttf");
		firstNameEditText.setTypeface(tf);
		lastNameEditText.setTypeface(tf);
		userNameEditText.setTypeface(tf);
		phoneEditText.setTypeface(tf);
		phone2EditText.setTypeface(tf);
		phone3EditText.setTypeface(tf);
		emailEditText.setTypeface(tf);
		email2EditText.setTypeface(tf);
		email3EditText.setTypeface(tf);
		addressEditText.setTypeface(tf);
		facebookEditText.setTypeface(tf);
		streetEditText.setTypeface(tf);
		zipEditText.setTypeface(tf);
		cityEditText.setTypeface(tf);
		
		// btn_Create.setTypeface(tf);



	}

	@Override
	public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCreate:
                //Log.e("", "Create button is clicked");
                Boolean isnet = Utility.checkInternetConnection(getActivity()); // Checking
                // internet
                // connection
                // is
                // Available
                // or
                // not
                if (isnet) {
                    if (fieldValidation()) { // Validation for Edittext. if all
                        // required fields are filled or
                        // not
                        UserProfileModel userProfileModel = new UserProfileModel();
                        userProfileModel.setFirstName(firstNameEditText.getText().toString());
                        userProfileModel.setLastName(lastNameEditText.getText().toString());
                        userProfileModel.setUserName(userNameEditText.getText().toString());
                        userProfileModel.setPhoneNumber(phoneEditText.getText().toString());
                        userProfileModel.setPhoneNumber2(phone2EditText.getText().toString());
                        userProfileModel.setPhoneNumber3(phone3EditText.getText().toString());
                        userProfileModel.setEmail(emailEditText.getText().toString());
                        userProfileModel.setEmail2(email2EditText.getText().toString());
                        userProfileModel.setEmail3(email3EditText.getText().toString());
                        userProfileModel.setAddress(addressEditText.getText().toString());
                        userProfileModel.setCity(cityEditText.getText().toString());
                        userProfileModel.setState(streetEditText.getText().toString());
                        userProfileModel.setZip(zipEditText.getText().toString());
                        userProfileModel.setFacebook(facebookEditText.getText().toString());
                        userProfileModel.setDeviceID("");
                        userProfileModel.setPhone_ID("");
                        userProfileModel.setAccountID(0);


                        EditProfileInsideRequestTask task = new EditProfileInsideRequestTask(
                                getActivity(), "");
                        task.execute(   firstNameEditText.getText().toString(),
                                        lastNameEditText.getText().toString(),
                                        phoneEditText.getText().toString(),
                                        emailEditText.getText().toString(),
                                        "",
                                        "",
                                        userNameEditText.getText().toString(),
                                        "",
                                        email2EditText.getText().toString(),
                                        email3EditText.getText().toString(),
                                        phone2EditText.getText().toString(),
                                        phone3EditText.getText().toString(),
                                        addressEditText.getText().toString(),
                                        cityEditText.getText().toString(),
                                        streetEditText.getText().toString(),
                                        zipEditText.getText().toString(),
                                        facebookEditText.getText().toString(),
                                        "",
                                        LivePhoneReadings.getPhoneId(getActivity().getApplicationContext()));
                    }
                } else {
                    // if Internet connection is not Available then alert will
                    // be open
                    Utility.showMessageDialog(getActivity(), "",
                            "Internet Connection is not Available");
                }
                break;

            case R.id.changePasswordButton:

                showChangePasswordDialog();


        }
	}

    private void showChangePasswordDialog() {
        changePasswordDialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(
                R.layout.dialog_change_password, null);
        final LinearLayout linearLayout = (LinearLayout) dialogView
                .findViewById(R.id.changePasswordProgressLayout);

        final EditText oldPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordOldPasswordEditText);
        final EditText newPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordNewPasswordEditText);
        final EditText confirmNewPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordConfirmNewPasswordEditText);

        changePasswordDialog.setView(dialogView);
        //changePasswordDialog.setCancelable(false);
        changePasswordDialog.setTitle("Change Password");
        final Button dialogOkButton = (Button) dialogView
                .findViewById(R.id.changePasswordDialogChangePasswordButton);

        dialogOkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (oldPasswordEditText.getText().toString().trim().length() > 0 && newPasswordEditText.getText().toString().trim().length() > 0 && confirmNewPasswordEditText.getText().toString().trim().length() > 0) {
                    dialogOkButton.setEnabled(false);
                    oldPasswordEditText.setEnabled(true);
                    newPasswordEditText.setEnabled(false);
                    confirmNewPasswordEditText.setEnabled(false);

                    linearLayout.setVisibility(View.VISIBLE);

                    new changePasswordTask().execute(LivePhoneReadings.getMobileUserProfileId(), oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());


                }
            }
        });
        changePasswordDialog.show();
    }

    class changePasswordTask extends AsyncTask<String, Void, String> {

        String changePasswordUrl = "http://jet-matics.com/JetComService/JetCom.svc/ChangePassword?";

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                ContentValues values = new ContentValues();
                values.put("mobileuserprofileid", params[0]);
                values.put("oldpassword", params[1]);
                values.put("newpassword", params[2]);

                //Log.dMainActivity.TAG, "calling url now");
                result = Utility.postRequest(changePasswordUrl, values);

                //Log.dMainActivity.TAG, "Result response= "
//                        + result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.contains("Successfully")) {
                    changePasswordDialog.dismiss();
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
                    confirmationDialog.setMessage("Password has been changed successfully.");
                    confirmationDialog.setPositiveButton("OK", null);
                    confirmationDialog.show();
                } else {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
                    confirmationDialog.setMessage("Old password is not correct.");
                    confirmationDialog.setPositiveButton("OK", null);
                    confirmationDialog.show();
                    changePasswordDialog.dismiss();

                }
            } else {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
                confirmationDialog.setMessage("Unable to connect. Please make sure that internet connection is active");
                confirmationDialog.setPositiveButton("OK", null);
                confirmationDialog.show();
                changePasswordDialog.dismiss();
            }
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

}
