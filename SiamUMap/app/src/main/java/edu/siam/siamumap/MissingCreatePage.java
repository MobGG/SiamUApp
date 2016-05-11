package edu.siam.siamumap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import siamumap.adaptor.BuildingSpinnerAdapter;
import siamumap.dto.Building;

/**
 * Created by Mob on 27-Sep-15.
 */
public class MissingCreatePage extends ActionBarActivity {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName;
    private static String soapAction;

    private static final int pickingImage = 1;
    String title, description, spinnerID, dateTime, userId;
    Bitmap resizeImage;
    boolean status;

    private BuildingSpinnerAdapter buildingSpinnerAdapter;
    private ArrayList<Building> buildings = new ArrayList<Building>();

    private ImageView image;
    private TextView pathFile;
    EditText txtTopic, txtDescription, txtFounderId;
    Spinner ddPlace;
    Button selectImageBtn;
    FloatingActionButton createPostBtn;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missing_create_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appMethod.openPermisson();

        txtTopic = (EditText) findViewById(R.id.txtTopic);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtFounderId = (EditText) findViewById(R.id.txtFounderId);
        ddPlace = (Spinner) findViewById(R.id.ddPlace);
        image = (ImageView) findViewById(R.id.missingImage);
        pathFile = (TextView) findViewById(R.id.imagePath);
        selectImageBtn = (Button) findViewById(R.id.addImageBtn);
        createPostBtn = (FloatingActionButton) findViewById(R.id.fabCreateButton);

        new getBuildingSpinnerData().execute();

        ddPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                spinnerID = ((TextView) v.findViewById(R.id.buildingNo)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Please select app for choose your picture"), pickingImage);
            }
        });

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = false;
                final String title = txtTopic.getText().toString();
                final String userId = txtFounderId.getText().toString();
                if (title == null || title.isEmpty()) {
                    Toast.makeText(MissingCreatePage.this, "กรุณาตั้งชื่อกระทู้",
                            Toast.LENGTH_LONG).show();
                    valid = false;
                } else {
                    valid = true;
                }
                if (userId == null || userId.isEmpty()) {
                    Toast.makeText(MissingCreatePage.this, "กรุณากรอกรหัสประจำตัวของท่าน",
                            Toast.LENGTH_LONG).show();
                    valid = false;
                } else {
                    valid = true;
                }
                if (valid) {
                    new createTopic().execute();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MissingPage.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case pickingImage:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = returnedIntent.getData();
                    String message = "URI: " + imageUri + "\n";

                    String imagePath = findPath(imageUri);
                    message += "Path:" + imagePath;

                    File imageFile = new File(imagePath);
                    resizeImage = decodeAndResize(imageFile);
                    image.setImageBitmap(resizeImage);
                    pathFile.setText(message);
                }
        }
    }

    private String findPath(Uri uri) {
        String imagePath;

        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            imagePath = cursor.getString(columnIndex);
        } else {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private Bitmap decodeAndResize(File pathFile) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = 2;// default size/2
            FileInputStream fileInputStream = new FileInputStream(pathFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream, null, option);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private class getBuildingSpinnerData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            methodName = "findAllBuilding";
            soapAction = "http://siamUMapService.org/findAllBuilding";
            buildingSpinnerAdapter = null;

            progressDialog = appMethod.createProgressDialog(MissingCreatePage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลอาคารทั้งหมด... ");
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
                for (int i = 0; i < count; i++) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    final Building building = new Building();
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
            ddPlace.setAdapter(buildingSpinnerAdapter);
        }
    }

    private class createTopic extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;
        String imageEncoded;

        @Override
        protected void onPreExecute() {
            methodName = "createTopic";
            soapAction = "http://siamUMapService.org/createTopic";

            progressDialog = appMethod.createProgressDialog(MissingCreatePage.this);
            progressDialog.setMessage("ระบบกำลังทำการประมวลผล... ");
            progressDialog.show();

            status = false;
            title = txtTopic.getText().toString();
            description = txtDescription.getText().toString();
            userId = txtFounderId.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateTime = sdf.format(currentDate);

            if (resizeImage != null) {
                Bitmap immagex = resizeImage;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            }

            SoapObject request = new SoapObject(namespace, methodName);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName("title");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(title);
            request.addProperty(propertyInfo);

            if (!description.isEmpty()) {
                propertyInfo = new PropertyInfo();
                propertyInfo.setName("description");
                propertyInfo.setType(String.class);
                propertyInfo.setValue(description);
                request.addProperty(propertyInfo);
            }

            if (imageEncoded != null) {
                propertyInfo = new PropertyInfo();
                propertyInfo.setName("picture");
                propertyInfo.setType(String.class);
                propertyInfo.setValue(imageEncoded);
                request.addProperty(propertyInfo);
            }

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("dateTime");
            propertyInfo.setType(Date.class);
            propertyInfo.setValue(dateTime);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("userId");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(userId);
            request.addProperty(propertyInfo);

            propertyInfo = new PropertyInfo();
            propertyInfo.setName("buildingNo");
            propertyInfo.setType(Integer.class);
            propertyInfo.setValue(spinnerID);
            request.addProperty(propertyInfo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            new MarshalDate().register(soapEnvelope);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                status = Boolean.valueOf(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            if (!status) {
                Toast.makeText(MissingCreatePage.this, "รหัสประจำตัวนี้ยังไม่ถูกเปิดใช้งาน", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MissingCreatePage.this, "สร้างกระทู้แล้ว", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MissingPage.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
