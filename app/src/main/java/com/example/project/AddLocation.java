package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddLocation extends Activity {

    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        final LocationDBHelper locationDBHelper = new LocationDBHelper(this);

        Button addLocation = (Button)findViewById(R.id.add_btn);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText get_lat = (EditText)findViewById(R.id.insert_lat_edit);
                latitude = Double.parseDouble(get_lat.getText().toString());

                EditText get_long = (EditText)findViewById(R.id.insert_long_edit);
                longitude = Double.parseDouble(get_long.getText().toString());

                locationDBHelper.addNewLocation(latitude, longitude);

                finish();
            }
        });
    }
}
