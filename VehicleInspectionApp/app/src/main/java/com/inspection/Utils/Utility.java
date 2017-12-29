package com.inspection.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.*;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;
import android.provider.ContactsContract.*;

import com.inspection.MainActivity;
import com.inspection.model.VehicleProfileModel;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private Activity activity = null;
    public static boolean DEBUG = true;
    public static final String SENDER_ID = "773352494082";
    public static ArrayList<VehicleProfileModel> VehiclePofileData;
    public static HashMap<String, Bitmap> imagesMap = new HashMap<>();

    public Utility(Activity activity) {
        this.activity = activity;
    }

    public static double roundValue(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void toast(String toastMessage, Context context) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static void cLog(Context mContext, String msg) {
        if (DEBUG == true) {
            //Log.e(mContext.getString(R.string.app_name), msg);
        }
    }



//    public static String postRequest(String url, List<NameValuePair> nameValuePairs) {
//        String request = "";
//        String result = null;
//        for (NameValuePair nData : nameValuePairs) {
//            // //Log.e("TAG","NAME::VALUE:->" + nData.getName() + "=="+
//            // nData.getValue());
//            request += nData.getName() + "=" + nData.getValue() + "&";
//        }
//
//        try {
//            request = request.substring(0, request.length() - 1);
//            String encryptedString = request.substring(0, request.length() - 1);
////			encryptedString = URLEncoder.encode(encryptedString, "UTF-8");
//            //Log.e("TAG", "request:: " + url + request);
//            //MyService.writefile("Webservice Request :::" + url+request);
//            String temp = url.concat(request);
//            temp = temp.replace("%", "");
//            //Log.i("TAG", "request:: " + temp);
////			String restUrl = URLEncoder.encode(url+request, "UTF-8");
////			HttpGet httppost = new HttpGet(restUrl);
//            String finalString = temp;
//            finalString = finalString.replace(' ', '+');
//            finalString = finalString.replace("[", "%5B");
//            finalString = finalString.replace("]", "%5D");
//            //Log.e("TAG", "request:: " + finalString);
//            HttpGet httppost = new HttpGet(finalString);
//            HttpParams httpParameters = new BasicHttpParams();
//            int timeoutConnection = 900000000;
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//            int timeoutSocket = 900000000;
//            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
//            BasicHttpResponse httpResponse = (BasicHttpResponse) httpClient.execute(httppost);
//            HttpEntity entity = httpResponse.getEntity();
//            if (entity != null) {
//                result = EntityUtils.toString(entity);
//                result = result.trim();
//            }
//            //Log.e("", "Responce:" + result);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Couldn't Login, Please try again";
//        }
//    }

    public static String postRequest(String url, ContentValues values) {
        String request = "";
        String result = null;
        for (String keyName: values.keySet()) {
            // //Log.e("TAG","NAME::VALUE:->" + nData.getName() + "=="+
            // nData.getValue());
            request += keyName + "=" + values.get(keyName) + "&";
        }

        try {
            request = request.substring(0, request.length() - 1);
            String encryptedString = request.substring(0, request.length() - 1);
//			encryptedString = URLEncoder.encode(encryptedString, "UTF-8");
            Log.e("TAG", "request:: " + url + request);
            //MyService.writefile("Webservice Request :::" + url+request);
            String temp = url.concat(request);
            temp = temp.replace("%", "");
            //Log.d(MainActivity.TAG, "request:: " + temp);
//			String restUrl = URLEncoder.encode(url+request, "UTF-8");
//			HttpGet httppost = new HttpGet(restUrl);
            String finalString = temp;
            finalString = finalString.replace(' ', '+');
            finalString = finalString.replace("[", "%5B");
            finalString = finalString.replace("]", "%5D");
            //Log.e("TAG", "request:: " + finalString);
            result = httpGet(finalString);
        } catch (Exception e) {
            e.printStackTrace();
            // Saeed Mostafa - 03092017 - result should be returned in case httpget failure [Start]
//             return null;
            result=null;
//            return result;
            // Saeed Mostafa - 03092017 - result should be returned in case httpget failure [End]
        }
        return result;
    }

//    public static String postRequestonly(String url) {
//        String request = "";
//        String result = null;
////		for (NameValuePair nData : nameValuePairs) {
//			// //Log.e("TAG","NAME::VALUE:->" + nData.getName() + "=="+
////			// nData.getValue());
////			request += nData.getName() + "=" + nData.getValue() + "&";
////		}
//        try {
////			String encryptedString = request.substring(0,request.length() - 1);
////			encryptedString = URLEncoder.encode(encryptedString, "UTF-8");
//            //Log.e("TAG", "request:: " + url + request);
//            //MyService.writefile("Webservice Request :::" + url);
//            String temp = url.concat(request);
//            temp = temp.replace("%", "");
//            //Log.i("TAG", "request:: " + temp);
////			String restUrl = URLEncoder.encode(url+request, "UTF-8");
////			HttpGet httppost = new HttpGet(restUrl);
//            String finalString = temp;
//            finalString = finalString.replace(' ', '+');
//            HttpGet httppost = new HttpGet(finalString);
//            HttpParams httpParameters = new BasicHttpParams();
//            int timeoutConnection = 900000000;
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//            int timeoutSocket = 900000000;
//            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
//            BasicHttpResponse httpResponse = (BasicHttpResponse) httpClient.execute(httppost);
//            HttpEntity entity = httpResponse.getEntity();
//            if (entity != null) {
//                result = EntityUtils.toString(entity);
//                result = result.trim();
//            }
//            //Log.e("", "Responce:" + result);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    //------------Show Messege Dialog
    public static final void showMessageDialog(Context contex, String title,
                                               String message) {
        if (message != null && message.trim().length() > 0) {
            Builder builder = new AlertDialog.Builder(contex);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }


    public static boolean validateString(String object) {
        boolean flag = false;
        if (object != null && object.equalsIgnoreCase("null") != true
                && object.trim().length() > 0) {
            flag = true;
        }
        return flag;
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;
        boolean flag = false;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            flag = true;
        }
        return flag;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean checkInternetConnection(Context context) {
//		 try {
//			 InetAddress.getByName("google.com").isReachable(2000);
//		    } catch (UnknownHostException e) {
//		    	e.printStackTrace();
//		        return false;
//		    } catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				 return false;
//			}
//		    return true;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            //Log.dMainActivity.TAG, "Internet Connection Not Present");
//	        Toast.makeText(context, "Internet Connection Not Present", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static long getdate(Context context, String date) {
        Calendar today = Calendar.getInstance();
        Calendar thatDay = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date d = null;
        try {
            d = formatter.parse(date);//catch exception
            thatDay = Calendar.getInstance();
            thatDay.setTime(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
        long days = (diff / (24 * 60 * 60 * 1000));
        return days;
    }

    public static void wakeup(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    public static void writefile1(String data) {
//	    	if(Enable){
        try {
//		    		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
//		    		String currentDateandTime = sdf.format(new Date());
            String timemili = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File f1 = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/matics.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
            out.write("\n" + data);
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //Log.e("", "Error in writing file");
        }
//	    	}
    }

    public static void writefile2(String data) {
//	    	if(Enable){
        try {
//		    		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
//		    		String currentDateandTime = sdf.format(new Date());
            String timemili = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File f1 = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/online.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
            out.write("\n" + data);
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //Log.e("", "Error in writing file");
        }
//	    	}
    }


    public static Typeface fonttypeface(Context mContext) {
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/gill_sans.ttf");
        return tf;
    }

    public static String getlatlong(String Add, Context mcon) {
        // String addressStr = "Ahmedabad,Gujarat,india";

        String Address = "";
        Geocoder geoCoder = new Geocoder(mcon, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(Add, 1);
            if (addresses.size() > 0) {
//				latitude = addresses.get(0).getLatitude();
//				longitude = addresses.get(0).getLongitude();
//				//Log.e("", "latitude in getlatlong method is :" + latitude);
//				//Log.e("", "longitude in getlatlong method is :" + longitude);
                Address = addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
            }
        } catch (IOException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            Address = "";
        }
        return Address;

    }

//    public static void addCalendarEvent(String title, String description, String dateString, int firstReminderType, String firstReminderDate, int secondReminderType, String secondReminderDate, String location) {
//
//
//        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//        Date requiredDate = null;
//        try {
//            requiredDate = yyyyMMdd.parse(dateString);
//
//            ContentResolver cr = BluetoothApp.context.getContentResolver();
//            ContentValues values = new ContentValues();
//            //Log.dMainActivity.TAG, "dateString=" + requiredDate.getTime());
//            values.put(CalendarContract.Events.DTSTART, requiredDate.getTime());
//            values.put(CalendarContract.Events.TITLE, title);
//            values.put(CalendarContract.Events.DESCRIPTION, description);
//
//            TimeZone timeZone = TimeZone.getDefault();
//            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
//
//            // default calendar
//            values.put(CalendarContract.Events.CALENDAR_ID, 1);
//
//            //values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=1");
//            //for one hour
//            values.put(CalendarContract.Events.DURATION, "+P2H");
//            values.put(CalendarContract.Events.EVENT_LOCATION, location);
//
//            values.put(CalendarContract.Events.HAS_ALARM, true);
//
//
//            // insert event to calendar
//            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
//
//            long eventID = Long.parseLong(uri.getLastPathSegment());
//
//            if (firstReminderDate != null && !firstReminderDate.isEmpty()) {
//                Date reminderDate1 = yyyyMMdd.parse(firstReminderDate);
//                long timeDifference = requiredDate.getTime() - reminderDate1.getTime();
//
//
//                // add 10 minute reminder for the event
//                ContentValues reminders = new ContentValues();
//                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
//                reminders.put(CalendarContract.Reminders.METHOD, firstReminderType);
//                reminders.put(CalendarContract.Reminders.MINUTES, timeDifference / 60000);
//                uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
//            }
//
//            if (secondReminderDate != null && !secondReminderDate.isEmpty()) {
//                Date reminderDate2 = yyyyMMdd.parse(secondReminderDate);
//                long timeDifference = requiredDate.getTime() - reminderDate2.getTime();
//
//
//                // add 10 minute reminder for the event
//                ContentValues reminders = new ContentValues();
//                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
//                reminders.put(CalendarContract.Reminders.METHOD, secondReminderType);
//                reminders.put(CalendarContract.Reminders.MINUTES, timeDifference / 60000);
//                uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
//            }
//
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return;
//        }
//    }

    public static void addContact(String contactName, String contactPhone, String contactAddress, String contactEmail, String imageURL) {
        //Log.dMainActivity.TAG, " Add contact started with values, contactName = " + contactName + " contactPhone = " + contactPhone + " imageURL = " + imageURL);
        if (!isPhoneNumberExisting(contactPhone)) {
            //Log.dMainActivity.TAG, "Phone doesn't exists");
            ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                    .build());

            // first and last names
            op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.StructuredName.GIVEN_NAME, contactName)
                            //.withValue(CommonDataKinds.StructuredName.FAMILY_NAME, "mohammed MOstafa")
                    .build());

            op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

            op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, contactEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());

            op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.StructuredPostal.DATA, contactAddress)
                    .withValue(CommonDataKinds.StructuredPostal.TYPE, CommonDataKinds.StructuredPostal.TYPE_WORK)
                    .build());

            if (!imageURL.isEmpty()) {
                Bitmap contactImage = getBitmapFromURL(imageURL);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (contactImage != null) {    // If an image is selected successfully
                    contactImage.compress(Bitmap.CompressFormat.PNG, 75, stream);

                    // Adding insert operation to operations list
                    // to insert Photo in the table ContactsContract.Data
                    op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                            .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                            .build());
                    //Log.dMainActivity.TAG, "image added");
                    try {
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

//            try {
//                ContentProviderResult[] results = BluetoothApp.context.getContentResolver().
//                        applyBatch(ContactsContract.AUTHORITY, op_list);
//                //Log.dMainActivity.TAG, "patch applied");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    public static boolean isPhoneNumberExisting(String phoneNumber) {
        boolean isExist = false;
        //Log.dMainActivity.TAG, "checking phone number: " + phoneNumber);
//        try {
//            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
//            String[] projection = new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
//            String selection = null;
//            String[] selectionArgs = null;
//            String sortOrder = ContactsContract.PhoneLookup.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//            ContentResolver cr = BluetoothApp.context.getContentResolver();
//            if (cr != null) {
//                //Log.dMainActivity.TAG, "preparing the cursor");
//                Cursor resultCur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
//                //Log.dMainActivity.TAG, "" + resultCur.getCount());
//                if (resultCur != null) {
//                    if (resultCur.getCount() > 0) {
//                        isExist = true;
//                    }
//                    resultCur.close();
//                }
//            }
//        } catch (Exception sfg) {
//            //Log.e("Error", "Error in loadContactRecord : " + sfg.toString());
//        }
        return isExist;
    }

    public static String httpGet(String urlString) {
        StringBuffer result = new StringBuffer();
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);

            urlConnection = (HttpURLConnection) url
                    .openConnection();


            //Log.dMainActivity.TAG, ""+url.toString());

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result.append(current);
            }

        } catch (IOException e) {
            //Log.dMainActivity.TAG, e.getMessage());
            e.printStackTrace();
            return "HTTP_BAD:" + "Connection not available, Please try again.";
        } catch (Exception e) {
            //Log.dMainActivity.TAG, e.getMessage());
            return "HTTP_BAD:" + "Connection not available, Please try again.";
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); //If you want further info on failure...
            }
        }
        //Log.dMainActivity.TAG, ""+result.toString());
        return result.toString();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static boolean isAirplaneModeOn(Context context) {

        return android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON, 0) == 1;

    }

    public static void showAlertDialog(Activity activity, String title, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
    }


//    public static VinResolvedObject resolveVin(String VIN) {
//        Utility.VehiclePofileData = new ArrayList<VehicleProfileModel>();
//        String result = null;
//
//        //Log.dMainActivity.TAG, "with VIN value = " + VIN);
//
//        String getServerPath = "http://api.edmunds.com/v1/api/toolsrepository/vindecoder?";
//        try {
//            ContentValues values=new ContentValues();
//
//            if (VIN == null || VIN.equalsIgnoreCase("null")
//                    || VIN.equalsIgnoreCase("")) {
//                values.put("vin", String
//                        .valueOf("NODATA"));
//            } else {
//                values.put("vin", String
//                        .valueOf(VIN));
//            }
//            values.put("fmt", String
//                    .valueOf("json"));
//
//            values.put("api_key", String
//                    .valueOf("e7yfyg8y7w9yr7rs3a2qr4ba"));
//
//            result = Utility.postRequest(getServerPath, values);
//            // ------------------------------------Parsing
//            if (result.toString().contains("styleHolder")) {
//                JSONObject jObject = new JSONObject(result.toString());
//
//                JSONArray j1 = jObject.getJSONArray("styleHolder");
//                JSONObject j2 = j1.getJSONObject(0);
//
//                return (new VinResolvedObject(VIN, j2.getString("year"), j2.getString("makeName"), j2.getString("modelName")));
//
//
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }


}
