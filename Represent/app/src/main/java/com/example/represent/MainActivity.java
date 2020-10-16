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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.internal.concurrent.TaskRunner;

public class MainActivity extends AppCompatActivity {

    ExecutorService executorService = Executors.newFixedThreadPool(4);
    VideoView flag;
    TextView input;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<String> serialReps;
    RequestQueue requestQueue;
    boolean loaded = false;
    ProgressDialog dialog;

    public Context getContext() {
        return this;
    }

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
        String inputStr = street.getText().toString()
                .replace(" ", "%20")
                .replace(",", "").trim().concat("%20");

        // Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(true);
        dialog.show();
        makeRequest(view, inputStr);

        // https://maps.googleapis.com/maps/api/geocode/json?address=
        // API Key: AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY
    }

    public void curLocationSearch(final View view) throws IOException {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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

                            // Progress Dialog
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setMessage("Loading...");
                            dialog.setCancelable(true);
                            dialog.show();

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

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Ask for permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    } else {

                        // We have permission
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.MAX_VALUE, 0, locationListener);

                    }
                }
            }
        });
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

            // Progress Dialog
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(true);
            dialog.show();

            makeRequest(view, street.replace(" ", "%20").trim());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void makeRequest(View view, String input) {

        // Make request
        String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
        String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
        String addressTag = "&address=";
        String msg = "address";
        String address = input;
        HashMap<String, String> user = new HashMap<>();
        user.put("user", input);
        serialReps = new ArrayList<>();
        try {
            serialReps.add(0, ObjectSerializer.serialize(user));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<String> test = serialReps;

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

                    // Senator 1
                    try {
                        if (!s1Obj.getString("photoUrl").equals("")) {
                            curPhoto = s1Obj.getString("photoUrl").trim();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    String curName = s1Obj.getString("name");
                    String curParty = s1Obj.getString("party");
                    String curState = state;
                    String curLink = "";
                    String phone = "";
                    String facebook = "";
                    String twitter = "";
                    String youtube = "";
                    JSONArray channels = s1Obj.getJSONArray("channels");
                    try {
                        curLink = (String) s1Obj.getJSONArray("urls").get(0);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        phone = (String) s1Obj.getJSONArray("phones").get(0);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        JSONObject facebookObj = channels.getJSONObject(0);
                        facebook = (String) facebookObj.get("id");
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        JSONObject twitterObj = channels.getJSONObject(1);
                        twitter = (String) twitterObj.get("id");
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        JSONObject youtubeObj = channels.getJSONObject(2);
                        youtube = (String) youtubeObj.get("id");
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    HashMap<String, String> curRep = new HashMap<String, String>();
                    curRep.put("photo", curPhoto);
                    curRep.put("name", curName);
                    curRep.put("party", curParty);
                    curRep.put("location", curState);
                    curRep.put("weblink", curLink);
                    curRep.put("phone", phone);
                    curRep.put("facebook", facebook);
                    curRep.put("twitter", twitter);
                    curRep.put("youtube", youtube);
                    try {
                        serialReps.add(1, ObjectSerializer.serialize(curRep));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Senator 2
                    channels = s2Obj.getJSONArray("channels");
                    try {
                        if (!s2Obj.getString("photoUrl").equals("")) {
                            curPhoto = s2Obj.getString("photoUrl").trim();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        curPhoto = "";
                    }
                    curName = s2Obj.getString("name");
                    curParty = s2Obj.getString("party");
                    curState = state;
                    try {
                        curLink = (String) s2Obj.getJSONArray("urls").get(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        curLink = "";
                    }
                    try {
                        phone = (String) s2Obj.getJSONArray("phones").get(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        phone = "";
                    }
                    try {
                        JSONObject facebookObj = channels.getJSONObject(0);
                        facebook = (String) facebookObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        facebook = "";
                    }
                    try {
                        JSONObject twitterObj = channels.getJSONObject(1);
                        twitter = (String) twitterObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        twitter = "";
                    }
                    try {
                        JSONObject youtubeObj = channels.getJSONObject(2);
                        youtube = (String) youtubeObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        youtube = "";
                    }
                    curRep = new HashMap<String, String>();
                    curRep.put("photo", curPhoto);
                    curRep.put("name", curName);
                    curRep.put("party", curParty);
                    curRep.put("location", curState);
                    curRep.put("weblink", curLink);
                    curRep.put("phone", phone);
                    curRep.put("facebook", facebook);
                    curRep.put("twitter", twitter);
                    curRep.put("youtube", youtube);
                    try {
                        serialReps.add(2, ObjectSerializer.serialize(curRep));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Representative
                    channels = repObj.getJSONArray("channels");
                    try {
                        if (!repObj.getString("photoUrl").equals("")) {
                            curPhoto = repObj.getString("photoUrl").trim();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        curPhoto = "";
                    }
                    curName = repObj.getString("name");
                    curParty = repObj.getString("party");
                    String districtTest = district.substring(district.indexOf("cd:") + 3);
                    if (districtTest.length() < 1 || districtTest.length() > 3) {
                        curState = "District-at-Large";
                    } else {
                        curState = "District " + districtTest;
                    }
                    try {
                        curLink = (String) repObj.getJSONArray("urls").get(0);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        phone = (String) repObj.getJSONArray("phones").get(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        phone = "";
                    }
                    try {
                        JSONObject facebookObj = channels.getJSONObject(0);
                        facebook = (String) facebookObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        facebook = "";
                    }
                    try {
                        JSONObject twitterObj = channels.getJSONObject(1);
                        twitter = (String) twitterObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        twitter = "";
                    }
                    try {
                        JSONObject youtubeObj = channels.getJSONObject(2);
                        youtube = (String) youtubeObj.get("id");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        youtube = "";
                    }
                    curRep = new HashMap<String, String>();
                    curRep.put("photo", curPhoto);
                    curRep.put("name", curName);
                    curRep.put("party", curParty);
                    curRep.put("location", curState);
                    curRep.put("weblink", curLink);
                    curRep.put("phone", phone);
                    curRep.put("facebook", facebook);
                    curRep.put("twitter", twitter);
                    curRep.put("youtube", youtube);
                    try {
                        serialReps.add(3, ObjectSerializer.serialize(curRep));
                        SharedPreferences shared = getApplicationContext().getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);

                        // Store representative info
                        try {
                            shared.edit().putString("representatives", ObjectSerializer.serialize((Serializable) serialReps)).apply();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        shared.edit().putString("votingInfo", "yes");
                        Intent intent = new Intent(getContext(), CongressionalView.class);
                        intent.putExtra("address", "address");
                        dialog.dismiss();
                        finish();
                        startActivity(intent);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    loaded = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Please enter a complete address or zip."
                        , Toast.LENGTH_LONG).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);

        //Enable persistent storage between views.
        if (loaded) {
            flag.stopPlayback();
        }
    }
}