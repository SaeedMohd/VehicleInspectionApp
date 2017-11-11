package com.matics.fragments

import com.matics.MainActivity
import com.matics.R
import com.matics.CrashReport.CustomUncaughtExceptionHandler
import com.matics.imageloader.Utils
import com.matics.Utils.ApplicationPrefs
import com.matics.serverTasks.GetCompetitorsListTask

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TableRow
import android.widget.TextView
import android.app.Fragment
import android.content.Intent
import android.opengl.Visibility
import android.view.View.*
import kotlinx.android.synthetic.main.fragment_vehicle_setting.*

import java.util.Calendar
import java.util.Date

class FragmentVehicleSettings : Fragment(), OnClickListener {


//    internal var tableRowGeneral: TableRow? = null
//    internal var trAppointment: TableRow? = null
//    internal var trCalender: TableRow? = null
//    internal var trNotifications: TableRow? = null
//    internal var trReminders: TableRow? = null
//    internal var trRepairShopInfo: TableRow? = null
//    internal var trVehicleInfo: TableRow? = null
//    internal var trWebSite: TableRow? = null
//    internal var generalTextView: TextView? = null
//    internal var appointmentTextView: TextView? = null
//    internal var calenderTextView: TextView? = null
//    internal var notificationsTextView: TextView? = null
//    internal var autoPairWithOBDTextView: TextView? = null
//    internal var remindersTextView: TextView? = null
//    internal var tvRepairShopInfo: TextView? = null
//    internal var tvVehicleInfo: TextView? = null
//    internal var webSiteTextView: TextView? = null
//    internal var linearLayout_General: LinearLayout? = null
//    internal var linearLayout_Appointment: LinearLayout? = null
//    internal var linear_Appointment: LinearLayout? = null
//    internal var linearLayout_Calender: LinearLayout? = null
//    internal var linearLayout_WebSite: LinearLayout? = null
//    internal var linear_calender: LinearLayout? = null
//    internal var linearLayout_Notifications: LinearLayout? = null
//    internal var linearLayout_Reminders: LinearLayout? = null
//    internal var linear_reminder: LinearLayout? = null
//    internal var linearLayout_RepairShopInfo: LinearLayout? = null
//    internal var linear_repairshop: LinearLayout? = null
//    internal var linear_WebSite: LinearLayout? = null
//    internal var linear_General: RelativeLayout? = null
//    internal var linear_notification: RelativeLayout? = null
//    internal var bluetoothNotificatioinSettingCheckBox: CheckBox? = null
//    internal var bluetoothSnoozeNotificationSettingCheckBox: CheckBox? = null
//    internal var autoPairWithOBDCheckBox: CheckBox? = null
//    internal var receivePushNotificationsTextView: TextView? = null
//    internal var receiveEmailNotificationsTextView: TextView? = null
//    internal var receiveSMSNotificationsTextView: TextView? = null
//    internal var receivePushNotificationsCheckBox: CheckBox? = null
//    internal var receiveEmailNotificationsCheckBox: CheckBox? = null
//    internal var receiveSMSNotificationsCheckBox: CheckBox? = null
//    internal var bluetoothSnoozeNotificationSettingTextView: TextView? = null
//    internal var bluetoothSnoozeNotificationRemainingTime: TextView? = null
//    internal var updateCompList: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //------------initializing variables
        Thread.setDefaultUncaughtExceptionHandler(CustomUncaughtExceptionHandler())
        val view = inflater.inflate(R.layout.fragment_vehicle_setting, container, false)
        (activity as MainActivity).supportActionBar!!.title = "Settings"

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize(view)
    }

    private fun initalize(view: View?) {
        val Bgimage = view!!.findViewById<ImageView>(R.id.imagebg)
        Utils.setShopImage(activity, Bgimage)


//        tableRowGeneral = view.findViewById<TableRow>(R.id.tableRowGeneral)
//        trAppointment = view.findViewById(R.id.tableRowAppointment) as TableRow
//        trCalender = view.findViewById(R.id.tableRowCalender) as TableRow
//        trNotifications = view.findViewById(R.id.tableRowNotifications) as TableRow
//        trReminders = view.findViewById(R.id.tableRowReminders) as TableRow
//        trWebSite = view.findViewById(R.id.tableRowWebSite) as TableRow
//        trRepairShopInfo = view.findViewById(R.id.tableRowRepairShopInfo) as TableRow
//        trVehicleInfo = view.findViewById(R.id.tableRowVehicleInfo) as TableRow
//
//
//        //0---------------initializing Linearlayouts
//        linearLayout_General = view.findViewById(R.id.linearLayout_General) as LinearLayout
//        linearLayout_Appointment = view.findViewById(R.id.linearLayout_Appointment) as LinearLayout
//        linear_General = view.findViewById(R.id.linear_General) as RelativeLayout
//        linear_Appointment = view.findViewById(R.id.linear_Appointment) as LinearLayout
//        linearLayout_Calender = view.findViewById(R.id.linearLayout_Calender) as LinearLayout
//        linear_calender = view.findViewById(R.id.linear_calender) as LinearLayout
//        linearLayout_Notifications = view.findViewById(R.id.linearLayout_Notifications) as LinearLayout
//        linear_notification = view.findViewById(R.id.linear_notification) as RelativeLayout
//        linearLayout_Reminders = view.findViewById(R.id.linearLayout_Reminders) as LinearLayout
//        linear_reminder = view.findViewById(R.id.linear_reminder) as LinearLayout
//        linearLayout_WebSite = view.findViewById(R.id.linearLayout_WebSite) as LinearLayout
//        linear_WebSite = view.findViewById(R.id.linear_WebSite) as LinearLayout
//        linearLayout_RepairShopInfo = view.findViewById(R.id.linearLayout_RepairShopInfo) as LinearLayout
//        linear_repairshop = view.findViewById(R.id.linear_repairshop) as LinearLayout

        linear_repairshop!!.visibility = View.GONE
        linear_reminder!!.visibility = View.GONE
        //		linear_notification.setVisibility(View.GONE);
        linearLayout_RepairShopInfo!!.visibility = View.GONE
        linearLayout_Reminders!!.visibility = View.GONE
        //		linearLayout_Notifications.setVisibility(View.GONE);

        //-----------Applying CLick Event
        linearLayout_General!!.setOnClickListener(this)
        linearLayout_Appointment!!.setOnClickListener(this)
        linear_Appointment!!.setOnClickListener(this)
        linearLayout_Calender!!.setOnClickListener(this)
        linear_calender!!.setOnClickListener(this)
        linearLayout_Notifications!!.setOnClickListener(this)
        linear_notification!!.setOnClickListener(this)
        //linearLayout_Reminders.setOnClickListener(this);
        //linear_reminder.setOnClickListener(this);
        //linearLayout_RepairShopInfo.setOnClickListener(this);
        //linear_repairshop.setOnClickListener(this);
        linearLayout_WebSite!!.setOnClickListener(this)
        linear_WebSite!!.setOnClickListener(this)



        tableRowGeneral!!.setOnClickListener(this)
        tableRowAppointment!!.setOnClickListener(this)
        tableRowCalender!!.setOnClickListener(this)
        tableRowNotifications!!.setOnClickListener(this)
        //tableRowReminders.setOnClickListener(this);
        //tableRowRepairShopInfo.setOnClickListener(this);
        tableRowVehicleInfo!!.setOnClickListener(this)


        //tvCalender, tvNotifications, tvReminders, tvRepairShopInfo, tvVehicleInfo;



        bluetoothNotificationSettingTextView!!.text = "Notify if bluetooth is Off"

        bluetoothNotificationSettingCheckBox!!.isChecked = ApplicationPrefs.getInstance(activity).isBluetoothNotificationOn
        bluetoothNotificationSettingCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            ApplicationPrefs.getInstance(activity).setBluetoothNotification(isChecked)
            bluetoothSnoozeNotificationSettingCheckBox!!.isEnabled = isChecked
            if (!isChecked) {
                bluetoothSnoozeNotificationSettingCheckBox!!.isChecked = false
                ApplicationPrefs.getInstance(activity).isBluetoothSnoozeRequired = false
                ApplicationPrefs.getInstance(activity).enableNotificationTime = 0
                bluetoothSnoozeRemainingTimeTextView!!.text = ""
                bluetoothSnoozeRemainingTimeTextView!!.visibility = GONE
            }
        }


        bluetoothSnoozeNotificationSettingTextView!!.text = "Bluetooth Notification Snoozed:"

        bluetoothSnoozeNotificationSettingCheckBox!!.setOnClickListener {
            if (bluetoothSnoozeNotificationSettingCheckBox!!.isChecked) {
                showSnoozeDialog()
            } else {
                bluetoothSnoozeNotificationSettingCheckBox!!.isChecked = false
                ApplicationPrefs.getInstance(activity).isBluetoothSnoozeRequired = false
                ApplicationPrefs.getInstance(activity).enableNotificationTime = 0
                bluetoothSnoozeRemainingTimeTextView!!.text = ""
                bluetoothSnoozeRemainingTimeTextView!!.visibility = GONE
            }
        }

        if (!bluetoothNotificationSettingCheckBox!!.isChecked) {
            bluetoothSnoozeNotificationSettingCheckBox!!.isEnabled = false
        }
        if (ApplicationPrefs.getInstance(activity).enableNotificationTime > 0) {
            if (ApplicationPrefs.getInstance(activity).enableNotificationTime > Date().time) {
                bluetoothSnoozeRemainingTimeTextView!!.text = getTimeDifferenceInMinutesSecondsFormat(ApplicationPrefs.getInstance(activity).enableNotificationTime - Date().time)
                bluetoothSnoozeRemainingTimeTextView!!.visibility = VISIBLE
                bluetoothSnoozeNotificationSettingCheckBox!!.isChecked = true
            }
        }

        autoPairWithOBDTextView!!.text = "Auto discover and pair with OBD2 devices"

        if (!ApplicationPrefs.getInstance(activity).autoPairWithObd2) {
            autoPairWithOBDCheckBox!!.isChecked = false
        } else {
            autoPairWithOBDCheckBox!!.isChecked = true
        }


        autoPairWithOBDCheckBox!!.setOnClickListener {
            if (autoPairWithOBDCheckBox!!.isChecked) {
                //Log.dMainActivity.TAG, "set auto Pair is set to true");
                ApplicationPrefs.getInstance(activity).autoPairWithObd2 = true
            } else {
                //Log.dMainActivity.TAG, "set auto Pair is set to false");
                ApplicationPrefs.getInstance(activity).autoPairWithObd2 = false
            }
        }

        appointmentTextView!!.text = "No Current Appointments Scheduled"

        calenderTextView!!.text = "No Items Currently On Your Vehicle Health Calendar"

        //		notificationsTextView = (TextView) view.findViewById(R.id.notificationsTextView);

        receivePushNotificationTextView!!.setOnClickListener { receivePushNotificationCheckBox!!.isChecked = !receivePushNotificationCheckBox!!.isChecked }


        receivePushNotificationCheckBox!!.setOnCheckedChangeListener { compoundButton, isChecked -> ApplicationPrefs.getInstance(context).setIsReceivePushNotificationsForSafetyCheck(isChecked) }


        receiveEmailNotificationTextView!!.setOnClickListener { receiveEmailNotificationCheckBox!!.isChecked = !receiveEmailNotificationCheckBox!!.isChecked }



        receiveEmailNotificationCheckBox!!.setOnCheckedChangeListener { compoundButton, isChecked -> ApplicationPrefs.getInstance(context).setIsReceiveEmailNotificationsForSafetyCheck(isChecked) }



        receiveSMSNotificationTextView!!.setOnClickListener { receiveSMSNotificationCheckBox!!.isChecked = !receiveSMSNotificationCheckBox!!.isChecked }



        receiveSMSNotificationCheckBox!!.setOnCheckedChangeListener { compoundButton, isChecked -> ApplicationPrefs.getInstance(context).setIsReceiveSMSNotificationsForSafetyCheck(isChecked) }







        webSiteTextView!!.text = Html.fromHtml("For additional Vehicle Health features log into our website at <a href=\"www.VehicleHealth.org\">www.VehicleHealth.org</a>")
        webSiteTextView!!.setOnClickListener {
            val url = "http://www.VehicleHealth.org"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }



        updateCompListButton!!.setOnClickListener {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Loading Competitors List...")
            progressDialog.isIndeterminate = true
            progressDialog.show()
            object : GetCompetitorsListTask(activity) {
                override fun onTaskCompleted(result: String) {
                    progressDialog.dismiss()
                }
            }.execute()
        }

    }

    override fun onDestroy() {
        println("other detail fragment onDestroy")
        super.onDestroy()
    }

    override fun onResume() {
        println("other detail fragment onResume")
        super.onResume()

    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        when (v.id) {
        //		CALL GENERAL
            R.id.linearLayout_General -> {
                //                linearLayout_General.setBackgroundResource(R.drawable.grey_strip);

                if (generalDownArrow.visibility == View.GONE) {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.GONE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE

                    generalDownArrow.visibility = View.VISIBLE

                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.VISIBLE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE
                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }


            }

        //		CALL APPINTMENT
            R.id.linearLayout_Appointment -> {

                if (appointmentsDownArrow.visibility == View.GONE) {
                    linearLayout_General!!.background = null
                    //			linearLayout_Appointment.se!!.backgroundResource(R.drawable.grey_strip);
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null
                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.VISIBLE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.GONE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    appointmentsDownArrow.visibility = View.VISIBLE

                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }


            }
        //		CALL CALENDER
            R.id.linearLayout_Calender -> {

                if (calendarDownArrow.visibility == View.GONE) {
                    linearLayout_General!!.background = null
                    //			linearLayout_Calender.se!!.backgroundResource(R.drawable.grey_strip);
                    linearLayout_Appointment!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null
                    //Log.e("", "Calender is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.VISIBLE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                    generalDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.GONE

                    calendarDownArrow.visibility = View.VISIBLE

                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }
            }
        //		CALL NOTIFICATIONS
            R.id.linearLayout_Notifications -> {

                if (scNotificationsDownArrow.visibility == View.GONE) {
                    //Log.e("", "notifications is clicked");
                    linearLayout_General!!.background = null
                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    //			linearLayout_Notifications.se!!.backgroundResource(R.drawable.grey_strip);
                    linearLayout_WebSite!!.background = null
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.VISIBLE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.GONE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    scNotificationsDownArrow.visibility = View.VISIBLE

                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }

            }
        //		CALL REMINDERS
            R.id.linearLayout_Reminders -> {

                if (remindersDownArrow.visibility == View.GONE) {
                    linearLayout_General!!.background = null
                    //			linearLayout_Reminders.se!!.backgroundResource(R.drawable.grey_strip);
                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null
                    //Log.e("", "Remenders is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.VISIBLE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.GONE
                    websiteRightArrow.visibility = View.VISIBLE


                    remindersDownArrow.visibility = View.VISIBLE

                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }


            }

        //CALL WebSite
            R.id.linearLayout_WebSite -> {

                if (websiteDownArrow.visibility == View.GONE) {
                    //linearLayout_Reminders.se!!.background(null);
                    linearLayout_General!!.background = null
                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    //linearLayout_Notifications.se!!.background(null);
                    //linearLayout_RepairShopInfo.se!!.background(null);
                    //			linearLayout_WebSite.se!!.backgroundResource(R.drawable.grey_strip);
                    //Log.e("", "Remenders is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.VISIBLE

                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE

                    websiteRightArrow.visibility = View.GONE


                    websiteDownArrow.visibility = View.VISIBLE
                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }

            }

        //		CALL REPAIR SHOP INFO
            R.id.linearLayout_RepairShopInfo -> {
                if (repairShopInfoDownArrow.visibility == View.GONE) {

                    //linearLayout_RepairShopInfo.se!!.backgroundResource(R.drawable.grey_strip);
                    linearLayout_General!!.background = null
                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    //linearLayout_Notifications.se!!.background(null);
                    //linearLayout_Reminders.se!!.background(null);
                    linearLayout_WebSite!!.background = null
                    //Log.e("", "Repair is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.VISIBLE
                    linear_WebSite!!.visibility = View.GONE

                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE
                    repairShopInfoDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE
                    repairShopInfoRightArrow.visibility = View.GONE

                    repairShopInfoDownArrow.visibility = View.VISIBLE

                } else {

                    linearLayout_Appointment!!.background = null
                    linearLayout_Calender!!.background = null
                    linearLayout_Notifications!!.background = null
                    linearLayout_Reminders!!.background = null
                    linearLayout_RepairShopInfo!!.background = null
                    linearLayout_WebSite!!.background = null


                    generalDownArrow.visibility = View.GONE
                    calendarDownArrow.visibility = View.GONE
                    appointmentsDownArrow.visibility = View.GONE
                    scNotificationsDownArrow.visibility = View.GONE
                    remindersDownArrow.visibility = View.GONE
                    websiteDownArrow.visibility = View.GONE

                    generalRightArrow.visibility = View.VISIBLE
                    calendarRightArrow.visibility = View.VISIBLE
                    appointmentsRightArrow.visibility = View.VISIBLE
                    scNotificationsRightArrow.visibility = View.VISIBLE
                    remindersRightArrow.visibility = View.VISIBLE
                    websiteRightArrow.visibility = View.VISIBLE


                    //Log.e("", "Appointment is clicked");
                    linear_General!!.visibility = View.GONE
                    linear_Appointment!!.visibility = View.GONE
                    linear_calender!!.visibility = View.GONE
                    linear_notification!!.visibility = View.GONE
                    linear_reminder!!.visibility = View.GONE
                    linear_repairshop!!.visibility = View.GONE
                    linear_WebSite!!.visibility = View.GONE

                }
            }

        //		CALL VEHICLE INFO
            R.id.tableRowVehicleInfo -> {
            }
            else -> {
            }
        }//Log.e("", "Appointment is clicked");
    }


    private fun showSnoozeDialog() {
        val snoozeDialog = AlertDialog.Builder(activity)
        snoozeDialog.setTitle("Snooze Bluetooth Notification")
        snoozeDialog.setPositiveButton("Cancel") { dialog, which -> bluetoothSnoozeNotificationSettingCheckBox!!.isChecked = false }
        snoozeDialog.setItems(R.array.BT_Snooze) { dialog, which ->
            bluetoothSnoozeNotificationSettingCheckBox!!.isChecked = true
            val now = Calendar.getInstance()
            when (which) {
                0 -> {
                    now.add(Calendar.HOUR_OF_DAY, 1)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                1 -> {
                    now.add(Calendar.HOUR_OF_DAY, 2)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                2 -> {
                    now.add(Calendar.HOUR_OF_DAY, 6)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                3 -> {
                    now.add(Calendar.HOUR_OF_DAY, 12)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                4 -> {
                    now.add(Calendar.DAY_OF_MONTH, 1)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                5 -> {
                    now.add(Calendar.DAY_OF_MONTH, 7)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                6 -> {
                    now.add(Calendar.MONTH, 1)
                    //Log.dMainActivity.TAG, ""+now.getTime());
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = now.time.time
                }
                7 -> {
                    ApplicationPrefs.getInstance(activity).setBluetoothNotification(false)
                    ApplicationPrefs.getInstance(activity).isBluetoothSnoozeRequired = false
                    ApplicationPrefs.getInstance(activity).enableNotificationTime = 0
                    bluetoothSnoozeRemainingTimeTextView!!.text = ""
                    bluetoothSnoozeRemainingTimeTextView!!.visibility = GONE
                }
            }//Log.dMainActivity.TAG, "Disable Notification");
            if (ApplicationPrefs.getInstance(activity).enableNotificationTime > 0) {
                bluetoothSnoozeRemainingTimeTextView!!.text = getTimeDifferenceInMinutesSecondsFormat(ApplicationPrefs.getInstance(activity).enableNotificationTime - Date().time)
                bluetoothSnoozeRemainingTimeTextView!!.visibility = VISIBLE
            }
        }
        val alert = snoozeDialog.create()
        alert.show()
    }

    private fun getTimeDifferenceInMinutesSecondsFormat(timeDifference: Long): String {
        var timeDifference = timeDifference
        val minutesString = ""
        val hoursString = ""
        val timeDifferenceString = StringBuilder()

        timeDifference /= 1000
        val days = timeDifference.toInt() / (24 * 60 * 60)
        val hours = (timeDifference / (60 * 60)).toInt() - days * 24
        val minutes = (timeDifference / 60).toInt() - hours * 60


        if (days >= 1) {
            return if (days == 1) {
                "" + days + " day"
            } else {
                "" + days + " days"
            }
        }

        if (hours >= 1) {
            if (hours == 1) {
                timeDifferenceString.append("" + hours + " hour")
            } else {
                timeDifferenceString.append("" + hours + " hours")
            }
        }


        if (minutes == 1) {
            timeDifferenceString.append(" $minutes min")
        } else {
            timeDifferenceString.append(" $minutes mins")
        }

        timeDifferenceString.append(" remaining")

        return timeDifferenceString.toString()
    }


}
