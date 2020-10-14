package com.example.represent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.internal.concurrent.TaskRunner;

public class MainActivity extends AppCompatActivity {

    VideoView flag;
    TextView input;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<CivicData> representatives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*if (no user){
            representatives = new ArrayList<>();
        } else {

        }*/

        flag = (VideoView) findViewById(R.id.flag);
        flag.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.istockphotousflag);

        // Enables video looping.

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flag.start();
                flag.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        flag.start();
                    }
                });
            }
        });
    }

    public void addressSearch(View view) throws IOException {
        TextView street = (TextView) findViewById(R.id.street);

        if (street.getText().toString().equals("") || street.getText().toString().equals("")) {
            Toast.makeText(view.getContext(), "Please enter an address", Toast.LENGTH_LONG).show();
            return;
        }
        String inputStr = street.getText().toString().replace(" ", "%20").trim().concat("%20");
        TextView city = (TextView) findViewById(R.id.city);
        if (city.getText().toString().equals("") || city.getText().toString().equals("")) {
            Toast.makeText(view.getContext(), "Please enter a valid city", Toast.LENGTH_LONG).show();
            return;
        }
        inputStr = inputStr.concat(city.getText().toString().replace(" ", "%20").trim().concat("%20"));
        TextView state = (TextView) findViewById(R.id.state);
        if (state.getText().toString().equals("") || state.getText().toString().equals("")) {
            Toast.makeText(view.getContext(), "Please enter a valid state", Toast.LENGTH_LONG).show();
            return;
        }
        inputStr = inputStr.concat(state.getText().toString().replace(" ", "%20").trim().concat("%20"));
        TextView zip = (TextView) findViewById(R.id.zip);
        if (zip.getText().toString().equals("") || zip.getText().toString().equals("")) {
            Toast.makeText(view.getContext(), "Please enter a zip code", Toast.LENGTH_LONG).show();
            return;
        }
        inputStr = inputStr.concat(zip.getText().toString().replace(" ", "%20").trim());

        makeRequest(view, inputStr);

        // https://maps.googleapis.com/maps/api/geocode/json?address=
        // API Key: AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY
    }

    public void curLocationSearch(final View view) throws IOException {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(view.getContext(), Locale.getDefault());

                String street = null;
                String city = null;
                String state = null;
                String country = null;
                String postal = null;
                String knownName = null;

                try {
                    addresses = geocoder.getFromLocation(curLoc.latitude, curLoc.longitude, 1);
                    street = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    postal = addresses.get(0).getPostalCode();
                    country = addresses.get(0).getCountryName();
                    knownName = addresses.get(0).getFeatureName();

                    makeRequest(view, street.replace(" ", "%20").trim());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        // If device is running SDK < 23

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.MAX_VALUE, 0, locationListener);
        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.MAX_VALUE, 0, locationListener);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void randomSearch(View view)  {
        Random r = new Random();
        double randomLat = 32.5555 + (41.5555 - 32.5555) * r.nextDouble();
        double randomLng = -117.5555 + (-81.555 + 117.5555) * r.nextDouble();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());

        String street = null;
        String city = null;
        String state = null;
        String country = null;
        String postal = null;
        String knownName = null;

        try {
            addresses = geocoder.getFromLocation(randomLat, randomLng, 1);
            street = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            postal = addresses.get(0).getPostalCode();
            country = addresses.get(0).getCountryName();
            knownName = addresses.get(0).getFeatureName();

            makeRequest(view, street.replace(" ", "%20").trim());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void makeRequest(View view, String input) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
                String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
                String addressTag = "&address=";
                String msg = "address";
                String address = input;
                CivicData user = new CivicData(input);
                representatives.add(0, user);

                String url = repInfoRequest + key + addressTag + address;

                RequestQueue requestQueue;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray officials = response.getJSONArray("officials");
                            JSONObject normInput = response.getJSONObject("normalizedInput");
                            JSONArray offices = response.getJSONArray("offices");
                            JSONObject districtObj = offices.getJSONObject(3);
                            String district = districtObj.getString("divisionId");
                            String state = normInput.getString("state");
                            JSONObject s1Obj = officials.getJSONObject(2);
                            JSONObject s2Obj = officials.getJSONObject(3);
                            JSONObject repObj = officials.getJSONObject(4);
                            String curPhoto = "";
                            try {
                                if (!s1Obj.getString("photoUrl").equals("")) {
                                    curPhoto = s1Obj.getString("photoUrl").trim();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            HashMap<String, String> linkMap = new HashMap<>();
                            String curName = s1Obj.getString("name");
                            String curParty = s1Obj.getString("party");
                            String curState = state;
                            String curLink = "";
                            try {
                                curLink = (String) s1Obj.getJSONArray("urls").get(0);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            CivicData curCivic = new CivicData(curPhoto, curName, curParty, curState, curLink);
                            representatives.add(1, curCivic);
                            try {
                                curPhoto = s2Obj.getString("photoUrl").trim();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            curName = s2Obj.getString("name");
                            curParty = s2Obj.getString("party");
                            curState = state;
                            try {
                                curLink = (String) s2Obj.getJSONArray("urls").get(0);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            curCivic = new CivicData(curPhoto, curName, curParty, curState, curLink);
                            representatives.add(2, curCivic);
                            try {
                                curPhoto = repObj.getString("photoUrl").trim();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            curName = repObj.getString("name");
                            curParty = repObj.getString("party");
                            curState = "District " + district.substring(district.indexOf("cd:") + 3);
                            try {
                                curLink = (String) repObj.getJSONArray("urls").get(0);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            curCivic = new CivicData(curPhoto, curName, curParty, curState, curLink);
                            representatives.add(3, curCivic);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(request);
            }
        });

        //Enable persistent storage between views.
        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);

        // Store representative info
        shared.edit().putString("representatives", ObjectSerializer.serialize(representatives)).apply();

        Intent intent = new Intent(view.getContext(), CongressionalView.class);
        String msg = "address";
        intent.putExtra(msg, input);
        startActivity(intent);
    }
}