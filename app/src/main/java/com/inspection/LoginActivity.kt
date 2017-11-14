package com.inspection

import android.app.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.*
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast


import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.plus.People
import com.google.android.gms.plus.Plus
import com.google.gson.Gson
import com.inspection.imageloader.Utils
import com.inspection.model.AccountDetailModel
import com.inspection.model.UserAccountModel
import com.inspection.model.UserProfileModel
import com.inspection.model.VehicleProfileModel
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Utility
import com.inspection.R
import com.inspection.serverTasks.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import kotlinx.android.synthetic.main.dialog_user_register.*


//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class LoginActivity : Activity(), View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    internal var registerDialog: AlertDialog? = null
    private var mCirclesList: ArrayList<String>? = null


    internal var accessTokenTracker: AccessTokenTracker? = null

    private var mSignInProgress: Int = 0

    private var mSignInIntent: PendingIntent? = null

    private var mSignInError: Int = 0


    private val mRequestServerAuthCode = false


    private val mServerHasToken = true

    internal var callbackManager: CallbackManager? = null

    internal var isFacebookLogin = false
    internal var isGooglePlusLogin = false

    internal var progressDialog: ProgressDialog? = null
    internal var forgotPasswordDialog: AlertDialog? = null

    internal var activity: Activity? = null

    internal var currentUser: GoogleSignInAccount? = null

    internal var accessToken: AccessToken? = null

    internal var facebookUser: FacebookUser? = null

    internal var googlePlusEmail: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawableResource(R.drawable.login_background_image_dark)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        activity = this

        loginRegisterButton!!.setOnClickListener { showRegisterDialog() }

        loginForgetPasswordButton!!.setOnClickListener { showForgotPasswordDialog() }


        loginSignInButton!!.setOnClickListener {
            if (loginEmailEditText!!.text.toString().trim { it <= ' ' }.length > 0 && loginPasswordEditText!!.text.toString().trim { it <= ' ' }.length > 0) {
                // new GetAccountDetailByEmailAndPhoneID(context,
                //                            true, dialogEditText.getText().toString().trim(), "")
                //                            .execute("");
//                GetUserLoginCredentialsResultTask().execute(loginEmailEditText!!.text.toString(), loginPasswordEditText!!.text.toString())
                //TODO: easy login just for demo, should be updated later
                GetUserLoginCredentialsResultTask().execute("FL111111", "4CE678CE")
            } else {
                val alertDialog = AlertDialog.Builder(activity)
                alertDialog.setMessage("Please enter your email and password")
                alertDialog.setPositiveButton("OK", null)
                alertDialog.show()
            }
        }


//        callbackManager = CallbackManager.Factory.create()
//
//        facebookLogInButton!!.setReadPermissions("user_friends, email")
//        facebookLogInButton!!.setOnClickListener {
//            //                progressDialog = new ProgressDialog(activity);
//            //                progressDialog.setIndeterminate(true);
//            //                progressDialog.setCancelable(true);
//            //                progressDialog.setMessage("Logging in ...");
//            //                progressDialog.show();
//            isFacebookLogin = true
//        }
//
//        // If using in a fragment
//
//        // Other app specific specialization
//
//        // Callback registration
//        facebookLogInButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                // App code
//                //Log.dTAG, "login result is: " + loginResult.recentlyGrantedPermissions)
//                progressDialog = ProgressDialog(activity)
//                progressDialog!!.setMessage("Loading...")
//                ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), true)
//                accessToken = AccessToken.getCurrentAccessToken()
//                val request = GraphRequest.newMeRequest(
//                        accessToken
//                ) { `object`, response ->
//                    try {
//                        facebookUser = FacebookUser()
//                        facebookUser!!.firstName = `object`.getString("first_name")
//                        facebookUser!!.lastName = `object`.getString("last_name")
//                        facebookUser!!.displayName = `object`.getString("name")
//                        facebookUser!!.facebookId = `object`.getString("id")
//                        facebookUser!!.email = `object`.getString("email")
//                    } catch (jsonException: JSONException) {
//                        //Log.dMainActivity.TAG, jsonException.message)
//                    }
//
//                    isFacebookLogin = true
//                    isGooglePlusLogin = false
//
//                    //Log.dMainActivity.TAG, "Facebook Email is: " + facebookUser!!.email!!)
//                    progressDialog!!.show()
//                    object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, facebookUser!!.email!!, "") {
//                        override fun onTaskCompleted(result: String) {
//                            progressDialog!!.dismiss()
//                            checkAccountDetailsRetrievedFromCloudHere(result)
//                        }
//                    }.execute()
//                }
//                val parameters = Bundle()
//                parameters.putString("fields", "id,name,link, birthday, first_name, last_name, gender, email")
//                request.parameters = parameters
//                request.executeAsync()
//
//                // If the access token is available already assign it.
//                //                progressDialog.dismiss();
//
//                //userIsLoggedInGotoMainActivity();
//            }
//
//            override fun onCancel() {
//                // App code
//                //Log.dTAG, "login cancelled")
//                //progressDialog.dismiss();
//                ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
//            }
//
//            override fun onError(exception: FacebookException) {
//                // App code
//                //Log.dTAG, "login error is: " + exception.message)
//                //progressDialog.dismiss();
//                ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
//            }
//        })
//
//
//        googlePlusSignInButton!!.setOnClickListener(this)
//
//
//        mCirclesList = ArrayList<String>()
//        //        mCirclesAdapter = new ArrayAdapter<String>(
//        //                this, R.layout.circle_member, mCirclesList);
//        //        mCirclesListView.setAdapter(mCirclesAdapter);
//
//        //Log.d"G+ Connection ----- : ", ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus)).toString())
//
//        if (ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus))) {
////            mGoogleApiClient = buildGoogleApiClient()
//            //Log.dMainActivity.TAG, "user is logged in with google plus")
//            userIsLoggedInGotoMainActivity()
//            //   mGoogleApiClient.connect();
//        }
//
//        if (ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_facebook))) {
//            //Log.dMainActivity.TAG, "user is logged in with FACEBOOK")
//            if (facebookLogInButton!!.text.toString().startsWith("Log out")) {
//                ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), true)
//                userIsLoggedInGotoMainActivity()
//            } else {
//                ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
//            }
//
//        }
        if (ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_email))) {
            //Log.dMainActivity.TAG, "user is logged in with EMAIL")
            userIsLoggedInGotoMainActivity()
        }

    }


    private fun buildGoogleApiClient(): GoogleApiClient {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        val builder = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
//                .addApi<Plus.PlusOptions>(Plus.API, Plus.PlusOptions.builder().build())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)

        //        if (mRequestServerAuthCode) {
        //            checkServerAuthConfiguration();
        //            builder = builder.requestServerAuthCode(WEB_CLIENT_ID, this);
        //        }

        return builder.build()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

        //        if (mGoogleApiClient.isConnected()) {
        //            mGoogleApiClient.disconnect();
        //        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN

                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT
                }

                if (!mGoogleApiClient!!.isConnecting) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
//                    mGoogleApiClient!!.connect()
                    handleGPlusLogin(data)
                }
            }
        }

        if (isFacebookLogin) {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun handleGPlusLogin(data: Intent?){
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            currentUser = result.getSignInAccount();
            // Get account information

//           currentUser =
            googlePlusEmail = currentUser!!.email
        }
//        currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)
//        googlePlusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient)

        if (!ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus))) {
            //Log.dTAG, "current user is: " + currentUser!!)
            isGooglePlusLogin = true
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.show()
            object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, googlePlusEmail!!, "") {
                override fun onTaskCompleted(result: String) {
                    checkAccountDetailsRetrievedFromCloudHere(result)

                }
            }.execute()

        } else {
            mSignInProgress = STATE_DEFAULT

            userIsLoggedInGotoMainActivity()
        }
    }
    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
    }

    override fun onClick(v: View) {

        // We only process button clicks when GoogleApiClient is not transitioning
        // between connected and not connected.
        when (v.id) {
            R.id.googlePlusSignInButton -> {
                //mStatus.setText(R.string.status_signing_in);
                // Saeed Mostafa - 05092017 - Remove deprecated G PLUS API [Start]
//                mGoogleApiClient = buildGoogleApiClient()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                mGoogleApiClient = GoogleApiClient.Builder(this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build()

                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, RC_SIGN_IN)

//                if (!mGoogleApiClient!!.isConnecting) {
//                    mSignInProgress = STATE_SIGN_IN
//                    //Log.dTAG, "Connecting to google+")
//                    mGoogleApiClient!!.connect()
//                }
            }
        }
    }


    override fun onConnected(bundle: Bundle?) {
        // Reaching onConnected means we consider the user signed in.
        //Log.dTAG, "onConnected")

//        currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).toString()
        googlePlusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient)

        if (!ApplicationPrefs.getInstance(this).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus))) {
            //Log.dTAG, "current user is: " + currentUser!!)
            isGooglePlusLogin = true
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.show()
            object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, googlePlusEmail!!, "") {
                override fun onTaskCompleted(result: String) {
                    checkAccountDetailsRetrievedFromCloudHere(result)

                }
            }.execute()

            object : GetAccountDetailByEmailAndPhoneIDTask(activity!!, false, googlePlusEmail!!, "") {
                override fun onTaskCompleted(result: String) {
                    checkAccountDetailsRetrievedFromCloudHere(result)

                }
            }.execute()

        } else {
            mSignInProgress = STATE_DEFAULT

            userIsLoggedInGotoMainActivity()
        }

    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        //Log.dTAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + result.errorCode)

        if (result.errorCode == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Log.w(TAG, "API Unavailable.")
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.resolution
            mSignInError = result.errorCode

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError()
            }
        }

        // In this sample we consider the user signed out whenever they do not have
        // a connection to Google Play services.
        onSignedOut()
    }

    private fun onSignedOut() {
        // Update the UI to reflect that the user is signed out.
        //        mSignInButton.setEnabled(true);
        //        mSignOutButton.setEnabled(false);
        //        mRevokeButton.setEnabled(false);

        // Show the sign-in options
        //        findViewById(R.id.layout_server_auth).setVisibility(View.VISIBLE);

        //        mStatus.setText(R.string.status_signed_out);

        //        mCirclesList.clear();
        //        mCirclesAdapter.notifyDataSetChanged();
    }

    private fun resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS
                startIntentSenderForResult(mSignInIntent!!.intentSender,
                        RC_SIGN_IN, null, 0, 0, 0)
            } catch (e: IntentSender.SendIntentException) {
                //Log.dTAG, "Sign in intent could not be sent: " + e.localizedMessage)
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN
                mGoogleApiClient!!.connect()
            }

        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            createErrorDialog().show()
        }
    }

    private fun createErrorDialog(): Dialog {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN
            ) {
                //Log.e(TAG, "Google Play services resolution cancelled")
                mSignInProgress = STATE_DEFAULT
                //mStatus.setText(R.string.status_signed_out);
            }
        } else {
            return AlertDialog.Builder(this)
                    .setMessage(R.string.play_services_error)
                    .setPositiveButton(R.string.close
                    ) { dialog, which ->
                        //Log.e(TAG, "Google Play services error could not be "
//                                + "resolved: " + mSignInError)
                        mSignInProgress = STATE_DEFAULT
                        //                                    mStatus.setText(R.string.status_signed_out);
                    }.create()
        }
    }

    override fun onResult(peopleData: People.LoadPeopleResult) {
        if (peopleData.status.statusCode == CommonStatusCodes.SUCCESS) {
            mCirclesList!!.clear()
            val personBuffer = peopleData.personBuffer
            try {
                val count = personBuffer.count
                for (i in 0..count - 1) {
                    mCirclesList!!.add(personBuffer.get(i).displayName)
                    //Log.dTAG, "" + personBuffer.get(i).displayName)
                }
            } finally {
                personBuffer.close()
            }

            for (circ in mCirclesList!!) {
                //Log.dTAG, "Circle list is: " + circ)
            }

            //            mCirclesAdapter.notifyDataSetChanged();
        } else {
            //Log.e(TAG, "Error requesting visible circles: " + peopleData.status)
        }
    }


    private fun userIsLoggedInGotoMainActivity() {
        //new BackgroundDailyServerCallsThread(activity).start();
        ApplicationPrefs.getInstance(activity).setLoggedInPrefs(true)
        object : GetCompetitorsListTask(this) {
            override fun onTaskCompleted(result: String) {

            }
        }.execute()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


    private fun saveGooglePlusCurrentUser() {

        if (currentUser != null) {
            val placeLived: String
//            if (currentUser!!.placesLived != null) {
//                placeLived = currentUser!!.placesLived[0].value
//            } else {
                placeLived = ""
//            }

            CreateProfile().execute(Utils.getStringOrEmptyForNull(currentUser!!.givenName),
                    Utils.getStringOrEmptyForNull(currentUser!!.familyName),
                    "Not set2",
                    Utils.getStringOrEmptyForNull(googlePlusEmail),
                    "",
                    "",
                    Utils.getStringOrEmptyForNull(currentUser!!.displayName),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Utils.getStringOrEmptyForNull(placeLived),
                    "",
                    "",
                    "",
                    "",
                    Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID),
                    "googleplus",
                    currentUser!!.id)
//            userIsLoggedInGotoMainActivity()

        } else {
            isGooglePlusLogin = false
            val facebookLoginErrorDialog = AlertDialog.Builder(activity)
            facebookLoginErrorDialog.setMessage("User doesn't have active Google+ user")
            facebookLoginErrorDialog.setPositiveButton("OK", null)
            facebookLoginErrorDialog.show()
        }
    }


    private fun saveFacebookUser() {
        CreateProfile().execute(facebookUser!!.firstName,
                facebookUser!!.lastName,
                "Not set",
                facebookUser!!.email,
                "",
                "",
                facebookUser!!.displayName,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID),
                "facebook",
                facebookUser!!.facebookId)

        //        UserProfileModel userProfileModel = new UserProfileModel();
        //        userProfileModel.setFirstName(facebookUser.firstName);
        //        userProfileModel.setLastName(facebookUser.lastName);
        //        userProfileModel.setUserName(facebookUser.displayName);
        //        userProfileModel.setPhoneNumber("Not Set");
        //        userProfileModel.setPhoneNumber2("");
        //        userProfileModel.setPhoneNumber3("");
        //        userProfileModel.setEmail("Not Set");
        //        userProfileModel.setEmail2("");
        //        userProfileModel.setEmail3("");
        //        userProfileModel.setAddress("");
        //        userProfileModel.setCity("Not Set");
        //        userProfileModel.setState("");
        //        userProfileModel.setZip("");
        //        userProfileModel.setFacebook("");
        //        userProfileModel.setDeviceID("");
        //        userProfileModel.setPhone_ID(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        //        userProfileModel.setAccountID("");
        //        userProfileModel.setSocialMedia("facebook");
        //        userProfileModel.setSocialMediaID(facebookUser.facebookId);

        //        ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), true);
        //        ApplicationPrefs.getInstance(activity).setUserProfilePref(userProfileModel);

        //        userIsLoggedInGotoMainActivity();
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
                                    Toast.makeText(activity, "Error while Registering. Please try again later.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "Please enter valid email or username", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity,
                        "No internet Connection.",
                        Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, "Profile created successfully", Toast.LENGTH_LONG).show()
                userIsLoggedInGotoMainActivity()
            } else {
                Toast.makeText(activity, "Error while creating profile, please try again later.", Toast.LENGTH_LONG).show()
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
            Toast.makeText(applicationContext, "Connection error. Please try again later", Toast.LENGTH_LONG).show()
            val failedLoginDialog = AlertDialog.Builder(activity)
            failedLoginDialog.setMessage("Couldn't login, Please try again")
            failedLoginDialog.setPositiveButton("OK", null)
            failedLoginDialog.show()
            progressDialog!!.dismiss()
            return
        }
        if (result.contains("Timeout") || result.contains("timeout") || result.contains("<HTML></HTML>")) {
            Toast.makeText(applicationContext, "Connection error. Please try again later", Toast.LENGTH_LONG).show()
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

                    if (isGooglePlusLogin && (userProfileModel.socialMedia == null || userProfileModel.socialMedia != "googleplus")) {
                        //Log.dMainActivity.TAG, "User needs to be udpated to be a Google Plus user")
                        saveGooglePlusCurrentUser()

                    }

                    if (isFacebookLogin && (userProfileModel.socialMedia == null || userProfileModel.socialMedia != "facebook")) {
                        //Log.dMainActivity.TAG, "User needs to be udpated to be a Facebook user")
                        saveFacebookUser()

                    }
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

                if (isGooglePlusLogin) {
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), true)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_email), false)
                } else if (isFacebookLogin) {
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), true)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_email), false)
                } else {
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
                    ApplicationPrefs.getInstance(activity).setBooleanPref(getString(R.string.is_user_logged_in_with_email), true)
                }
                userIsLoggedInGotoMainActivity()

            } catch (jsonExp: JSONException) {
                //Log.dMainActivity.TAG, jsonExp.message)
                if (isGooglePlusLogin) {
                    saveGooglePlusCurrentUser()
                } else if (isFacebookLogin) {
                    saveFacebookUser()
                } else {
                    jsonExp.printStackTrace()
                    Toast.makeText(activity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    val failedLoginDialog = AlertDialog.Builder(activity)
                    failedLoginDialog.setMessage("Invalid username or password")
                    failedLoginDialog.setPositiveButton("OK", null)
                    failedLoginDialog.show()
                    //progressDialog.dismiss();
                }
                return
            }

        } else {

            if (isGooglePlusLogin) {
                saveGooglePlusCurrentUser()
            } else if (isFacebookLogin) {
                saveFacebookUser()
            } else {
                //Log.dMainActivity.TAG, "I am here !!!!  ")
                val failedLoginDialog = AlertDialog.Builder(activity)
                failedLoginDialog.setMessage("Invalid username or password")
                failedLoginDialog.setPositiveButton("OK", null)
                failedLoginDialog.show()
            }

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

