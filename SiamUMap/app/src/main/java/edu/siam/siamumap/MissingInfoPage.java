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

import siamumap.dto.Post;

/**
 * Created by Mob on 27-Sep-15.
 */
public class MissingInfoPage extends ActionBarActivity {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName = "findOneTopic";
    private static String soapAction = "http://siamUMapService.org/findOneTopic";

    private String postId;
    private Post post = new Post();

    ImageView missingImage;
    TextView foundLocationView, descriptionView, foundDateView;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missing_info_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appMethod.openPermisson();

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("topicId");

        missingImage = (ImageView) findViewById(R.id.missingImage);
        foundLocationView = (TextView) findViewById(R.id.foundLocationView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        foundDateView = (TextView) findViewById(R.id.foundDateView);

        new getPostById().execute();
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

    private class getPostById extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = appMethod.createProgressDialog(MissingInfoPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลของหาย... ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName("topicId");
            propertyInfo.setType(String.class);
            propertyInfo.setValue(postId);
            request.addProperty(propertyInfo);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);// aht = androidHttpTransport
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                post.setPostTitle(response.getPropertyAsString("title"));
                post.setPostPlace(response.getPropertyAsString("buildingNo"));
                if (response.hasProperty("description")) {
                    post.setPostDescription(response.getPropertyAsString("description"));
                }
                post.setPostDate(response.getPropertyAsString("dateTime") + "Z");
                if (response.hasProperty("picture")) {
                    post.setPostImage(response.getPropertyAsString("picture"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            AppMethod appMethod = new AppMethod();

            getSupportActionBar().setTitle(post.getPostTitle());
            foundLocationView.setText("อาคาร " + String.valueOf(post.getPostPlace()));
            descriptionView.setText(post.getPostDescription());
            foundDateView.setText(appMethod.convertDate(post.getPostDate()));
            if (post.getPostImage() != null) {
                byte[] decodedString = Base64.decode(post.getPostImage(), Base64.DEFAULT);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                missingImage.setImageBitmap(bitmap);

            } else {
                missingImage.setImageResource(R.drawable.no_picture);
            }
        }
    }
}
