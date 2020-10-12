package com.example.represent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.VideoView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    VideoView flag;
    TextView input;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag = (VideoView) findViewById(R.id.flag);
        flag.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.istockphotousflag);
        flag.start();

        // Enables video looping.
        flag.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                flag.start();
            }
        });
    }

    public void addressSearch(View view) throws IOException {
        TextView street = (TextView) findViewById(R.id.street);
        String inputStr = street.getText().toString().replace(" ", "+").trim();
        TextView city = (TextView) findViewById(R.id.city);
        inputStr = inputStr.concat(city.getText().toString().replace(" ", "+").trim());
        TextView state = (TextView) findViewById(R.id.state);
        inputStr = inputStr.concat(state.getText().toString().replace(" ", "+").trim());
        TextView zip = (TextView) findViewById(R.id.zip);
        inputStr = inputStr.concat(zip.getText().toString().replace(" ", "+").trim());
        LatLng curLocation = getLocationFromAddress(MainActivity.this, inputStr);

        Log.i("LatLng:", curLocation.latitude + " " + curLocation.longitude);

        // https://maps.googleapis.com/maps/api/geocode/json?address=
        // API Key: AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public void curLocationSearch(View view) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i("LatLng:", curLoc.latitude + " " + curLoc.longitude);
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
        LatLng random = new LatLng(randomLat, randomLng);
        Log.i("LatLng:", random.latitude + " " + random.longitude);
    }
}