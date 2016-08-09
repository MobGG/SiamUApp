package edu.siam.siamumap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mob on 24-Dec-15.
 */
public class AppMethod {

    public void openPermisson() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public String setWebserviceURL() {
        //  connection always close when use 3bb ip
//        String webserviceUrl = "http://192.168.43.80:8254/Service.asmx"; // all code work normally by share 3bb ip to wifi
//        String webserviceUrl = "http://10.255.20.219:50129/Service.asmx";
        String webserviceUrl = "http://192.168.137.1:50667/Service.asmx";
        return webserviceUrl;
    }

    public ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int sampleSize = 1;
        if(height > reqHeight || width > reqWidth) {
            if(width > height)
                sampleSize = Math.round((float) height / (float) reqHeight);
            else
                sampleSize = Math.round((float) width / (float) reqWidth);
        }
        return sampleSize;
    }

    public String convertDate(String date) {
        String day, month, time;
        int year;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date d = format.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(d);
            day = new SimpleDateFormat("dd").format(d);
            month = new SimpleDateFormat("MM").format(d);
            year = c.get(Calendar.YEAR) + 543;
            time = new SimpleDateFormat("HH:mm:ss").format(d);
            String thaiDate = day + "/" + month + "/" + year + " " + time;
            return thaiDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // check connect or not
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static void checkConnectivityStatus(final Context context) {
        int conn = AppMethod.getConnectivityStatus(context);
        if (conn == AppMethod.TYPE_NOT_CONNECTED) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Application Siam U App ต้องการใช้การเชื่อมต่อ ท่านต้องการเปิดการเชื่อมต่อหรือไม่?");
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }


    public static void checkLocationProvider(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isNetwork && !isGPS) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("ท่านต้องการเปิดใช้งาน ระบบติดตามตำแหน่งหรือไม่?");
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }

}
