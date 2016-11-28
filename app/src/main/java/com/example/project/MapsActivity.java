package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 410020;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private LatLng coordinates;
    private SupportMapFragment mapFragment;
    private Marker currentLocationMarker;
    private LocationDBHelper locationDBHelper;
    private ArrayList<com.example.project.Location> locations;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupLocationServices();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationDBHelper = new LocationDBHelper(this);

        locations = new ArrayList<>(locationDBHelper.getAllLocations());
    }

    /* Sets up the location services */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupLocationServices() {
        requestLocationPermissions();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String locationConfig = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            Intent enableGPS = new Intent(locationConfig);
            startActivity(enableGPS);
        }
    }

    // Requests location permission from the user
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermissions() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null && !googleApiClient.isConnected())
            googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //checks for location permission if granted by the user.
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        //initializes the Google API Client and connects
        buildGoogleApiClient();
        googleApiClient.connect();

        /*
        When a marker is clicked, the app will calculate the distance and time
        required for the user to go between their current path and the added path
        */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                calculate(marker.getPosition());
                return false;
            }
        });

        for(int i = 0; i < locations.size(); i++){

            int streetNum = locations.get(i).getStreetNumber();
            String streetName = locations.get(i).getStreetName();
            String city = locations.get(i).getCity();
            String province = locations.get(i).getProvince();
            String nameOfLoc = locations.get(i).getNameOfLocation();

            double latitude = 0;
            double longitude = 0;

            //Requires a Geocoder to be present in the user's phone.
            //Will retrieve the address string and convert into lat/lng
            if(Geocoder.isPresent()){
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String address = streetNum + streetName + city + province;

                try {
                    List<android.location.Address> listOfAddress = geocoder.getFromLocationName(address, 1);
                    for(android.location.Address addr: listOfAddress) {
                        latitude = addr.getLatitude();
                        longitude = addr.getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Adds markers to the map till the array list is depleted
            if(locations.size() != 0){
                coordinates = new LatLng(latitude, longitude);
                MarkerOptions marker = new MarkerOptions();
                marker.position(coordinates);
                marker.title(nameOfLoc);
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(marker);
            }
        }
    }

    //builds the Google API Client
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();

        Location lastLocation = null;

        //checks for permission to use fine location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }


        //if the last location is not null, the coordinates will get its lat/lng attributes and set on map
        if (lastLocation != null) {
            coordinates = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(coordinates);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currentLocationMarker = mMap.addMarker(markerOptions);
        }

        //Creating a new location request to set current location
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    //Menu items, add and delete locations, initalized here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_location_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_location:
                Intent intent = new Intent(MapsActivity.this, AddLocation.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.delete_location:
                intent = new Intent(MapsActivity.this, DeleteLocation.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //When location is changed, camera position is moved to new location.
    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        coordinates = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coordinates);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currentLocationMarker = mMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinates).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    //Calculates the distance and time between two points
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void calculate(LatLng position){

        Location lastLocation = null;
        Location newLocation = new Location("");

        newLocation.setLatitude(position.latitude);
        newLocation.setLongitude(position.longitude);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        LatLng currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        String url = getUrl(currLocation, position);
        double distance = lastLocation.distanceTo(newLocation);

        int speedInMetres = 10;

        float time = (float) (distance/speedInMetres)/60;

        FetchUrl fetch = new FetchUrl();
        fetch.execute(url);

        Toast.makeText(this, "Distance: " + distance + " m, Time: " + time + " min.", Toast.LENGTH_SHORT).show();
    }

    /*
    DISCLAIMER: Most of the methods from this point onwards as well as the DataParser.java class
    was done using the help of many users from StackOverFlow.
     */

    //Gets the Google Maps API URl set up to be downloaded.
    private String getUrl(LatLng origin, LatLng dest) {

        String original_point = "origin=" + origin.latitude + "," + origin.longitude;
        String destination_point = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";
        String parameters = original_point + "&" + destination_point + "&" + sensor;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    //Fetches the URL download using AsyncTask
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    //Downloads the URL and sets the data into a string
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            inputStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}