package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class VotingInfo extends AppCompatActivity {

    String returnTo = "";
    HashMap<LatLng, ArrayList<String>> pollingLocationsMap = new HashMap<>();
    HashMap<LatLng, ArrayList<String>> dropLocationsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_info);
        returnTo = this.getIntent().getStringExtra("returnTo");
        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        LatLng curLoc = new LatLng(shared.getFloat("userLat", 0f), shared.getFloat("userLng", 0f));
        HashMap<String, String> pollingLocationsInput = new HashMap<>();
        HashMap<String, String> dropLocationsInput = new HashMap<>();
        try {
            pollingLocationsInput = (HashMap<String, String>) ObjectSerializer.deserialize(shared.getString("pollingLocationsMap", ObjectSerializer.serialize(new HashMap<String, String>())));
            for (String each : pollingLocationsInput.keySet()) {
                ArrayList<Double> curDouble= (ArrayList<Double>) ObjectSerializer.deserialize(each);
                LatLng curLatLng = new LatLng(curDouble.get(0), curDouble.get(1));
                ArrayList<String> curPolling = (ArrayList<String>) ObjectSerializer.deserialize(pollingLocationsInput.get(each));
                pollingLocationsMap.put(curLatLng, curPolling);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            dropLocationsInput = (HashMap<String, String>) ObjectSerializer.deserialize(shared.getString("dropLocationsMap", ObjectSerializer.serialize(new HashMap<String, String>())));
            for (String each : dropLocationsInput.keySet()) {
                ArrayList<Double> curDouble= (ArrayList<Double>) ObjectSerializer.deserialize(each);
                LatLng curLatLng = new LatLng(curDouble.get(0), curDouble.get(1));
                ArrayList<String> curDrop = (ArrayList<String>) ObjectSerializer.deserialize(dropLocationsInput.get(each));
                dropLocationsMap.put(curLatLng, curDrop);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        fillDropOffLocations(curLoc);
        fillPollingLocations(curLoc);
    }

    public Context getContext()
    {
        return this;
    }
    public void fillDropOffLocations(LatLng curLocation) {
        TextView dropOffText = findViewById(R.id.dropOffText);
        if (dropLocationsMap.size() > 0) {
            double min = Double.MAX_VALUE;
            HashMap<Float, String> distances = new HashMap<>();
            for (LatLng each : dropLocationsMap.keySet()) {
                float distance = getDistanceFloat(curLocation, each);
                distances.put(distance, dropLocationsMap.get(each).get(0)
                        + "\n" + dropLocationsMap.get(each).get(1));
            }

            PriorityQueue<Float> minPQ = new PriorityQueue<>();
            minPQ.addAll(distances.keySet());

            String output = "";
            int i = 0;
            DecimalFormat df = new DecimalFormat("0.00");
            while (!minPQ.isEmpty() && i < 3) {
                Float distance = minPQ.poll();
                if (!distances.isEmpty() && distance != null) {
                    output = output + distances.get(distance) + "\n" + df.format(distance * 0.000621371f) + " miles away" + "\n\n";
                }
                i++;
            }
            if (output.length() > 3) {
                output = output.substring(0, output.length() - 2);
            }
            dropOffText.setText(output);
        } else {
            dropOffText.setText("Drop-off location data is not yet available for this location");
        }
    }

    public void fillPollingLocations(LatLng curLocation) {
        TextView pollingText = findViewById(R.id.pollingText);
        if (pollingLocationsMap.size() > 0) {
            double min = Double.MAX_VALUE;
            HashMap<Float, String> distances = new HashMap<>();
            for (LatLng each : pollingLocationsMap.keySet()) {
                float distance = getDistanceFloat(curLocation, each);
                distances.put(distance, pollingLocationsMap.get(each).get(0)
                        + "\n" + pollingLocationsMap.get(each).get(1));
            }

            PriorityQueue<Float> minPQ = new PriorityQueue<>();
            minPQ.addAll(distances.keySet());

            String output = "";
            int i = 0;
            DecimalFormat df = new DecimalFormat("0.00");
            while (!minPQ.isEmpty() && i < 3) {
                Float distance = minPQ.poll();
                if (!distances.isEmpty() && distance != null) {
                    output = output + distances.get(distance) + "\n" + df.format(distance * 0.000621371f) + " miles away" + "\n\n";
                }
                i++;
            }
            if (output.length() > 3) {
                output = output.substring(0, output.length() - 2);
            }
            pollingText.setText(output);
        } else {
            pollingText.setText("Polling location data is not yet available for this location");
        }
    }

    public float getDistanceFloat(LatLng lat, LatLng lng) {
        Location l1 = new Location("One");
        l1.setLatitude(lat.latitude);
        l1.setLongitude(lat.longitude);
        Location l2 = new Location("Two");
        l2.setLatitude(lng.latitude);
        l2.setLongitude(lng.longitude);
        float distance = l1.distanceTo(l2);
        return distance;
    }

    public void back(View view) {
        if (returnTo.equals("CongressionalView")) {
            Intent intent = new Intent(this, CongressionalView.class);
            intent.putExtra("address", "address");
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailedView.class);
            intent.putExtra("address", "address");
            int repNum = this.getIntent().getIntExtra("rep", 1);
            intent.putExtra("rep", repNum);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (returnTo.equals("CongressionalView")) {
            Intent intent = new Intent(this, CongressionalView.class);
            intent.putExtra("address", "address");
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailedView.class);
            intent.putExtra("address", "address");
            int repNum = this.getIntent().getIntExtra("rep", 1);
            intent.putExtra("rep", repNum);
            finish();
            startActivity(intent);
        }
    }
}