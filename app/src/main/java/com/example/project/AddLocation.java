package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddLocation extends Activity {

    public int streetNumber = 0;
    public String streetName;
    public String city;
    public String province;
    public String nameOfLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        setTitle("Add Location");

        final LocationDBHelper locationDBHelper = new LocationDBHelper(this);

        final EditText getStreetNum = (EditText)findViewById(R.id.insert_street_num);
        final EditText getStreetName = (EditText)findViewById(R.id.insert_street_name);
        final EditText getCity = (EditText)findViewById(R.id.insert_city);
        final EditText getProvince = (EditText)findViewById(R.id.insert_province);
        final EditText getNameOfLocation = (EditText)findViewById(R.id.insert_name_address);

        Button addLocation = (Button)findViewById(R.id.add_btn);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddLocation.this, MapsActivity.class);

                streetNumber = Integer.parseInt(getStreetNum.getText().toString());
                streetName = getStreetName.getText().toString();
                city = getCity.getText().toString();
                province = getProvince.getText().toString();
                nameOfLocation = getNameOfLocation.getText().toString();

                locationDBHelper.addNewLocation(nameOfLocation, streetNumber, streetName, city, province);

                finish();
                startActivity(intent);
            }
        });

        Button cancel = (Button)findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
