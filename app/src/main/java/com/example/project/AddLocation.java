package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddLocation extends Activity {

    public int streetNumber;
    public String streetName;
    public String city;
    public String province;
    public String nameOfLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        final LocationDBHelper locationDBHelper = new LocationDBHelper(this);

        Button addLocation = (Button)findViewById(R.id.add_btn);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddLocation.this, MapsActivity.class);

                EditText getStreetNum = (EditText)findViewById(R.id.insert_street_num);
                streetNumber = Integer.parseInt(getStreetNum.getText().toString());

                EditText getStreetName = (EditText)findViewById(R.id.insert_street_name);
                streetName = getStreetName.getText().toString();

                EditText getCity = (EditText)findViewById(R.id.insert_city);
                city = getCity.getText().toString();

                EditText getProvince = (EditText)findViewById(R.id.insert_province);
                province = getProvince.getText().toString();

                EditText getNameOfLocation = (EditText)findViewById(R.id.insert_name_address);
                nameOfLocation = getNameOfLocation.getText().toString();

                locationDBHelper.addNewLocation(nameOfLocation, streetNumber, streetName, city, province);

                finish();
                startActivity(intent);
            }
        });
    }
}
