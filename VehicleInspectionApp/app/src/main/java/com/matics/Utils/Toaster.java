package com.matics.Utils;

import android.app.Activity;
import android.widget.Toast;


public class Toaster {

    public static void shortToast(Activity context, String text) {
        try {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void longToast(Activity context, String text) {
        try {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
