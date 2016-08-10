package edu.siam.siamumap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
    AppMethod appMethod = new AppMethod();

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appMethod.checkConnectivityStatus(this);

        Button mapBtn = (Button)findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent = Gotopage
                Intent intent = new Intent(getApplicationContext(), MapPage.class);
                startActivity(intent);
            }
        });
        Button pplBtn = (Button)findViewById(R.id.peopleButton);
        pplBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PeopleSearchPage.class);
                startActivity(intent);
                //close this page
//                finish();
            }
        });
        Button missingBtn = (Button)findViewById(R.id.missingButton);
        missingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MissingPage.class);
                startActivity(intent);
            }
        });
    }
}
