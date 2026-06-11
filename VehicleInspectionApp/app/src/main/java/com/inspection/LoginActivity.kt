package com.inspection


//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

//import kotlinx.android.synthetic.main.activity_login.*
//import kotlinx.android.synthetic.main.dialog_forgot_password.*
//import kotlinx.android.synthetic.main.dialog_user_register.*
//import org.json.XML
//import com.inspection.xml_utils.XmlParser
import android.app.*
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bugfender.sdk.Bugfender
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.inspection.MainActivity.Companion
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Constants.IDLE_TIMEOUT
import com.inspection.Utils.Utility
import com.inspection.Utils.checkInternetAndSpeed
import com.inspection.Utils.toApiSubmitFormat
import com.inspection.Utils.toDBFormat
import com.inspection.databinding.ActivityLoginBinding
import com.inspection.model.*
import com.inspection.serverTasks.*
import com.inspection.utils.XmlUtils
import com.inspection.utils.XmlUtils.normalizeForModel
import com.inspection.utils.XmlUtils.normalizeJsonArrays
import com.inspection.utils.XmlUtils.xmlToJsonObject

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
//import shaded.org.json.JSONObject
//import org.json.JSONObject
//import org.sha.json.XML

//import org.json.XML
//import shaded.org.json.XML
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault
import java.util.concurrent.TimeUnit
import kotlin.toString
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class LoginActivity : AppCompatActivity() ,NetworkSpeedDetector.NetworkSpeedListener {

    internal var registerDialog: AlertDialog? = null
    private var mCirclesList: ArrayList<String>? = null

    private var specialistArrayModel = ArrayList<TypeTablesModel.employeeList>()
    private var changedPassword=""

    private var mSignInProgress: Int = 0

    private var mSignInIntent: PendingIntent? = null

    private var mSignInError: Int = 0

    private var hidePassword: Boolean = true
    private lateinit var binding: ActivityLoginBinding


    private val mRequestServerAuthCode = false


    private val mServerHasToken = true

    internal var isFacebookLogin = false
    internal var isGooglePlusLogin = false

    internal var progressDialog: ProgressDialog? = null
    internal var forgotPasswordDialog: AlertDialog? = null

    internal var activity: Activity? = null
    private lateinit var networkSpeedDetector: NetworkSpeedDetector
    internal var currentUser: GoogleSignInAccount? = null

//    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var timer: Timer
    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_login)
        setContentView(binding.root)
        window.setBackgroundDrawableResource(R.drawable.login_background_image_dark)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        activity = this
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
//        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (BuildConfig.FLAVOR.equals("dev")){
            binding.envText.visibility = View.VISIBLE
            binding.envText.text = "DEVELOPMENT ENVIRONMENT"
        } else if (BuildConfig.FLAVOR.equals("uat")){
            binding.envText.visibility = View.VISIBLE
            binding.envText.text = "UAT ENVIRONMENT"
        } else if (BuildConfig.FLAVOR.equals("production")){
            binding.envText.visibility = View.GONE
//            envText.text = "PRODUCTION ENVIRONMENT"
        }
        networkSpeedDetector = NetworkSpeedDetector(this);
        networkSpeedDetector.setNetworkSpeedListener(this);
        networkSpeedDetector.startMonitoring();

//        networkCallback = object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                // Network is available
//                runOnUiThread {
//                    Toast.makeText(this@LoginActivity, "Network is available", Toast.LENGTH_SHORT).show()
//                    checkNetworkSpeed()
//                }
//            }
//
//            override fun onLost(network: Network) {
//                // Network is lost
//                runOnUiThread {
//                    Toast.makeText(this@LoginActivity, "Network is lost", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
//                // Network capabilities changed
//                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
//                    runOnUiThread {
//                        checkNetworkSpeed()
//                        Toast.makeText(this@LoginActivity, "Internet is available", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }


        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)  // Monitor Wi-Fi specifically
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_MMS)
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_SUPL)
            .build()
//        connectivityManager.registerNetworkCallback(request,networkCallback)


//        timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                // Update UI from the main thread using the handler
//                handler.post {
//                    checkNetworkSpeed()
//
//                }
//            }
//        }, 0, 5000)

            ApplicationPrefs.getInstance(activity).sessionID = UUID.randomUUID().toString()
        ApplicationPrefs.getInstance(activity).deviceID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID)

        binding.showPassBtn.setOnClickListener {
            if (hidePassword) {
                binding.loginPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.showPassBtn.background = this.getDrawable(R.drawable.eyehideicon)
            } else {
                binding.loginPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.showPassBtn.background = this.getDrawable(R.drawable.eyeicon)
            }
            hidePassword = !hidePassword
        }



        binding.forgotPasswordButton!!.setOnClickListener {
            if (binding.loginEmailEditText.text.isNullOrEmpty()) {
                Utility.showValidationAlertDialog(activity, "Please enter your email address ")
            } else {
                val builder = AlertDialog.Builder(activity)

                builder.setTitle("Confirmation ...")
                builder.setMessage("Are you sure you want to reset your password ?")
                builder.setCancelable(false)
                builder.setPositiveButton("YES") { dialog, which ->
                    val userEmail = binding.loginEmailEditText.text.toString()//URLEncoder.encode(loginEmailEditText.text.toString(), "UTF-8");
                    Log.v("LOGIN : "+ "RESET PASS  -- ",Constants.resetPassword + userEmail)
                    Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.resetPassword + userEmail,
                            Response.Listener { response ->
                                Log.v("asd", "asd")
                                activity!!.runOnUiThread {
                                    Log.v("RESPONSE", response.toString())
                                    if (response.toString().contains("Success", false)) {
//                                        Utility.showMessageDialog(activity, "Confirmation...", "Please check your email for your temp password")
                                        Utility.showUnifiedConfirmationDialog(activity,"Please check your email for your temp password")
                                    } else {
//                                        Utility.showMessageDialog(activity, "Connection Error...", "Error while resetting password ... Please try again ...")
                                        Utility.showUnifiedErrorDialog(activity,"Error while resetting password ... Please try again ...")
                                    }
                                }
                            }, Response.ErrorListener {
//                        Utility.showMessageDialog(activity, "Login Error ...", it.message)
                            Utility.showUnifiedErrorDialog(activity,it.message)
                    }))
                }
                builder.setNegativeButton("No") { dialog, which ->
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
//                recordsProgressView.visibility = View.VISIBLE

            }
        }

        binding.chgPasswordButtoninDialog.setOnClickListener {
            initiateChangePassword()
        }



            if (!ApplicationPrefs.getInstance(activity).loggedInUserEmail.isNullOrEmpty()) {
                binding.loginEmailEditText.setText(ApplicationPrefs.getInstance(activity).loggedInUserEmail)
                binding.loginPasswordEditText.setText(ApplicationPrefs.getInstance(activity).loggedInUserPass)
            }
            //loginEmailEditText.setText("Johnson.Fredrick@aaa-texas.Com")

        binding.loginButton!!.setOnClickListener {
                if (binding.loginEmailEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty() && binding.loginPasswordEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.recordsProgressView.visibility = View.VISIBLE
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

    override fun onSlowNetworkDetected(message: String) {
        val sb = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
        val customView = layoutInflater.inflate(R.layout.custom_snack_no_signal, null)
        val messageTextView : TextView = customView.findViewById<TextView>(R.id.snackbar_message)
        messageTextView.setText(message)
        // Set Snackbar's background to transparent to only show custom layout
        sb.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        // Add the custom layout to Snackbar
        val snackbarLayout = sb.view as ViewGroup
        snackbarLayout.addView(customView, 0)
        sb.show()
    }

    override fun onNetworkSpeedRestored() {
//        val sb = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
//        val customView = layoutInflater.inflate(R.layout.custom_snack_connected, null)
//        sb.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
//        val snackbarLayout = sb.view as ViewGroup
//        snackbarLayout.addView(customView, 0)
//        sb.show()
    }

    private fun showNetworkIndicator(message: String, isConnected: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        snackbar.setTextColor(resources.getColor(R.color.mainColor))
        snackbar.show()
//        if (!isConnected) {
//            snackbar.setAction("Retry") {
//                // Handle retry logic
//            }.show()
//        } else {
//            snackbar.dismiss()
//        }
    }



//    @SuppressLint("RestrictedApi")
//    private fun showCustomSnackbar(view: View, msg: String) {
//        // Create the default Snackbar
////        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
////
////        // Inflate the custom layout
////        val customSnackbarView = LayoutInflater.from(this).inflate(R.layout.custom_snackbar, null)
//
//        // Get references to the TextView and Button in the custom layout
//        val messageTextView: TextView = snackbar_text
//        val imageView: ImageView = snackbar_icon
//
//        // Set the message and action button behavior
//        messageTextView.text = msg
//        messageTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
//        // Customize the Snackbar layout
//
////        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
////        snackbarLayout.setPadding(0, 0, 0, 0) // Remove default padding
////        snackbarLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)) // Make background transparent
//
//        // Add custom view to Snackbar layout
////        snackbarLayout.addView(customSnackbarView, 0)
//        // Show the Snackbar
////        snackbar.show()
//    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network callback when the activity is destroyed
//        connectivityManager.unregisterNetworkCallback(networkCallback)
//        timer.cancel()
    }

//    private fun checkNetworkSpeed() {
//        val network = connectivityManager.activeNetwork ?: return
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return
//
//        val downlinkSpeed = networkCapabilities.linkDownstreamBandwidthKbps
//        val uplinkSpeed = networkCapabilities.linkUpstreamBandwidthKbps
//
//        runOnUiThread {
////            showNetworkIndicator("Download Speed: $downlinkSpeed kbps\nUpload Speed: $uplinkSpeed kbps", true)
//            showCustomSnackbar(findViewById(android.R.id.content),"Download Speed: $downlinkSpeed kbps\nUpload Speed: $uplinkSpeed kbps")
//            Toast.makeText(this@LoginActivity, "Download Speed: $downlinkSpeed kbps\nUpload Speed: $uplinkSpeed kbps", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun checkRemoteConfigVersion() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val fetchInterval = if (BuildConfig.DEBUG) 0L else 3600L
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(fetchInterval)
            .build()
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(mapOf(
            "latest_version_code" to 0L,
            "latest_version_name" to ""
        ))
        android.util.Log.d("RC_VERSION", "Fetching Remote Config — installed versionCode=${BuildConfig.VERSION_CODE} versionName=${BuildConfig.VERSION_NAME}")
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.w("RC_VERSION", "fetchAndActivate failed: ${task.exception?.message}")
                return@addOnCompleteListener
            }
            val remoteVersionCode = remoteConfig.getLong("latest_version_code").toInt()
            val remoteVersionName = remoteConfig.getString("latest_version_name")
            android.util.Log.d("RC_VERSION", "Fetched — latest_version_code=$remoteVersionCode latest_version_name='$remoteVersionName'")
            android.util.Log.d("RC_VERSION", "Comparison — remote($remoteVersionCode) > installed(${BuildConfig.VERSION_CODE}) = ${remoteVersionCode > BuildConfig.VERSION_CODE}")
            if (remoteVersionCode > BuildConfig.VERSION_CODE) {
                android.util.Log.d("RC_VERSION", "Showing update dialog")
                showUpdateAvailableDialog(remoteVersionName)
            } else {
                android.util.Log.d("RC_VERSION", "No update dialog — app is up to date")
            }
        }
    }

    private fun showUpdateAvailableDialog(versionName: String) {
        if (isFinishing || isDestroyed) return
        val message: CharSequence = if (versionName.isNotBlank()) {
            val prefix = "Version "
            val suffix = " is now available. Please update the app to get the latest features and improvements."
            android.text.SpannableStringBuilder(prefix + versionName + suffix).apply {
                val start = prefix.length
                val end = start + versionName.length
                setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#1565C0")), start, end, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            "A new version of the app is available. Please update to get the latest features and improvements."
        }
        val dialog = android.app.Dialog(this, R.style.CompactDialog)
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_available, null)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialogView.findViewById<android.widget.TextView>(R.id.tvMessage).text = message
        dialogView.findViewById<android.widget.Button>(R.id.btnAction).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.setWindowAnimations(R.style.DialogPopAnimation)
        val widthPx = (320 * resources.displayMetrics.density).toInt()
        dialog.window?.setLayout(widthPx, android.view.WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.decorView?.setPadding(0, 0, 0, 0)
    }

    fun getAppVersion() {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Get App Version Step")
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getAppVersion).build()
        binding.recordsProgressView.visibility = View.VISIBLE
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("&&&&&*(*", "failed with exception : " + e!!.message)
                activity!!.runOnUiThread {
//                    Utility.showMessageDialog(activity, "Retrieve Data Error", e.message)
                    Utility.showUnifiedErrorDialog(activity,"Retrieve Data Error - ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response!!.body!!.string()
                if (!responseString.replace(" ","").equals("[]")) {
                    PRGDataModel.getInstance().tblPRGAppVersion = Gson().fromJson(responseString, Array<PRGAppVersion>::class.java).toCollection(ArrayList())
                }
                activity!!.runOnUiThread {
                    if (!resources.getString(R.string.app_version).equals(PRGDataModel.getInstance().tblPRGAppVersion[0].version) && PRGDataModel.getInstance().tblPRGAppVersion[0].enabled==1) {
                        var alertBuilder = androidx.appcompat.app.AlertDialog.Builder(activity!!);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Note")
                        alertBuilder.setMessage("New ${PRGDataModel.getInstance().tblPRGAppVersion[0].version} is available with the below changes: \n" +
                                " ${PRGDataModel.getInstance().tblPRGAppVersion[0].message} \n" +
                                " Do you Want to Continue ?");
                        alertBuilder.setPositiveButton("YES") { dialog, which ->
                            getTypeTables();
                        }
                        alertBuilder.setNegativeButton("NO") { dialog, which ->
                            finishAndRemoveTask();
                        }
                        val alert = alertBuilder.create();
                        alert.show();
                    } else {
                        getTypeTables();
                    }

                }
            }
        })
    }

    override fun onPause() {
        lastActiveTime = System.currentTimeMillis()
        super.onPause()
    }

    override fun onResume() {
        val now = System.currentTimeMillis()
        if (now - lastActiveTime > IDLE_TIMEOUT) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        lastActiveTime = now
        super.onResume()
        checkRemoteConfigVersion()
    }

        fun getTypeTables() {
//            FirebaseCrashlytics.getInstance().setCustomKey("Details", "Get Table Types Step")
            var clientBuilder = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            var client = clientBuilder.build()
            var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
            binding.recordsProgressView.visibility = View.VISIBLE
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("&&&&&*(*", "failed with exception : " + e!!.message)
                    activity!!.runOnUiThread {
//                        Utility.showMessageDialog(activity, "Retrieve Data Error", e.message)
                        Utility.showUnifiedErrorDialog(activity,"Get Type Tables - " + e.message)
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {

                    var responseString = response!!.body!!.string()
                    if (responseString.toString().contains("returnCode>1<", false)) {
                        activity!!.runOnUiThread {
//                            Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                            Utility.showUnifiedErrorDialog(activity,"Get Type Tables - " + responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                            binding.recordsProgressView.visibility = View.GONE
                        }
                    } else {
//                        var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")))
//                        var jsonObj = obj.getJSONObject("responseXml")
//                        TypeTablesModel.setInstance(
//                            Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java)
//                        )

                        val xmlPart = responseString.substring(
                            responseString.indexOf("<responseXml"),
                            responseString.indexOf("<returnCode")
                        )

                        val rootJson = xmlToJsonObject(xmlPart)
                        Log.v("rootJson -> ", rootJson.toString())

// IMPORTANT: Jsoup already removed wrapper
                        val responseJson = rootJson

                        Log.v("responseJson -> ", responseJson.toString())

                        val normalized = normalizeForModel(
                            responseJson,
                            TypeTablesModel::class.java
                        )

                        Log.v("normalized -> ", normalized.toString())

                        TypeTablesModel.setInstance(
                            Gson().fromJson(normalized, TypeTablesModel::class.java)
                        )

                        (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
                            TypeTablesModel.getInstance().EmployeeList[it].FullName = TypeTablesModel.getInstance().EmployeeList[it].FirstName + " " + TypeTablesModel.getInstance().EmployeeList[it].LastName
                        }
                        specialistArrayModel = TypeTablesModel.getInstance().EmployeeList

                        activity!!.runOnUiThread {
                            if (specialistArrayModel.filter { s -> s.Email.lowercase(getDefault())
                                    .equals(
                                        binding.loginEmailEditText.text.toString()
                                            .lowercase(getDefault())
                                    ) }.size > 0) {
                                ApplicationPrefs.getInstance(activity).loggedInUserEmail = binding.loginEmailEditText!!.text.toString()

                                ApplicationPrefs.getInstance(activity).loggedInUserPass = binding.loginPasswordEditText!!.text.toString()
                                ApplicationPrefs.getInstance(activity).loggedInUserID = ""
                                ApplicationPrefs.getInstance(activity).loggedInUserFullName = ""
                                ApplicationPrefs.getInstance(activity).loggedInIsSpecialist = "Yes"
                            } else {
//                                ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].NTLogin
//                                ApplicationPrefs.getInstance(activity).loggedInUserFullName = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(loginEmailEditText.text.toString().toLowerCase()) }[0].FullName
                                ApplicationPrefs.getInstance(activity).loggedInUserEmail = binding.loginEmailEditText.text.toString()

                                ApplicationPrefs.getInstance(activity).loggedInUserPass = binding.loginPasswordEditText!!.text.toString()
                                ApplicationPrefs.getInstance(activity).loggedInIsSpecialist = "No"
                            }
                                FirebaseCrashlytics.getInstance().log("Logged In Successfully")
                                userIsLoggedInGotoMainActivity()
//                            } else {
//                                Utility.showMessageDialog(activity, "Login Failed...", "This email is not listed in specialists list")
//                            }
//                            recordsProgressView.visibility = View.GONE
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
//            Utility.showMessageDialog(activity, "Internet Speed", checkInternetAndSpeed(this))
            checkInternetAndSpeed(this)

            val keysAndValues = CustomKeysAndValues.Builder()
                    .putString("Email", binding.loginEmailEditText.text.toString())
                    .putString("Login Time", Date().toApiSubmitFormat())
                    .putString("Session ID", ApplicationPrefs.getInstance(activity).sessionID)
                    .build()
            Bugfender.setDeviceString("User Email", binding.loginEmailEditText.text.toString())
            Bugfender.setDeviceString("App Version", BuildConfig.VERSION_NAME)
            Bugfender.i("Login Time", Date().toDBFormat())
            FirebaseCrashlytics.getInstance().setCustomKeys(keysAndValues)
            checkInternetAndSpeed(this)
//            FirebaseCrashlytics.getInstance().setCustomKey("Internet Speed", checkInternetAndSpeed(this))

            FirebaseCrashlytics.getInstance().setUserId(binding.loginEmailEditText.text.toString())
//            Log.v("START CRASH","---> HERE")
//            FirebaseCrashlytics.getInstance().recordException(RuntimeException("Test exception for Crashlytics"));
//            throw RuntimeException("Test Crash"); // Force a crash
//            try {
//                throw RuntimeException("Test Crash") // This should normally crash the app
//            } catch (e: RuntimeException) {
//                e.printStackTrace()
//                // Force the app to crash
//                System.exit(1)
//                // Or alternatively:
//                // android.os.Process.killProcess(android.os.Process.myPid());
//            }

//            throw RuntimeException("Test Crash"); // Force a crash

            val userEmail = URLEncoder.encode(binding.loginEmailEditText.text.toString(), "UTF-8");
            val userPass = URLEncoder.encode(binding.loginPasswordEditText.text.toString(), "UTF-8");
            Log.v("LOGIN : "+ "EXEC LOGIN -- ",Constants.authenticateUrl + userEmail + "&password=" + userPass + "&version=${resources.getString(R.string.app_version)}")
            Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.authenticateUrl + userEmail + "&password=" + userPass + "&version=${resources.getString(R.string.app_version)}",
                    Response.Listener { response ->
                        activity!!.runOnUiThread {
                            Log.v("RESPONSE", response.toString())
                            if (response.toString().contains("1}]", false)) {
                                getAppVersion()
                                //getTypeTablesStatic();
                            } else if (response.toString().contains("2}]", false)) {
                                binding.recordsProgressView.visibility = View.VISIBLE
                                binding.changePassDialog.visibility = View.VISIBLE
                            } else {
                                var errorMessage = "Please validate the email and/or password"
//                                Utility.showMessageDialog(activity, "Login Error ...", errorMessage)
                                Utility.showUnifiedErrorDialog(activity,errorMessage)
                                binding.recordsProgressView.visibility = View.GONE
                            }
                        }
                    }, Response.ErrorListener {
//                    Utility.showMessageDialog(activity, "Login Error ...", it.message)
                    Utility.showUnifiedErrorDialog(activity,it.message)
                    binding.recordsProgressView.visibility = View.GONE
            }))
        }


        private fun userIsLoggedInGotoMainActivity() {
//            FirebaseCrashlytics.getInstance().setCustomKey("Details", "Starting Main Activity")
//            throw RuntimeException("Test Crash"); // Force a crash
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        private fun initiateChangePassword(){
            if (binding.chgPasswordEditText!!.text.toString().trim { it <= ' ' }.isEmpty() || binding.chgPasswordConfirmEditText!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                Utility.showValidationAlertDialog(activity, "Please enter passowrd and confirm password")
            } else if (!binding.chgPasswordEditText.text.toString().equals(binding.chgPasswordConfirmEditText.text.toString())) {
                Utility.showValidationAlertDialog(activity, "Passowrd and confirm password are not matching")
            } else {
                val userEmail = URLEncoder.encode(binding.loginEmailEditText.text.toString(), "UTF-8");
                val userPass = URLEncoder.encode(binding.chgPasswordEditText.text.toString(), "UTF-8");
                Log.v("LOGIN : "+ "CHG PASS -- ",Constants.changePassword + userEmail +"&password="+userPass)
                Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.changePassword + userEmail +"&password="+userPass,
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                Log.v("RESPONSE", response.toString())
                                if (response.toString().contains("Success", false)) {
//                                    Utility.showMessageDialog(activity,"Confirmation...","Password changed succesfully ...")
                                    Utility.showUnifiedConfirmationDialog(activity,"Password changed successfully")
                                    binding.loginPasswordEditText.setText(userPass)
                                } else {
                                    var errorMessage = "Error changing password - " + response.toString()
//                                    Utility.showMessageDialog(activity, "Error ...", errorMessage)
                                    Utility.showUnifiedErrorDialog(activity,"" + errorMessage)
                                }
                                binding.changePassDialog.visibility = View.GONE
                                binding.recordsProgressView.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
//                    Utility.showMessageDialog(activity, "Error ...", it.message)
                        Utility.showUnifiedErrorDialog(activity,"Error changing password - " + it.message)
                        binding.changePassDialog.visibility = View.GONE
                        binding.recordsProgressView.visibility = View.GONE
                }))

            }


        }



    companion object {

        private val TAG = "LoginActivity"


        private val STATE_DEFAULT = 0
        private val STATE_SIGN_IN = 1
        private val STATE_IN_PROGRESS = 2

        var lastActiveTime: Long = System.currentTimeMillis()
//        const val IDLE_TIMEOUT = 1 * 60 * 1000L // 5 minutes

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