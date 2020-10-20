package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CongressionalView extends AppCompatActivity {

    ExecutorService executorService = Executors.newFixedThreadPool(4);
    String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
    String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    String addressTag = "&address=";
    String votingInfo = "";
    LatLng userLoc;
    ArrayList<String> repSerialized;
    ArrayList<HashMap<String, String>> representativesArr;
    Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible

    public Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        votingInfo = shared.getString("votingInfo", "yes");
        userLoc = new LatLng(shared.getFloat("userLat", 0), shared.getFloat("userLng", 0));

        representativesArr = new ArrayList<>();
        try {
            repSerialized = (ArrayList<String>) ObjectSerializer.deserialize(shared.getString("representatives", ObjectSerializer.serialize(new ArrayList<>())));
            HashMap<String, String> user = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(0));
            this.representativesArr.add(0, user);

            HashMap<String, String> s1 = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(1));
            this.representativesArr.add(1, s1);

            HashMap<String, String> s2 = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(2));
            this.representativesArr.add(2, s2);

            HashMap<String, String> rep = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(3));
            this.representativesArr.add(3, rep);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ImageView bulb = findViewById(R.id.bulb);

        if (votingInfo.equals("yes")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animation.setDuration(2000); // 2 second duration for each animation cycle
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                    animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                    bulb.startAnimation(animation); //to start animation
                }
            });
        }

        try {
            parseData();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private void parseData() throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Get ids
                // Senator 1
                ImageView s1Photo = (ImageView) findViewById(R.id.s1Photo);
                TextView s1Name = (TextView) findViewById(R.id.s1Name);
                TextView s1Party = (TextView) findViewById(R.id.s1Party);
                TextView s1State = (TextView) findViewById(R.id.s1State);
                Button s1link = (Button) findViewById(R.id.s1Link);

                // Senator 2
                ImageView s2Photo = (ImageView) findViewById(R.id.s2Photo);
                TextView s2Name = (TextView) findViewById(R.id.s2Name);
                TextView s2Party = (TextView) findViewById(R.id.s2Party);
                TextView s2State = (TextView) findViewById(R.id.s2State);
                Button s2link = (Button) findViewById(R.id.s2Link);

                // Representative
                ImageView rPhoto = (ImageView) findViewById(R.id.rPhoto);
                TextView rName = (TextView) findViewById(R.id.rName);
                TextView rParty = (TextView) findViewById(R.id.rParty);
                TextView rDistrict = (TextView) findViewById(R.id.rDistrict);
                Button rlink = (Button) findViewById(R.id.rLink);

                /*HashMap<String, String> curRep = new HashMap<String, String>();
                curRep.put("photo", curPhoto);
                curRep.put("name", curName);
                curRep.("party", curParty);
                curRep.put("location", curState);
                curRep.put("weblink", curLink);*/

                HashMap<String, String> s1Obj = representativesArr.get(1);
                HashMap<String, String> s2Obj = representativesArr.get(2);
                HashMap<String, String> repObj = representativesArr.get(3);

                // Senator 1
                if (!s1Obj.get("photo").equals("")) {
                    Picasso.get().load(s1Obj.get("photo").trim()).placeholder(R.drawable.head_deep_blue)
                            .resize(70, 70).into(s1Photo);
                }
                try {
                    s1Name.setText(s1Obj.get("name"));
                    s1Party.setText(s1Obj.get("party"));
                    s1State.setText(s1Obj.get("location"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Senator 2
                if (!s2Obj.get("photo").equals("")) {
                    Picasso.get().load(s2Obj.get("photo").trim()).placeholder(R.drawable.head_deep_blue)
                            .resize(70, 70).into(s2Photo);
                }
                try {
                    s2Name.setText(s2Obj.get("name"));
                    s2Party.setText(s2Obj.get("party"));
                    s2State.setText(s2Obj.get("location"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Representative
                if (!repObj.get("photo").equals("")) {
                    Picasso.get().load(repObj.get("photo").trim()).placeholder(R.drawable.head_deep_blue)
                            .resize(70, 70).into(rPhoto);
                }
                try {
                    rName.setText(repObj.get("name"));
                    rParty.setText(repObj.get("party"));
                    rDistrict.setText(repObj.get("location"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void s1More(View view) {
        Intent intent = new Intent(this, DetailedView.class);
        intent.putExtra("rep", 1);
        finish();
        startActivity(intent);
    }

    public void s2More(View view) {
        Intent intent = new Intent(this, DetailedView.class);
        intent.putExtra("rep", 2);
        finish();
        startActivity(intent);
    }

    public void repMore(View view) {
        Intent intent = new Intent(this, DetailedView.class);
        intent.putExtra("rep", 3);
        finish();
        startActivity(intent);
    }

    public void back(View view) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("address", "address");
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("address", "address");
        finish();
        startActivity(intent);
    }

    public void votingInfo(View view) {
        animation.cancel();

        // Bring info view to front.
        ImageView bulb = findViewById(R.id.bulb);
        bulb.animate().alpha(0f).setDuration(1000).start();
        ConstraintLayout votingView = findViewById(R.id.votingView);
        votingView.animate().alpha(1f).setDuration(1000).start();
        votingView.bringToFront();
        Button yes = findViewById(R.id.yes);
        yes.setClickable(true);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    moreInfo(view);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Button no = findViewById(R.id.no);
        no.setClickable(true);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInfo(view);
            }
        });

        // Prevent clicking on buried images/buttons.
        ImageView s1Photo = findViewById(R.id.s1Photo);
        s1Photo.setClickable(false);
        ImageView s2Photo = findViewById(R.id.s2Photo);
        s2Photo.setClickable(false);
        Button s1Link = findViewById(R.id.s1Link);
        s1Link.setClickable(false);
        Button s2Link = findViewById(R.id.s2Link);
        s2Link.setClickable(false);
    }

    public void moreInfo(View view) throws IOException {

        // Progress Dialog
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(true);
        dialog.show();

        SharedPreferences shared = getApplicationContext().getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        shared.edit().putString("pollingLocationsMap", "").apply();
        shared.edit().putString("dropLocationsMap", "").apply();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> address = new ArrayList<>();
        try {
            address = geocoder.getFromLocation(userLoc.latitude, userLoc.longitude, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
        String addressTag = "&address=";
        String addressParsed = address.get(0).getAddressLine(0).replace(",", "").replace(" ", "%20").trim();
        if (addressParsed.contains("ca") || addressParsed.contains("CA")) {
            addressParsed = addressParsed.concat("%20");
        } else {
            addressParsed = addressParsed.concat("&electionId=7000");
        }
        String url = "https://www.googleapis.com/civicinfo/v2/voterinfo?key=" + key + addressTag + addressParsed;
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HashMap<String, String> pollingLocationsMap = new HashMap<>();
                try {
                    JSONArray pollingLocations = response.getJSONArray("pollingLocations");
                    if (pollingLocations.length() > 0) {
                        for (int i = 0; i < pollingLocations.length(); i++) {
                            JSONObject cur = pollingLocations.getJSONObject(i);

                            // Parse LatLng.
                            ArrayList<Double> latLng = new ArrayList<>();
                            try {
                                latLng.add(cur.getDouble("latitude"));
                                latLng.add(cur.getDouble("longitude"));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                break;
                            }

                            // Parse address.
                            JSONObject addressObj = cur.getJSONObject("address");
                            String address = "";
                            try {
                                address = addressObj.getString("locationName") + "\n";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("line1") + "\n";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("city") + ", ";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("state") + " ";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("zip");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                            // Parse polling hours.
                            String pollingHours = "";
                            try {
                                pollingHours = cur.getString("pollingHours");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            ArrayList<String> locStringArr = new ArrayList<>();
                            locStringArr.add(address);
                            locStringArr.add(pollingHours);
                            try {
                                pollingLocationsMap.put(ObjectSerializer.serialize(latLng), ObjectSerializer.serialize(locStringArr));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (pollingLocationsMap.size() != 0) {
                        SharedPreferences shared = getApplicationContext().getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);

                        // Store representative info
                        try {
                            shared.edit().putString("pollingLocationsMap", ObjectSerializer.serialize(pollingLocationsMap)).apply();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                HashMap<String, String> dropOffLocationsMap = new HashMap<>();
                try {
                    JSONArray dropLocations = response.getJSONArray("dropOffLocations");
                    if (dropLocations.length() > 0) {
                        for (int i = 0; i < dropLocations.length(); i++) {
                            JSONObject cur = dropLocations.getJSONObject(i);

                            // Parse LatLng.
                            ArrayList<Double> latLng = new ArrayList<>();
                            try {
                                latLng.add(cur.getDouble("latitude"));
                                latLng.add(cur.getDouble("longitude"));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                break;
                            }

                            // Parse address.
                            JSONObject addressObj = cur.getJSONObject("address");
                            String address = "";
                            try {
                                address = addressObj.getString("locationName") + "\n";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("line1") + "\n";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("city") + ", ";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("state") + " ";
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                address = address + addressObj.getString("zip");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                            // Parse polling hours.
                            String pollingHours = "";
                            try {
                                pollingHours = cur.getString("pollingHours").replace(";", "\n");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            ArrayList<String> locStringArr = new ArrayList<>();
                            locStringArr.add(address);
                            locStringArr.add(pollingHours);
                            try {
                                dropOffLocationsMap.put(ObjectSerializer.serialize(latLng), ObjectSerializer.serialize(locStringArr));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (dropOffLocationsMap.size() != 0) {
                        SharedPreferences shared = getApplicationContext().getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);

                        // Store representative info
                        try {
                            shared.edit().putString("dropLocationsMap", ObjectSerializer.serialize(dropOffLocationsMap)).apply();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (pollingLocationsMap.size() > 0 || dropOffLocationsMap.size() > 0) {
                        Intent intent = new Intent(getContext(), VotingInfo.class);
                        intent.putExtra("returnTo", "CongressionalView");
                        dialog.cancel();
                        finish();
                        startActivity(intent);
                    } else {
                        dialog.cancel();
                        Toast.makeText(getContext(), "Information is not yet available for this location."
                                , Toast.LENGTH_LONG).show();
                        noInfo(findViewById(R.id.no));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    dialog.cancel();
                    Toast.makeText(getContext(), "Information is not yet available for this location."
                            , Toast.LENGTH_LONG).show();
                    requestQueue.stop();
                    noInfo(findViewById(R.id.no));
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Information is not yet available for this location."
                        , Toast.LENGTH_LONG).show();
                dialog.cancel();
                noInfo(findViewById(R.id.no));

            }
        });
        requestQueue.add(request);
    }

    public void noInfo(View view) {
        // Take view to back
        ConstraintLayout votingView = findViewById(R.id.votingView);
        votingView.animate().alpha(0f).setDuration(1000).start();
        Button yes = findViewById(R.id.yes);
        yes.setClickable(false);
        Button no = findViewById(R.id.no);
        no.setClickable(false);
        votingView.removeView(votingView);
        ImageView bulb = findViewById(R.id.bulb);
        bulb.animate().cancel();

        // Allow clicking on original links.
        // Prevent clicking on buried images/buttons.
        ImageView s1Photo = findViewById(R.id.s1Photo);
        s1Photo.setClickable(true);
        ImageView s2Photo = findViewById(R.id.s2Photo);
        s2Photo.setClickable(true);
        Button s1Link = findViewById(R.id.s1Link);
        s1Link.setClickable(true);
        Button s2Link = findViewById(R.id.s2Link);
        s2Link.setClickable(true);
        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        shared.edit().putString("votingInfo", "no");
        votingInfo = "no";
    }
}