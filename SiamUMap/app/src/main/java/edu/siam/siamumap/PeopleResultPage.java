package edu.siam.siamumap;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import siamumap.dto.People;

/**
 * Created by Mob on 27-Sep-15.
 */
public class PeopleResultPage extends ActionBarActivity {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName = "findOnePeople";
    private static String soapAction = "http://siamUMapService.org/findOnePeople";

    private String peopleId;
    private People people = new People();

    ImageView imageView;
    TextView nameView, facultyView, departmentView, buildingView, roomView, telView, emailView;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_result_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appMethod.openPermisson();

        Bundle bundle = getIntent().getExtras();
        peopleId = bundle.getString("peopleId");

        imageView = (ImageView) findViewById(R.id.imageView);
        nameView = (TextView) findViewById(R.id.nameView);
        facultyView = (TextView) findViewById(R.id.facultyView);
        departmentView = (TextView) findViewById(R.id.departmentView);
        buildingView = (TextView) findViewById(R.id.buildingView);
        roomView = (TextView) findViewById(R.id.roomView);
        telView = (TextView) findViewById(R.id.telView);
        emailView = (TextView) findViewById(R.id.emailView);

        new getPeopleById().execute();
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

    private class getPeopleById extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = appMethod.createProgressDialog(PeopleResultPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลบุคลากร... ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName("userId");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(peopleId);
            request.addProperty(propertyInfo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                people.setPeopleName(response.getPropertyAsString("name"));
                people.setPeopleFaculty(response.getPropertyAsString("facultyName"));
                people.setPeopleDepartment(response.getPropertyAsString("departmentName"));
                if (response.hasProperty("picture")) {
                    people.setPeopleImage(response.getPropertyAsString("picture"));
                }
                if (response.hasProperty("buildingNo")) {
                    people.setPeopleBuilding(response.getPropertyAsString("buildingNo"));
                }
                if (response.hasProperty("roomNo")) {
                    people.setPeopleRoom(response.getPropertyAsString("roomNo"));
                }
                if (response.hasProperty("telNo")) {
                    people.setPeopleTel(response.getPropertyAsString("telNo"));
                }
                if (response.hasProperty("email")) {
                    people.setPeopleEmail(response.getPropertyAsString("email"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();

            nameView.setText(people.getPeopleName());
            facultyView.setText(people.getPeopleFaculty());
            if (people.getPeopleDepartment() == null) {
                departmentView.setText(people.getPeopleFaculty());
            } else {
                departmentView.setText(people.getPeopleDepartment());
            }
            if (people.getPeopleImage() != null) {
                byte[] decodedString = Base64.decode(people.getPeopleImage(), Base64.DEFAULT);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.no_picture);
            }
            if (people.getPeopleBuilding() == null || people.getPeopleBuilding().isEmpty()) {
                buildingView.setText("-");
            } else {
                buildingView.setText(people.getPeopleBuilding());
            }
            if (people.getPeopleRoom() == null || people.getPeopleRoom().isEmpty()) {
                roomView.setText("ไม่มีห้องพัก");
            } else {
                roomView.setText(people.getPeopleRoom());
            }
            if (people.getPeopleTel() == null || people.getPeopleTel().isEmpty()) {
                telView.setText("-");
            } else {
                telView.setText(people.getPeopleTel());
            }
            if (people.getPeopleEmail() == null || people.getPeopleEmail().isEmpty()) {
                emailView.setText("-");
            } else {
                emailView.setText(people.getPeopleEmail());
            }
        }
    }
}
