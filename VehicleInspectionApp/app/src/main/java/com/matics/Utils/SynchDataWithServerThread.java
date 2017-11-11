package com.matics.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.matics.Bluetooth.ResponceStatus;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.AccelerationModel;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SynchDataWithServerThread extends Thread {

    Context context;
    DataBaseHelper dbHelper;
    String dataSynchFilePath = Environment.getExternalStorageDirectory().getPath() + "/Matics/";
    String dataSynchFileName = "online.txt";
    public static boolean isRunning = false;
    private static SynchDataWithServerThread synchDataWithServerThread;
    String VehicalIN;

    private SynchDataWithServerThread(Context context) {


    }

    public void startSynchingThread(Context context) {
        this.context = context;
        if (dbHelper == null) {
            dbHelper = new DataBaseHelper(context);
        }


            //Log.dMainActivity.TAG, "Not running, let's make it start");
            try {
                start();
            }catch(IllegalStateException exp){
                //Log.dMainActivity.TAG,"Already started");
            }catch(IllegalThreadStateException exp){
                //Log.dMainActivity.TAG,"Already started");
            }

    }


    public static void stopSynchingThread(){
        isRunning=false;
        if(synchDataWithServerThread!=null) {
            synchDataWithServerThread.interrupt();
            synchDataWithServerThread = null;
        }

    }


    public static SynchDataWithServerThread getInstance(Context context) {

        if (synchDataWithServerThread == null) {
            synchDataWithServerThread = new SynchDataWithServerThread(context);
        }
        return synchDataWithServerThread;
    }


    public void run() {
        //Log.dMainActivity.TAG, "isRunning value ="+isRunning);
        isRunning=true;
        while (isRunning) {
            //Log.dMainActivity.TAG, "will start to synch data with server. First checking if there is connectivity and there are readings in the DB or not");
            if (Utility.checkInternetConnection(context)
                    && dbHelper.getReadingsCount() > 0) {
                ArrayList<String> readingsStrings = ReadingsManager.getReadingsForServerSynch(context);
                writeDataSynchFile(readingsStrings);
                ResponceStatus dataSynchResponce = uploadDataSynchFile();
                //if (dataSynchResponce.getResCode()==200 && dataSynchResponce.getResultJson().contains("\"IsSuccess\":true")){
                //Log.dMainActivity.TAG, "Data sent to server successfully. Clearing the data from the DB and removing data synch file");
                dbHelper.clearReadingsTable();
                deleteDataSynchFile();

            }


            if (Utility.checkInternetConnection(context)
                    && dbHelper.getAccelerationReadingsCount() > 0) {
                ArrayList<AccelerationModel> accelerationModelArrayList = dbHelper.getAccelerations();
                String accelerationJsonList = new Gson().toJson(accelerationModelArrayList);
                //Log.dMainActivity.TAG, accelerationJsonList);
                new sendAccelerationData().execute(accelerationJsonList);
                dbHelper.clearAccelerationTable();
                deleteDataSynchFile();

            }


            //Log.dMainActivity.TAG, "will sleep now for 20 seconds then check again.");
            try {
                sleep(3 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Log.dMainActivity.TAG, "Sleep thread interrupted with exception: " + e.getLocalizedMessage());
            }
            //Log.dMainActivity.TAG, "sleep completed, waking up now to check.");
        }

        //Log.dMainActivity.TAG, "Thread should be stopped by now");
        dbHelper.close();
        interrupt();

    }

    private void deleteDataSynchFile() {
        File dataSynchFile = new File(dataSynchFilePath + dataSynchFileName);
        dataSynchFile.delete();
    }

    public void writeDataSynchFile(ArrayList<String> readingsStrings) {
        try {
            String data_synch_url = context.getString(R.string.data_synch_url);
            File dataSynchFile = new File(dataSynchFilePath + dataSynchFileName);
            BufferedWriter out = new BufferedWriter(new FileWriter(dataSynchFile, true));
            for (String data : readingsStrings) {
                //Log.dMainActivity.TAG, data_synch_url + data);
                out.write("\n" + data_synch_url + data);
            }
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //Log.e("", "Error in writing file");
        }
    }

    private ResponceStatus uploadDataSynchFile() {

        int serverResponseCode = 0;
        ResponceStatus resStatus = new ResponceStatus();
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(dataSynchFilePath + dataSynchFileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(
                    sourceFile);
            URL url = new URL(
                    "http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname=" + dataSynchFileName);

            // URL url = new
            // URL("http://192.168.1.21:9991/JetCom.svc/UploadDocument?fname="+Name);
            //Log.e("",
//                    "post is : http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname="
//                            + dataSynchFileName);

            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("uploaded_file", dataSynchFileName);
            dos = new DataOutputStream(conn.getOutputStream());
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            serverResponseCode = conn.getResponseCode();
            resStatus.setResCode(serverResponseCode);
            resStatus.setResponseStatus(conn.getResponseMessage());

            //Log.e("uploadFile", "HTTP Response is : Server" + conn.getResponseMessage() + ": " + serverResponseCode);
            if (serverResponseCode == 200) {
                InputStream in = conn.getInputStream();
                byte data[] = new byte[1024];
                int counter = -1;
                String jsonString = "";
                while ((counter = in.read(data)) != -1) {
                    jsonString += new String(data, 0, counter);
                }
                //Log.dMainActivity.TAG, "HTTP Response is JSON String: "
//                        + jsonString);

                resStatus.setResultJson(jsonString);
                //Log.dMainActivity.TAG, "resStatus = " + resStatus);
            }
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            //Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Upload file to server",
//                    "Exception : " + e.getMessage(), e);
        }
        return resStatus;

    }

    public class sendAccelerationData extends AsyncTask<String, Integer, String> {

        private String ErrorDetail, status;
        // private final LoginData loginData = new LoginData();

        public sendAccelerationData() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return sendAccelerationData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

        /**
         * routine use to get-authentication for request credential
         */

        private String sendAccelerationData(String accelerationJsonList) {

            String result = null;
            //String getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/SendAccelerationInfo";
            String getServerPath = null;
            try {
                //getServerPath = "http://185.28.22.2/WebServices/jsonMessage.php?jsonMessage="+ URLEncoder.encode(accelerationJsonList, "UTF-8");
                getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/SendAccelerationInfo?jsonMessage=" + URLEncoder.encode(accelerationJsonList, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {


                result = Utility.httpGet(getServerPath);
                //Log.dMainActivity.TAG, "" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onCancelled() {
            super.onCancelled();
            // if(mProgressDialog != null && mProgressDialog.isShowing())
            // mProgressDialog.dismiss();
        }
    }

}
