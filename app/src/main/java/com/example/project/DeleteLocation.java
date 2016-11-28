package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class DeleteLocation extends Activity{

    LocationDBHelper locationDBHelper;
    ArrayList<String> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_location);
        setTitle("Delete Location");

        locationDBHelper = new LocationDBHelper(this);
        locationList = new ArrayList<>(locationDBHelper.getAllLocationNames());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationList);

        final Spinner location_spinner = (Spinner)findViewById(R.id.location_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location_spinner.setAdapter(adapter);

        Button delete = (Button)findViewById(R.id.delete_location_btn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DeleteLocation.this, MapsActivity.class);
                locationDBHelper.deleteLocation(locationList.get(location_spinner.getSelectedItemPosition()));

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
