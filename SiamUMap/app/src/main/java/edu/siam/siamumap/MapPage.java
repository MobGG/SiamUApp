package edu.siam.siamumap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import siamumap.dto.Building;

/**
 * Created by Mob on 27-Sep-15.
 */
public class MapPage extends AppCompatActivity implements GoogleMap.InfoWindowAdapter {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName = "findAllBuilding";
    private static String soapAction = "http://siamUMapService.org/findAllBuilding";

    private ArrayList<Building> buildings = new ArrayList<Building>();


    GoogleMap mapFragment;
    Marker marker, mMarker;

    LocationManager lm;
    double lat, lng;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appMethod.openPermisson();


        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                appMethod.checkLocationProvider(MapPage.this);
            }
            protected Void doInBackground(Void... unused) {
                return null;
            }
            protected void onPostExecute(Void unused) {
            }
        }.execute();


        mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.siamU_Map)).getMap();
        new getAllBuildingData().execute();
        mapFragment.setInfoWindowAdapter(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        String markerTitle = marker.getTitle();
        LayoutInflater inflater = getLayoutInflater();
        View infoWindow = inflater.inflate(R.layout.map_custom_info_window, null);

        LinearLayout layout = (LinearLayout) infoWindow.findViewById(R.id.customLayout);
        layout.setBackgroundResource(R.drawable.custom_info_window);
        if (markerTitle.equals("คุณอยู่ที่นี่")) {
            return null;
        } else {
            for (int i = 0; i < buildings.size(); i++) {
                if (markerTitle.matches(buildings.get(i).getBuildingDescription())) {
                    byte[] decodedString = Base64.decode(buildings.get(i).getBuildingPicture(), Base64.DEFAULT);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                    ImageView buildingImage = (ImageView) infoWindow.findViewById(R.id.buildingImage);
                    buildingImage.setImageBitmap(bitmap);

                    TextView buildingNo = (TextView) infoWindow.findViewById(R.id.buildingNo);
                    buildingNo.setText("อาคาร " + String.valueOf(buildings.get(i).getBuildingNo()));

                    TextView buildingDescription = (TextView) infoWindow.findViewById(R.id.buildingDescription);
                    buildingDescription.setText(buildings.get(i).getBuildingDescription());

                    TextView buildingFloor = (TextView) infoWindow.findViewById(R.id.buildingFloor);
                    buildingFloor.setText("จำนวนชั้น " + String.valueOf(buildings.get(i).getBuildingFloor()) + " ชั้น");
                    break;
                }
            }
            return infoWindow;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private class getAllBuildingData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = appMethod.createProgressDialog(MapPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลอาคาร... ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Call webservice for get data and fill in spinner
            SoapObject request = new SoapObject(namespace, methodName);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);
            //Old = androidHttpTransport >>> New = HttpTransportSE
            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                int count = response.getPropertyCount();
                for (int i = 0; i < count; i++) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    final Building building = new Building();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    building.setBuildingNo(responseChild.getPropertyAsString("buildingNo"));
                    building.setBuildingDescription(responseChild.getPropertyAsString("description"));
                    if (responseChild.hasProperty("picture")) {
                        building.setBuildingPicture(responseChild.getPropertyAsString("picture"));
                    }
                    building.setBuildingFloor(responseChild.getPropertyAsString("floor"));
                    building.setLat(Double.parseDouble(responseChild.getPropertyAsString("latitude")));
                    building.setLng(Double.parseDouble(responseChild.getPropertyAsString("longitude")));
                    buildings.add(building);
//                    int c = (int) ((100f / count) * (i + 1));
//                    publishProgress(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            if (mapFragment != null) {
                for (int i = 0; i < buildings.size(); i++) {
                    LatLng position = new LatLng(buildings.get(i).getLat(), buildings.get(i).getLng());
                    marker = mapFragment.addMarker(
                            new MarkerOptions()
                                    .position(position)
                                    .title(buildings.get(i).getBuildingDescription())
                    );
                }
            }

        }
    }

    LocationListener listener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            LatLng coordinate = new LatLng(loc.getLatitude(), loc.getLongitude());
            lat = loc.getLatitude();
            lng = loc.getLongitude();

            if (mMarker != null) {mMarker.remove();}

            mMarker = mapFragment.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("คุณอยู่ที่นี่")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18));
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    public void onResume() {
        super.onResume();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isNetwork) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, listener);
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                lat = loc.getLatitude();
                lng = loc.getLongitude();
            }
        }

        if (isGPS) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, listener);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                lat = loc.getLatitude();
                lng = loc.getLongitude();
            }
        }
    }

    public void onPause() {
        super.onPause();
        lm.removeUpdates(listener);
    }
}
