package com.inspection.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.inspection.Utils.Utility;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    Bitmap bmp;
    ImageView passedImage = null;
    String photoUrl = "";
    Activity activity = null;
    //ProgressDialog progressDialog;

    public ImageLoader(Activity activity, ImageView passedImage, String photoUrl) {
        this.passedImage = passedImage;
        this.photoUrl = photoUrl;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Loading ...");
//            progressDialog.setIndeterminate(true);
//            progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub
        //Log.dMainActivity.TAG, "" + photoUrl);
        try {
            if (!photoUrl.contains("null")) {
                if (!Utility.imagesMap.containsKey(photoUrl) || Utility.imagesMap.get(photoUrl)==null) {
//                    URL url = new URL(photoUrl);
//                    val url = URL("" + context.getString(R.string.safetyCheckPhotosURL) + fileName)
//                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                    InputStream in = new URL(photoUrl).openStream();
////                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    bmp = BitmapFactory.decodeStream(in);
//                    URL url = new URL("https://www.gstatic.com/webp/gallery/4.sm.jpg");
//                    Log.v("COMAPRE: " ,""+photoUrl.equals("http://valueaddedonline.com/AccountInfoPicture/telematicspicture1996-ehlx.jpg"));
//                    URL url = new URL("http://valueaddedonline.com/AccountInfoPicture/telematicspicture1996-ehlx.jpg");
                    URL url = new URL(photoUrl.replace("www.",""));
                    bmp=BitmapFactory.decodeStream((InputStream)url.getContent());
                    Utility.imagesMap.put(photoUrl, bmp);
                } else {
                    //Log.dMainActivity.TAG, "Image Found");
                    bmp = Utility.imagesMap.get(photoUrl);
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            try {
//                bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.banner);
//            } catch (IllegalStateException ex) {
//                ex.printStackTrace();
//
//            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        //Log.dMainActivity.TAG, "heree******");

        if (result != null) {
            passedImage.setImageBitmap(result);
            FileOutputStream out = null;
            try {
                String filename=photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/ShopImage.png");
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/"+filename);
                out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            //Log.dMainActivity.TAG, "bmp is nulll");
        }
    }

}
