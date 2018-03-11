package com.inspection

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast

import com.facebook.FacebookSdk
import com.inspection.Utils.Utility
import com.inspection.R
import com.inspection.Utils.toast
import com.inspection.model.UserProfileModel
import kotlinx.android.synthetic.main.dialog_user_register.*

import java.util.regex.Pattern

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

class SignupActivity : Activity() {

    internal var activity: Activity? = null
    private var registerProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.dialog_user_register)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        activity = this
        //        new DataBaseHelper(activity);

        registerProgressDialog = ProgressDialog(this)
        registerProgressDialog!!.isIndeterminate = true



        registerButton!!.setOnClickListener {
            if (Utility.checkInternetConnection(activity)) {
                if (firstNameEditText!!.text.toString().trim { it <= ' ' }.length > 0 && lastNameEditText!!.text.toString().trim { it <= ' ' }.length > 0 &&
                        userNameEditText!!.text.toString().trim { it <= ' ' }.length > 0 && passwordEditText!!.text.toString().trim { it <= ' ' }.length > 0 && emailEditText!!.text.toString().trim { it <= ' ' }.length > 0 &&
                        phoneEditText!!.text.toString().trim { it <= ' ' }.length > 0) {

                    if (isEmailValid(emailEditText!!.text.toString())) {
                        firstNameEditText!!.isEnabled = false
                        lastNameEditText!!.isEnabled = false
                        userNameEditText!!.isEnabled = false
                        passwordEditText!!.isEnabled = false
                        emailEditText!!.isEnabled = false
                        email2EditText!!.isEnabled = false
                        email3EditText!!.isEnabled = false
                        phoneEditText!!.isEnabled = false
                        phone2EditText!!.isEnabled = false
                        phone3EditText!!.isEnabled = false
                        facebookEditText!!.isEnabled = false
                        addressEditText!!.isEnabled = false
                        cityEditText!!.isEnabled = false
                        streetEditText!!.isEnabled = false
                        zipEditText!!.isEnabled = false
                        registerProgressDialog!!.setMessage("Signing Up...")
                        registerProgressDialog!!.show()

                        CreateProfile().execute(firstNameEditText!!.text.toString(), lastNameEditText!!.text.toString(), phoneEditText!!.text.toString(), emailEditText!!.text.toString(), "", "", userNameEditText!!.text.toString(), passwordEditText!!.text.toString(), email2EditText!!.text.toString(), email3EditText!!.text.toString(), phone2EditText!!.text.toString(), phone3EditText!!.text.toString(), addressEditText!!.text.toString(), cityEditText!!.text.toString(), streetEditText!!.text.toString(),
                                zipEditText!!.text.toString(), facebookEditText!!.text.toString(), "", "", "", "")
                    } else {
                        activity!!.toast("Invalid Email format. Please check your input in Email fields")
                    }

                } else {
                    activity!!.toast("FirstName, LastName, UserName, Password, Email, and Phone are mandatory fields")
                }
            } else {
                Toast.makeText(activity,
                        "No internet Connection.",
                        Toast.LENGTH_SHORT).show()
            }
        }

        backButton!!.setOnClickListener { finish() }

        if (intent.extras.getString("email") != null){
            emailEditText!!.setText(intent.extras.getString("email"))
            emailEditText!!.isEnabled = false
        }

        if (intent.extras.getString("phone") != null){
            phoneEditText!!.setText(intent.extras.getString("phone"))
            phoneEditText!!.isEnabled = false
        }

    }


    internal inner class CreateProfile : AsyncTask<String, Void, String>() {
        var userProfileModel = UserProfileModel()

        override fun doInBackground(vararg params: String): String {
            val result = createProfileRequest(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11],
                    params[12], params[13], params[14], params[15], params[16], params[17], params[18], params[19], params[20])
            return result
        }


        private fun createProfileRequest(firstName: String, lastName: String, phoneNumber: String, email: String, deviceId: String, accountId: String, userName: String, password: String, email2: String, email3: String, phoneNumber2: String, phoneNumber3: String, address: String, city: String, street: String, zip: String, facebook: String, customerId: String, phoneId: String, socialMedia: String, socialMediaId: String): String {

            val urlPath = "http://www.jet-matics.com/JetComService/JetCom.svc/CreateProfile?"
            var result = ""
            try {
                val values = ContentValues()
                values.put("FirstName", firstName)
                values.put("LastName", lastName)
                values.put("PhoneNumber", phoneNumber)
                values.put("Email", email)
                values.put("DeviceID", deviceId)
                if (accountId.length > 0) {
                    values.put("AccountId", Integer.parseInt(accountId))
                } else {
                    values.put("AccountId", "" + accountId)
                }

                values.put("UserName", userName)
                values.put("Password", password)
                values.put("Email2", email2)
                values.put("Email3", email3)
                values.put("Phone2", phoneNumber2)
                values.put("Phone3", phoneNumber3)
                values.put("Address", address)
                values.put("City", city)
                values.put("ST", street)
                values.put("Zip", zip)
                values.put("Facebook", facebook)
                values.put("CustomerID", customerId)
                values.put("Phone_ID", phoneId)
                values.put("SocialMedia", socialMedia)
                values.put("SocialMediaID", socialMediaId)

                userProfileModel.firstName = firstName
                userProfileModel.lastName = lastName
                userProfileModel.userName = userName
                userProfileModel.phoneNumber = phoneNumber
                userProfileModel.phoneNumber2 = phoneNumber2
                userProfileModel.phoneNumber3 = phoneNumber3
                userProfileModel.email = email
                userProfileModel.email2 = email2
                userProfileModel.email3 = email3
                userProfileModel.address = address
                userProfileModel.city = city
                userProfileModel.state = street
                userProfileModel.zip = zip
                userProfileModel.facebook = facebook
                userProfileModel.deviceID = deviceId
                userProfileModel.phone_ID = phoneId

                if (accountId.length > 0) {
                    values.put("AccountId", Integer.parseInt(accountId))
                    userProfileModel.accountID = Integer.parseInt(accountId)
                } else {
                    userProfileModel.accountID = 0
                }
                userProfileModel.socialMedia = socialMedia


                result = Utility.postRequest(urlPath, values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result: String) {
            // TODO Auto-generated method stub
            super.onPostExecute(result)



            if (result.contains("IsSuccess\":true")) {
                //                    new GetAccountDetailByEmailAndPhoneID(activity, false, userProfileModel.getEmail(), "").execute();
                //                    activity!!.toast("Profile created successfully");
            } else {
                activity!!.toast("Error while creating profile, please try again later.")
                registerProgressDialog!!.dismiss()
                enableFields()
            }
        }

    }



    private fun enableFields() {
        firstNameEditText!!.isEnabled = true
        lastNameEditText!!.isEnabled = true
        userNameEditText!!.isEnabled = true
        passwordEditText!!.isEnabled = true
        emailEditText!!.isEnabled = true
        email2EditText!!.isEnabled = true
        email3EditText!!.isEnabled = true
        phoneEditText!!.isEnabled = true
        phone2EditText!!.isEnabled = true
        phone3EditText!!.isEnabled = true
        facebookEditText!!.isEnabled = true
        addressEditText!!.isEnabled = true
        cityEditText!!.isEnabled = true
        streetEditText!!.isEnabled = true
        zipEditText!!.isEnabled = true
    }

    companion object {

        fun isEmailValid(email: String): Boolean {
            var isValid = false

            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val inputStr = email

            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(inputStr)
            if (matcher.matches()) {
                isValid = true
            }
            return isValid
        }
    }


}

