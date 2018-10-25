package com.inspection

import android.app.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.*
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.ButtonBarLayout


import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.inspection.model.AccountDetailModel
import com.inspection.model.UserAccountModel
import com.inspection.model.UserProfileModel
import com.inspection.model.VehicleProfileModel
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Utility
import com.inspection.Utils.toast
import com.inspection.serverTasks.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import kotlinx.android.synthetic.main.dialog_user_register.*


//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class LoginActivity : Activity(){

    internal var registerDialog: AlertDialog? = null
    private var mCirclesList: ArrayList<String>? = null




    private var mSignInProgress: Int = 0

    private var mSignInIntent: PendingIntent? = null

    private var mSignInError: Int = 0


    private val mRequestServerAuthCode = false


    private val mServerHasToken = true

    internal var isFacebookLogin = false
    internal var isGooglePlusLogin = false

    internal var progressDialog: ProgressDialog? = null
    internal var forgotPasswordDialog: AlertDialog? = null

    internal var activity: Activity? = null

    internal var currentUser: GoogleSignInAccount? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawableResource(R.drawable.login_background_image_dark)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        activity = this

        signUpButton!!.setOnClickListener { showRegisterDialog() }

        forgotPasswordButton!!.setOnClickListener { showForgotPasswordDialog() }

        loginEmailEditText.setText("Johnson.Fredrick@aaa-texas.Com")

        loginButton!!.setOnClickListener {
            if (loginEmailEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty() && loginPasswordEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                // new GetAccountDetailByEmailAndPhoneID(context,
                //                            true, dialogEditText.getText().toString().trim(), "")
                //                            .execute("");
//                GetUserLoginCredentialsResultTask().execute(loginEmailEditText!!.text.toString(), loginPasswordEditText!!.text.toString())
                //TODO: easy login just for demo, should be updated later
//                GetUserLoginCredentialsResultTask().execute("FL111111", "4CE678CE")
                ApplicationPrefs.getInstance(activity).loggedInUserEmail = loginEmailEditText!!.text.toString()
                userIsLoggedInGotoMainActivity()
            } else {
                val alertDialog = AlertDialog.Builder(activity)
                alertDialog.setMessage("Please enter your email and password")
                alertDialog.setPositiveButton("OK", null)
                alertDialog.show()
            }
        }

    }


    private fun userIsLoggedInGotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }



    internal inner class GetUserLoginCredentialsResultTask : AsyncTask<String, Void, String>() {

        var emailString: String = ""

        override fun onPreExecute() {
            super.onPreExecute()
            //            progressDialog = ProgressDialog.show(activity, "", "Logging in...", true, false);
            progressDialog = ProgressDialog(activity)
            progressDialog!!.isIndeterminate = true
            progressDialog!!.setMessage("Logging in....")
            progressDialog!!.show()
        }

        override fun doInBackground(vararg params: String): String {
            //Log.dMainActivity.TAG, "SAEED-onbackground*******")
            emailString = params[0]
            val result = getUserLoginCredentialsResult(params[0], params[1])
            //Log.dMainActivity.TAG, "SAEED- "+result);
            return result
        }

        override fun onPostExecute(result: String?) {
            // TODO Auto-generated method stub
            super.onPostExecute(result)
            if (result == null || result.contains("Connection not available")) {
                val failedLoginDialog = AlertDialog.Builder(activity)
                failedLoginDialog.setMessage("Please check your internet connection")
                failedLoginDialog.setPositiveButton("OK", null)
                failedLoginDialog.show()
                progressDialog!!.dismiss()
            } else {
                if (result.contains("System did not find the User") || result.contains("IsSuccess\":false")) {
                    val failedLoginDialog = AlertDialog.Builder(activity)
                    failedLoginDialog.setMessage("Invalid username or password")
                    failedLoginDialog.setPositiveButton("OK", null)
                    failedLoginDialog.show()
                    progressDialog!!.dismiss()
                } else {

                    object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, emailString, "") {
                        override fun onTaskCompleted(result: String) {
                            checkAccountDetailsRetrievedFromCloudHere(result)
                        }
                    }.execute()
                }
            }
        }

        private fun getUserLoginCredentialsResult(userName: String, password: String): String {
            //Log.dMainActivity.TAG, "SAEED-getUserLoginCredentialsResult*******")

            val urlPath = "http://www.jet-matics.com/JetComService/JetCom.svc/UserLogin?"
            var result = ""
            try {
                val values = ContentValues()
                values.put("UserName", userName)
                values.put("Pwd", password)
                values.put("Device", "Android")

                //Log.dMainActivity.TAG, "calling url now")
                result = Utility.postRequest(urlPath, values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

    }

    private fun showForgotPasswordDialog() {
        forgotPasswordDialog = AlertDialog.Builder(activity).create()
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(
                R.layout.dialog_forgot_password, null)


        forgotPasswordDialog!!.setView(dialogView)
        forgotPasswordDialog!!.setCancelable(true)
        forgotPasswordDialog!!.setTitle("Forgot Password?")

        forgotPasswordResetPasswordButton.setOnClickListener {
            if (emailEditText!!.text.toString().trim { it <= ' ' }.length > 0) {
                forgotPasswordResetPasswordButton.isEnabled = false
                emailEditText!!.isEnabled = false
                forgotPasswordProgressLayout.visibility = View.VISIBLE
                object : ResetPasswordTask(activity!!, emailEditText!!.text.toString(), "Password has been reset successfully. Please check your email.") {
                    override fun onTaskCompleted(result: String) {

                    }
                }.execute()
            }
        }
        forgotPasswordDialog!!.show()
    }


    fun showRegisterDialog() {
        registerDialog = AlertDialog.Builder(activity).create()
        registerDialog!!.setCancelable(false)
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(
                R.layout.dialog_email_register, null)
        registerDialog!!.setView(dialogView)
        registerDialog!!.setTitle("Register New Account")
        registerButton!!.setOnClickListener {
            if (Utility.checkInternetConnection(activity)) {
                if (emailEditText!!.text.toString().trim { it <= ' ' }.length > 0 || phoneEditText!!.text.toString().trim { it <= ' ' }.length > 0) {
                    emailEditText!!.isEnabled = false
                    phoneEditText!!.isEnabled = false
                    registerDialog!!.dismiss()
                    val progress = ProgressDialog(activity)
                    progress.isIndeterminate = true
                    progress.setMessage("Loading...")
                    progress.show()
                    object : GenericServerTask(activity!!, getString(R.string.preparePhoneLoginUrl), arrayOf("email", "phone"), arrayOf(emailEditText!!.text.toString(), phoneEditText!!.text.toString())) {
                        override fun onTaskCompleted(result: String) {
                            progress.dismiss()
                            if (result!!.contains("PreparePhoneLoginWithEmailAndPhoneResult")) {
                                val customerId = result!!.replace("{\"PreparePhoneLoginWithEmailAndPhoneResult\":", "").replace("}", "").trim().toInt()
                                if (customerId == -1) {
                                    val intent = Intent(activity, SignupActivity::class.java)
                                    if (emailEditText!!.text.toString().trim().length > 0 || phoneEditText!!.text.toString().trim().length > 0) {
                                        val bundle = Bundle()
                                        if (emailEditText!!.text.toString().trim().length > 0) {
                                            bundle.putString("email", emailEditText!!.text.toString())
                                        }

                                        if (phoneEditText!!.text.toString().length > 0) {
                                            bundle.putString("phone", phoneEditText!!.text.toString())
                                        }
                                        intent.putExtras(bundle)
                                    }

                                    startActivity(intent)
                                } else if (customerId > -1) {
                                    object : ResetPasswordTask(activity!!, emailEditText!!.getText().toString(), "Your new password has been sent to your inbox. Please use it to login") {
                                        override fun onTaskCompleted(result: String) {

                                        }
                                    }.execute()
                                } else {
                                    activity!!.toast("Error while Registering. Please try again later.")
                                }
                            }
                        }
                    }.execute()

                    //                        new GetAccountDetailByEmailAndPhoneIDTask(activity, false, emailEditText.getText().toString(), "") {
                    //                            @Override
                    //                            public void onTaskCompleted(String result) {
                    //                                UserProfileModel userProfileModel = this.getUserProfileModel();
                    //                                if (userProfileModel != null) {
                    //                                    registerDialog.dismiss();
                    //                                    new ResetPasswordTask(activity, emailEditText.getText().toString(), "Your new password has been sent to your inbox. Please use it to login") {
                    //                                        @Override
                    //                                        public void onTaskCompleted(String result) {
                    //
                    //                                        }
                    //                                    }.execute();
                    //                                } else {
                    //                                    registerDialog.dismiss();
                    //                                    Intent intent = new Intent(activity, SignupActivity.class);
                    //                                    startActivity(intent);
                    //                                }
                    //                            }
                    //                        }.execute();

                } else {
                    activity!!.toast("Please enter valid email or username")
                }
            } else {
                activity!!.toast("No internet Connection.")
            }
        }

        backButton!!.setOnClickListener { registerDialog!!.dismiss() }

        registerDialog!!.show()
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
            if (registerDialog != null) {
                registerDialog!!.dismiss()
            }


            if (result.contains("IsSuccess\":true")) {
                object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, userProfileModel.email, "") {
                    override fun onTaskCompleted(result: String) {
                        if (!isFacebookLogin && !isGooglePlusLogin) {
                            checkAccountDetailsRetrievedFromCloudHere(result)
                        }
                    }
                }.execute()
                activity!!.toast("Profile created successfully")
                userIsLoggedInGotoMainActivity()
            } else {
                activity!!.toast("Error while creating profile, please try again later.")
            }
        }

    }

    internal inner class FacebookUser {
        var firstName: String? = null
        var lastName: String? = null
        var displayName: String? = null
        var facebookId: String? = null
        var email: String? = null
    }


    fun checkAccountDetailsRetrievedFromCloudHere(result: String?) {
        var accountDetailModel: AccountDetailModel? = AccountDetailModel()
        //Log.dMainActivity.TAG, "result : " + result!!)
        if (result == null) {
            applicationContext.toast("Connection error. Please try again later")
            val failedLoginDialog = AlertDialog.Builder(activity)
            failedLoginDialog.setMessage("Couldn't login, Please try again")
            failedLoginDialog.setPositiveButton("OK", null)
            failedLoginDialog.show()
            progressDialog!!.dismiss()
            return
        }
        if (result.contains("Timeout") || result.contains("timeout") || result.contains("<HTML></HTML>")) {
            applicationContext.toast("Connection error. Please try again later")
            val failedLoginDialog = AlertDialog.Builder(activity)
            failedLoginDialog.setMessage("Couldn't login, Please try again")
            failedLoginDialog.setPositiveButton("OK", null)
            failedLoginDialog.show()
            progressDialog!!.dismiss()
            return
        } else if (result.contains("IsSuccess\":true")) {
            //Log.dMainActivity.TAG, "is success, lets set the user profile data")
            try {
                val jObject = JSONObject(result.toString())
                val profileResult = jObject
                        .getJSONObject("GetAccountDetailByEmailAndPhoneIDResult")
                val userAccountObject = profileResult
                        .getJSONObject("AccountUser")
                if (profileResult.getJSONObject("UserProfile") != null) {
                    //Log.dMainActivity.TAG, "let's save user profile")
                    val userProfileObject = profileResult
                            .getJSONObject("UserProfile")
                    val userProfileModel = Gson().fromJson(
                            userProfileObject.toString(),
                            UserProfileModel::class.java)


                    val userAccountModel = Gson().fromJson(
                            userAccountObject.toString(),
                            UserAccountModel::class.java)


                    if (!userProfileModel.isShop && userProfileModel.accountID > 0) {
                        object : GetShopUserProfileDetails(activity!!, false, userAccountModel.email, "") {
                            override fun onTaskCompleted(result: String) {
                                if (progressDialog != null) {
                                    progressDialog!!.dismiss()
                                }
                            }
                        }.execute()
                    }


                    accountDetailModel = AccountDetailModel(
                            userAccountModel, userProfileModel, null)

                    val vehicleProfileListJsonArray = profileResult.getJSONArray("VehicleProfileList")
                    //Log.dMainActivity.TAG, "Its count = " + vehicleProfileListJsonArray.length())
                    val vehicleProfileModelArrayList = ArrayList<VehicleProfileModel>()
                    vehicleProfileModelArrayList.clear()
                    if (vehicleProfileListJsonArray.length() > 0) {
                        var vehicleProfileModel: VehicleProfileModel?
                        for (x in 0..vehicleProfileListJsonArray.length() - 1) {
                            vehicleProfileModel = Gson().fromJson(vehicleProfileListJsonArray.get(x).toString(), VehicleProfileModel::class.java)
                            if (vehicleProfileModel!!.vin == "" && vehicleProfileModel.model == "" && vehicleProfileModel.make == "" && vehicleProfileModel.year == "0") {
                                vehicleProfileModel = null
                                continue
                            }
                            vehicleProfileModelArrayList.add(vehicleProfileModel)
                        }

                    }


                } else {
                    progressDialog!!.dismiss()


                }

                if (accountDetailModel != null) {
                    if (accountDetailModel.userProfileModel != null) {

                        ApplicationPrefs.getInstance(activity).userProfilePref = accountDetailModel.userProfileModel
                        if (accountDetailModel.userAccountModel != null) {
                            //ApplicationPrefs.getInstance(activity).setUserAccountPrefs(accountDetailModel.userAccountModel)
                        }
                        if (progressDialog != null) {
                            progressDialog!!.dismiss()
                        }
                    }

                }


                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_email), true)

                userIsLoggedInGotoMainActivity()

            } catch (jsonExp: JSONException) {

                    jsonExp.printStackTrace()
                    activity!!.toast("Invalid username or password")
                    val failedLoginDialog = AlertDialog.Builder(activity)
                    failedLoginDialog.setMessage("Invalid username or password")
                    failedLoginDialog.setPositiveButton("OK", null)
                    failedLoginDialog.show()
                    //progressDialog.dismiss();
                return
            }

        } else {


                //Log.dMainActivity.TAG, "I am here !!!!  ")
                val failedLoginDialog = AlertDialog.Builder(activity)
                failedLoginDialog.setMessage("Invalid username or password")
                failedLoginDialog.setPositiveButton("OK", null)
                failedLoginDialog.show()
           }
    }

    companion object {

        private val TAG = "LoginActivity"


        private val STATE_DEFAULT = 0
        private val STATE_SIGN_IN = 1
        private val STATE_IN_PROGRESS = 2

        private val RC_SIGN_IN = 555999
        private val FACEBOOK_LOGIN_IN = 5

        private val SAVED_PROGRESS = "sign_in_progress"

        private val WEB_CLIENT_ID = "WEB_CLIENT_ID"

        // Base URL for your token exchange server, no trailing slash.
        private val SERVER_BASE_URL = "SERVER_BASE_URL"

        private val EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken"

        private val SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes"


        var mGoogleApiClient: GoogleApiClient? = null
    }
}