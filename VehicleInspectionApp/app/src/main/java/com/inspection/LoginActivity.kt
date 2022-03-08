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
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.ButtonBarLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import com.inspection.Utils.toast
import com.inspection.model.*
import com.inspection.serverTasks.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import kotlinx.android.synthetic.main.dialog_user_register.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient


//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import java.io.IOException
import java.net.URLEncoder
import java.util.*

import java.util.concurrent.TimeUnit

class LoginActivity : Activity(){

    internal var registerDialog: AlertDialog? = null
    private var mCirclesList: ArrayList<String>? = null

    private var specialistArrayModel = ArrayList<TypeTablesModel.employeeList>()
    private var changedPassword=""

    private var mSignInProgress: Int = 0

    private var mSignInIntent: PendingIntent? = null

    private var mSignInError: Int = 0

    private var hidePassword: Boolean = true


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


        if (BuildConfig.FLAVOR.equals("dev")){
            envText.visibility = View.VISIBLE
            envText.text = "DEVELOPMENT ENVIRONMENT"
        } else if (BuildConfig.FLAVOR.equals("uat")){
            envText.visibility = View.VISIBLE
            envText.text = "UAT ENVIRONMENT"
        } else if (BuildConfig.FLAVOR.equals("production")){
            envText.visibility = View.GONE
//            envText.text = "PRODUCTION ENVIRONMENT"
        }

            ApplicationPrefs.getInstance(activity).sessionID = UUID.randomUUID().toString()
        ApplicationPrefs.getInstance(activity).deviceID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID)

        showPassBtn.setOnClickListener {
            if (hidePassword) {
                loginPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPassBtn.background = this.getDrawable(R.drawable.eyehideicon)
            } else {
                loginPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showPassBtn.background = this.getDrawable(R.drawable.eyeicon)
            }
            hidePassword = !hidePassword
        }

        forgotPasswordButton!!.setOnClickListener {
            if (loginEmailEditText.text.isNullOrEmpty()) {
                Utility.showValidationAlertDialog(activity, "Please enter your email address ")
            } else {
                val builder = AlertDialog.Builder(activity)

                builder.setTitle("Confirmation ...")
                builder.setMessage("Are you sure you want to reset your password ?")
                builder.setCancelable(false)
                builder.setPositiveButton("YES") { dialog, which ->
                    val userEmail = loginEmailEditText.text.toString()//URLEncoder.encode(loginEmailEditText.text.toString(), "UTF-8");
                    Log.v("LOGIN : "+ "RESET PASS  -- ",Constants.resetPassword + userEmail)
                    Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.resetPassword + userEmail,
                            Response.Listener { response ->
                                Log.v("asd", "asd")
                                activity!!.runOnUiThread {
                                    Log.v("RESPONSE", response.toString())
                                    if (response.toString().contains("Success", false)) {
                                        Utility.showMessageDialog(activity, "Confirmation...", "Please check your email for your temp password")
                                    } else {
                                        Utility.showMessageDialog(activity, "Connection Error...", "Error while resetting password ... Please try again ...")
                                    }
                                }
                            }, Response.ErrorListener {
                        Utility.showMessageDialog(activity, "Login Error ...", it.message)
                    }))
                }
                builder.setNegativeButton("No") { dialog, which ->
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
//                recordsProgressView.visibility = View.VISIBLE

            }
        }

        chgPasswordButtoninDialog.setOnClickListener {
            initiateChangePassword()
        }



            if (!ApplicationPrefs.getInstance(activity).loggedInUserEmail.isNullOrEmpty()) {
                loginEmailEditText.setText(ApplicationPrefs.getInstance(activity).loggedInUserEmail)
                loginPasswordEditText.setText(ApplicationPrefs.getInstance(activity).loggedInUserPass)
            }
            //loginEmailEditText.setText("Johnson.Fredrick@aaa-texas.Com")

            loginButton!!.setOnClickListener {
                if (loginEmailEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty() && loginPasswordEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    executeLogin()
                } else {
                    Utility.showValidationAlertDialog(activity,"Please enter your email and password")
//                    val alertDialog = AlertDialog.Builder(activity)
//                    alertDialog.setMessage("Please enter your email and password")
//                    alertDialog.setPositiveButton("OK", null)
//                    alertDialog.show()
                }
            }

//        loginEmailEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                loginEmailEditText.isEnabled = p0.toString().length > 0
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//        })


        }


        fun getTypeTables() {
            var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            var client = clientBuilder.build()
            var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
            recordsProgressView.visibility = View.VISIBLE
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("&&&&&*(*", "failed with exception : " + e!!.message)
                    activity!!.runOnUiThread {
                        Utility.showMessageDialog(activity, "Retrieve Data Error", e.message)
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {

                    var responseString = response!!.body!!.string()
                    if (responseString.toString().contains("returnCode>1<", false)) {
                        activity!!.runOnUiThread {
                            Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                            recordsProgressView.visibility = View.GONE
                        }
                    } else {
                        var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")))
                        var jsonObj = obj.getJSONObject("responseXml")
                        TypeTablesModel.setInstance(Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java))
                        (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
                            TypeTablesModel.getInstance().EmployeeList[it].FullName = TypeTablesModel.getInstance().EmployeeList[it].FirstName + " " + TypeTablesModel.getInstance().EmployeeList[it].LastName
                        }
                        specialistArrayModel = TypeTablesModel.getInstance().EmployeeList

                        activity!!.runOnUiThread {
                            if (specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }.size > 0) {
                                ApplicationPrefs.getInstance(activity).loggedInUserEmail = loginEmailEditText!!.text.toString()
                                ApplicationPrefs.getInstance(activity).loggedInUserPass = loginPasswordEditText!!.text.toString()
                                ApplicationPrefs.getInstance(activity).loggedInUserID = ""
                                ApplicationPrefs.getInstance(activity).loggedInUserFullName = ""
                            } else {
                                ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].NTLogin
                                ApplicationPrefs.getInstance(activity).loggedInUserFullName = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].FullName
                            }
//                                ApplicationPrefs.getInstance(activity).sessionID = UUID.randomUUID().toString()
//                                ApplicationPrefs.getInstance(activity).deviceID = Settings.Secure.getString(getContentResolver(),
//                                        Settings.Secure.ANDROID_ID)
                                userIsLoggedInGotoMainActivity()
//                            } else {
//                                Utility.showMessageDialog(activity, "Login Failed...", "This email is not listed in specialists list")
//                            }
                            recordsProgressView.visibility = View.GONE
                        }
                    }
                }
            })
        }

//    fun getTypeTablesStatic() {
//        var responseString = Constants.getTableTypesStatic
//        if (responseString.toString().contains("returnCode>1<", false)) {
//            activity!!.runOnUiThread {
//                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
//                recordsProgressView.visibility = View.GONE
//            }
//        } else {
//            var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")))
//            var jsonObj = obj.getJSONObject("responseXml")
//            TypeTablesModel.setInstance(Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java))
//            (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
//                TypeTablesModel.getInstance().EmployeeList[it].FullName = TypeTablesModel.getInstance().EmployeeList[it].FirstName + " " + TypeTablesModel.getInstance().EmployeeList[it].LastName
//            }
//            specialistArrayModel = TypeTablesModel.getInstance().EmployeeList
//
//            activity!!.runOnUiThread {
//                if (specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }.size > 0) {
//                    ApplicationPrefs.getInstance(activity).loggedInUserEmail = loginEmailEditText!!.text.toString()
//                    ApplicationPrefs.getInstance(activity).loggedInUserPass = loginPasswordEditText!!.text.toString()
//                    ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].NTLogin
//                    ApplicationPrefs.getInstance(activity).loggedInUserFullName = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].FullName
////                                ApplicationPrefs.getInstance(activity).sessionID = UUID.randomUUID().toString()
////                                ApplicationPrefs.getInstance(activity).deviceID = Settings.Secure.getString(getContentResolver(),
////                                        Settings.Secure.ANDROID_ID)
//                    userIsLoggedInGotoMainActivity()
//                } else {
//                    Utility.showMessageDialog(activity, "Login Failed...", "This email is not listed in specialists list")
//                }
//                recordsProgressView.visibility = View.GONE
//            }
//        }
//    }

//    private fun loadSpecialistName(){
//
//        if (specialistArrayModel != null && specialistArrayModel.size > 0) {
//            requiredSpecialistName = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(ApplicationPrefs.getInstance(activity).loggedInUserEmail.toLowerCase()) }[0].FullName
//            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(ApplicationPrefs.getInstance(activity).loggedInUserEmail.toLowerCase()) }[0].NTLogin
//        }
//
//    }


        private fun executeLogin() {
//            throw RuntimeException("Test Crash"); // Force a crash
            val userEmail = URLEncoder.encode(loginEmailEditText.text.toString(), "UTF-8");
            val userPass = URLEncoder.encode(loginPasswordEditText.text.toString(), "UTF-8");
            Log.v("LOGIN : "+ "EXEC LOGIN -- ",Constants.authenticateUrl + userEmail + "&password=" + userPass)
            Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.authenticateUrl + userEmail + "&password=" + userPass,
                    Response.Listener { response ->
                        activity!!.runOnUiThread {
                            Log.v("RESPONSE", response.toString())
                            if (response.toString().contains("1}]", false)) {
                                getTypeTables();
                                //getTypeTablesStatic();
                            } else if (response.toString().contains("2}]", false)) {
                                recordsProgressView.visibility = View.VISIBLE
                                changePassDialog.visibility = View.VISIBLE
                            } else {
                                var errorMessage = "Please validate the email and/or password"
                                Utility.showMessageDialog(activity, "Login Error ...", errorMessage)
                            }
                        }
                    }, Response.ErrorListener {
                Utility.showMessageDialog(activity, "Login Error ...", it.message)
            }))
        }


        private fun userIsLoggedInGotoMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        private fun initiateChangePassword(){
            if (chgPasswordEditText!!.text.toString().trim { it <= ' ' }.isEmpty() || chgPasswordConfirmEditText!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                Utility.showValidationAlertDialog(activity, "Please enter passowrd and confirm password")
            } else if (!chgPasswordEditText.text.toString().equals(chgPasswordConfirmEditText.text.toString())) {
                Utility.showValidationAlertDialog(activity, "Passowrd and confirm password are not matching")
            } else {
                val userEmail = URLEncoder.encode(loginEmailEditText.text.toString(), "UTF-8");
                val userPass = URLEncoder.encode(chgPasswordEditText.text.toString(), "UTF-8");
                Log.v("LOGIN : "+ "CHG PASS -- ",Constants.changePassword + userEmail +"&password="+userPass)
                Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.changePassword + userEmail +"&password="+userPass,
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                Log.v("RESPONSE", response.toString())
                                if (response.toString().contains("Success", false)) {
                                    Utility.showMessageDialog(activity,"Confirmation...","Password changed succesfully ...")
                                    loginPasswordEditText.setText(userPass)
                                } else {
                                    var errorMessage = "Error changing password"
                                    Utility.showMessageDialog(activity, "Error ...", errorMessage)
                                }
                                changePassDialog.visibility = View.GONE
                                recordsProgressView.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
                    Utility.showMessageDialog(activity, "Error ...", it.message)
                    changePassDialog.visibility = View.GONE
                    recordsProgressView.visibility = View.GONE
                }))

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