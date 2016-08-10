package edu.siam.siamumap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.siam.siamumap.MyDatePicker.DatePickerFragment;
import siamumap.adaptor.BuildingSpinnerAdapter;
import siamumap.adaptor.MissingItemCustomAdapter;
import siamumap.dto.Building;
import siamumap.dto.Post;

/**
 * Created by Mob on 27-Sep-15.
 */
public class MissingPage extends ActionBarActivity implements DatePickerFragment.TheListener {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName;
    private static String soapAction;

    private String title, spinnerID, strStartDate, strEndDate;

    BuildingSpinnerAdapter buildingSpinnerAdapter;
    DatePickerFragment startFragment;

    private ArrayList<Building> buildings = new ArrayList<Building>();
    private ArrayList<Post> posts = new ArrayList<Post>();

    View criteria;
    EditText txtTopic, txtStartDate, txtEndDate;
    Spinner spinnerBuilding;
    Button btnSearch, btnClear;
    ImageButton btnStart, btnEnd;
    ListView missingListView;
    FloatingActionButton btnAdd;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle missingList) {
        super.onCreate(missingList);
        setContentView(R.layout.missing_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appMethod.openPermisson();

        criteria = (View) findViewById(R.id.criteria);
        txtTopic = (EditText) findViewById(R.id.txtTopic);
        txtStartDate = (EditText) findViewById(R.id.startDate);
        txtEndDate = (EditText) findViewById(R.id.endDate);
        spinnerBuilding = (Spinner) findViewById(R.id.ddPlace);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnStart = (ImageButton) findViewById(R.id.btnStartDate);
        btnEnd = (ImageButton) findViewById(R.id.btnEndDate);
        missingListView = (ListView) findViewById(R.id.listOfMissing);
        btnAdd = (FloatingActionButton) findViewById(R.id.fabAddButton);

        criteria.setVisibility(View.GONE);

        new getBuildingSpinnerData().execute();

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                spinnerID = ((TextView) v.findViewById(R.id.buildingNo)).getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criteria.setVisibility(View.GONE);
                new getTopicData().execute();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTopic.setText("");
                spinnerBuilding.setSelection(0);

                txtStartDate.setText("");
                txtEndDate.setText("");
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent = Gotopage
                Intent intent = new Intent(getApplicationContext(), MissingCreatePage.class);
                startActivity(intent);
                finish();
            }
        });

        missingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View v, int position, long id) {
                String topicId = ((TextView) v.findViewById(R.id.post_id)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), MissingInfoPage.class);
                intent.putExtra("topicId", topicId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                criteria.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pickStartDate(View v) {
        startFragment = new DatePickerFragment();
        startFragment.setFlag(DatePickerFragment.startDate);
        startFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickEndDate(View v) {
        startFragment = new DatePickerFragment();
        startFragment.setFlag(DatePickerFragment.endDate);
        startFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void returnStartDate(String date) {
        Date startDate, endDate;
        startDate = convertStringToDate(date);
        endDate = convertStringToDate(strEndDate);

        if (endDate.before(startDate)) {
            strStartDate = date;
            txtStartDate.setText(date);
            strEndDate = date;
            txtEndDate.setText(date);
        } else {
            strStartDate = date;
            txtStartDate.setText(date);
        }
    }

    @Override
    public void returnEndDate(String date) {
        Date startDate, endDate;
        startDate = convertStringToDate(strStartDate);
        endDate = convertStringToDate(date);

        if (startDate.after(endDate)) {
            strStartDate = date;
            txtStartDate.setText(date);
            strEndDate = date;
            txtEndDate.setText(date);
        } else {
            strEndDate = date;
            txtEndDate.setText(date);
        }
    }

    private String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(currentDate);
        return  date;
    }

    private Date convertStringToDate(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date convertDate = sdf.parse(strDate);
            return convertDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class getBuildingSpinnerData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            methodName = "findAllBuilding";
            soapAction = "http://siamUMapService.org/findAllBuilding";
            buildingSpinnerAdapter = null;

            progressDialog = appMethod.createProgressDialog(MissingPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลอาคาร... ");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                int count = response.getPropertyCount();

                Building building = new Building();
                building.setBuildingDescription("เลือกทั้งหมด");
                buildings.add(building);
                for (int i = 0; i < count; i++) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    building = new Building();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    building.setBuildingNo(responseChild.getPropertyAsString("buildingNo"));
                    building.setBuildingDescription(responseChild.getPropertyAsString("description"));
                    buildings.add(building);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            buildingSpinnerAdapter = new BuildingSpinnerAdapter(getApplicationContext(), R.layout.custom_building_spinner, buildings);
            spinnerBuilding.setAdapter(buildingSpinnerAdapter);

            strStartDate = getCurrentDate();
            txtStartDate.setText(getCurrentDate());
            strEndDate = getCurrentDate();
            txtEndDate.setText(getCurrentDate());
            new getTopicData().execute();
        }
    }

    private class getTopicData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            methodName = "findTopicByCriteria";
            soapAction = "http://siamUMapService.org/findTopicByCriteria";
            title = txtTopic.getText().toString();
            posts = new ArrayList<Post>();

            progressDialog = appMethod.createProgressDialog(MissingPage.this);
            progressDialog.setMessage("กำลังค้นหาข้อมูลของหาย...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName("title");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(title);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("buildingNo");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(spinnerID);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("strStartDate");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(strStartDate);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("strEndDate");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(strEndDate);
            request.addProperty(propertyInfo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                int count = response.getPropertyCount();

                for (int i = 0; i < count; i++) {
                    final Post post = new Post();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    post.setPostID(responseChild.getPropertyAsString("topicNo"));
                    post.setPostTitle(responseChild.getPropertyAsString("title"));
                    post.setPostPlace(responseChild.getPropertyAsString("buildingNo"));
                    post.setPostDate(responseChild.getPropertyAsString("dateTime"));
                    if (responseChild.hasProperty("picture")) {
                        post.setPostImage(responseChild.getPropertyAsString("picture"));
                    }
                    posts.add(post);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void something) {
            MissingItemCustomAdapter missingItemCustomAdapter = new MissingItemCustomAdapter(getApplicationContext(), posts);
            missingListView.setAdapter(missingItemCustomAdapter);

            TextView empty = (TextView) findViewById(R.id.empty);
            missingListView.setEmptyView(empty);

            progressDialog.dismiss();
        }
    }
}
