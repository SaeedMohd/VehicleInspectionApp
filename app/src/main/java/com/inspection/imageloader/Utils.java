package com.inspection.imageloader;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.inspection.R;
import com.inspection.model.UserAccountModel;
import com.inspection.Utils.ApplicationPrefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Utils {
	public static int CopyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}


	public static String getStringOrEmptyForNull(String requiredString) {
		if (requiredString != null) {
			return requiredString;
		} else {
			return "";
		}

	}


	public static void setShopImage(Activity activity, ImageView imageView) {

		UserAccountModel userAccountModel = ApplicationPrefs.getInstance(activity).getUserAccountPref();
//		if  {

		if (ApplicationPrefs.getInstance(activity).getIsLoggedInPref() && userAccountModel.getAccountId() > 0 && (ActivityCompat.checkSelfPermission(activity,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && !userAccountModel.getPhotoUrl().equals("")) {
			String filename=userAccountModel.getPhotoUrl().substring(userAccountModel.getPhotoUrl().lastIndexOf("/")+1);
//			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/ShopImage.png");
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/"+filename);
			if(file.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Matics/ShopImage.png", options);
				Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Matics/"+filename, options);
				imageView.setImageBitmap(bitmap);
			} else {
				try {
					new ImageLoader(activity, imageView, userAccountModel.getPhotoUrl()).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.banner);
			imageView.setImageBitmap(icon);
		}
	}


	public static void setShopImageBackground(Activity activity, ImageView imageView) {

		UserAccountModel userAccountModel = ApplicationPrefs.getInstance(activity).getUserAccountPref();
		if (ApplicationPrefs.getInstance(activity).getIsLoggedInPref() && userAccountModel.getAccountId() > 0 && !userAccountModel.getPhotoUrl().equals("")) {
			String filename=userAccountModel.getPhotoUrl().substring(userAccountModel.getPhotoUrl().lastIndexOf("/")+1);
//		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/ShopImage.png");
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/"+filename);
			if (file.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Matics/ShopImage.png", options);
				Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Matics/"+filename, options);
				imageView.setBackground(new BitmapDrawable(bitmap));
			}
		}
	}

}
