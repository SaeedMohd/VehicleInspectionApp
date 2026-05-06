package com.inspection;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.WindowManager;

import androidx.multidex.MultiDexApplication;

import com.bugfender.sdk.Bugfender;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.inspection.Utils.Utility;


public class CustomApplication extends MultiDexApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
//        Bugfender.init(this, "c3n0CKCzaxaYJ5oAHXzTLc6tL0SFsHNz", BuildConfig.DEBUG, true);//saeed account
         if (BuildConfig.FLAVOR.equals("uat")){
             Bugfender.init(this, "TRG6fGVD69B5cBt7ZrUlW3QXlHdkjScb",BuildConfig.DEBUG, true);
        } else if (BuildConfig.FLAVOR.equals("production")){
             Bugfender.init(this, "00OCHvUa3uXcanjMPjqp5FMjWUl85ScI",BuildConfig.DEBUG, true);
        }



//        Bugfender.setNetworkLoggingEnabled(true);
        Bugfender.enableCrashReporting();

//        Bugfender.enableUIEventLogging(this);
//        Bugfender.enableLogcatLogging();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                // Log the exception to Crashlytics
//                FirebaseCrashlytics.getInstance().recordException(throwable);
//
//                // Start the ErrorActivity
////                Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                getApplicationContext().startActivity(intent);
//
//                // Terminate the app gracefully
//                System.exit(1);
//            }
//        });
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                // Log the exception to Crashlytics
//                FirebaseCrashlytics.getInstance().recordException(throwable);
//
//                // Try to allow Crashlytics some time to send the report
//                try {
//                    Thread.sleep(2000); // Sleep for 2 seconds to give Crashlytics time to send
//                    Process.killProcess(Process.myPid());
//                    System.exit(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // Call the default handler to terminate the app gracefully
////                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);
//            }
//        });

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                // Call your custom function here
////                handleCrash(throwable);
//                // You can also log the crash details with Firebase Crashlytics
//                FirebaseCrashlytics.getInstance().recordException(throwable);
//                try {
//                    Thread.sleep(2000); // Sleep for 2 seconds to ensure Crashlytics logs the crash
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // If needed, exit the app or restart it gracefully
//                Process.killProcess(Process.myPid());
//                System.exit(1);
//            }
//        });

    }

    private void handleCrash(Throwable throwable) {
        // Perform any action you need before the app crashes
        // e.g., log custom data, show a custom error screen, or save crash data
        Log.e("CrashHandler", "App is about to crash!", throwable);
        FirebaseCrashlytics.getInstance().setCustomKey("TEST", "TEST");
        FirebaseCrashlytics.getInstance().recordException(throwable);

//        new Handler(Looper.getMainLooper()).post(() -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//            builder.setTitle("Error")
//                    .setMessage("An unexpected error occurred. The app will close.")
//                    .setCancelable(false)
//                    .setPositiveButton("OK", (dialog, which) -> {
//                        System.exit(1); // Close the app when the user acknowledges the message
//                    });
//
//            AlertDialog dialog = builder.create();
//            // Ensure the dialog can be shown from the application context
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//            dialog.show();
//        });

        Process.killProcess(Process.myPid());
        System.exit(1);
    }

}
