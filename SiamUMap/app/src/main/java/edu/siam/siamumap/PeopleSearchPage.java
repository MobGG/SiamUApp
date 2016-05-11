package edu.siam.siamumap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import android.os.StrictMode;
import android.widget.TextView;

import org.ksoap2.*;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.transport.*;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.HashMap;

import siamumap.adaptor.FacultySpinnerAdapter;
import siamumap.adaptor.PeopleCustomAdapter;
import siamumap.dto.Faculty;
import siamumap.dto.People;


/**
 * Created by Mob on 27-Sep-15.
 */
public class PeopleSearchPage extends ActionBarActivity {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName;
    private static String soapAction;
    //criteria
    private String peopleName;
    private String spinnerID;
    //dto
    private FacultySpinnerAdapter facultySpinnerAdapter;
    private ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    private ArrayList<People> people = new ArrayList<People>();
    //android
    View criteria;
    EditText txtName;
    Spinner spinnerFaculty;
    Button btnSearch, btnClear;
    ListView listOfPeople;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_search_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appMethod.openPermisson();

        //binding android part to java
        criteria = (View) findViewById(R.id.criteria);
        txtName = (EditText) findViewById(R.id.txtName);
        spinnerFaculty = (Spinner) findViewById(R.id.spinnerMajor);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnClear = (Button) findViewById(R.id.btnClear);
        listOfPeople = (ListView) findViewById(R.id.listOfPeople);

        new getFacultySpinnerData().execute();
        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                spinnerID = ((TextView) v.findViewById(R.id.spinnerID)).getText().toString();
//                String spinnerValue = ((TextView) v.findViewById(R.id.spinnerValue)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                people = null;
                new getPeopleData().execute();
                criteria.setVisibility(View.GONE);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.setText("");
                spinnerFaculty.setSelection(0);
//                people = null;
            }
        });
        listOfPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View v, int position, long id) {
                String peopleId = ((TextView) v.findViewById(R.id.people_id)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), PeopleResultPage.class);
                intent.putExtra("peopleId", peopleId);
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

    private class getFacultySpinnerData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            methodName = "findFacultyByStatus";
            soapAction = "http://siamUMapService.org/findFacultyByStatus";
            facultySpinnerAdapter = null;

            progressDialog = appMethod.createProgressDialog(PeopleSearchPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลคณะทั้งหมด... ");
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

                Faculty faculty = new Faculty();
                faculty.setFacultyName("เลือกทั้งหมด");
                faculties.add(faculty);
                for (int i = 0; i < count; i++) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    faculty = new Faculty();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    faculty.setFacultyID(responseChild.getPropertyAsString("facultyID"));
                    faculty.setFacultyName(responseChild.getPropertyAsString("facultyName"));
                    faculty.setStatusID(responseChild.getPropertyAsString("statusID"));
                    faculties.add(faculty);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            facultySpinnerAdapter = new FacultySpinnerAdapter(getApplicationContext(), R.layout.custom_faculty_spinner, faculties);
            spinnerFaculty.setAdapter(facultySpinnerAdapter);
        }
    }

    private class getPeopleData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            methodName = "findPeopleByCriteria";
            soapAction = "http://siamUMapService.org/findPeopleByCriteria";
            peopleName = txtName.getText().toString();
            people = new ArrayList<People>();

            progressDialog = appMethod.createProgressDialog(PeopleSearchPage.this);
            progressDialog.setMessage("กำลังค้นหาข้อมูลบุคลากร... ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName("name");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(peopleName);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("facultyId");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(spinnerID);
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
                    final People person = new People();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    person.setPeopleID(responseChild.getPropertyAsString("userId"));
                    person.setPeopleName(responseChild.getPropertyAsString("name"));
                    person.setPeopleFaculty(responseChild.getPropertyAsString("facultyName"));
                    if (responseChild.hasProperty("picture")) {
                        person.setPeopleImage(responseChild.getPropertyAsString("picture"));
                    }
                    people.add(person);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            PeopleCustomAdapter customAdapter = new PeopleCustomAdapter(getApplicationContext(), people);
            listOfPeople.setAdapter(customAdapter);

            TextView empty = (TextView) findViewById(R.id.empty);
            listOfPeople.setEmptyView(empty);
        }
    }
}
