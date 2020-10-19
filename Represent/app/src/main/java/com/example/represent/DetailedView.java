package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailedView extends AppCompatActivity {

    HashMap<String, String> curRep;
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
    String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    String addressTag = "&address=";
    String votingInfo = "";
    LatLng userLoc;
    int repNum;
    ArrayList<String> repSerialized;
    ArrayList<HashMap<String, String>> representativesArr;
    ImageView bulb;
    Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        ArrayList<HashMap<String, String>> representativesArr = new ArrayList<>();

        repNum = this.getIntent().getIntExtra("rep", 1);
        votingInfo = shared.getString("votingInfo", "yes");

        userLoc = new LatLng(shared.getFloat("userLat", 0), shared.getFloat("userLng", 0));

        this.representativesArr = new ArrayList<>();
        try {
            repSerialized = (ArrayList<String>) ObjectSerializer.deserialize(shared.getString("representatives", ObjectSerializer.serialize(new ArrayList<>())));
            HashMap<String, String> user = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(0));
            curRep = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(repNum));
            this.representativesArr.add(0, user);

            HashMap<String, String> s1 = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(1));
            this.representativesArr.add(1, s1);

            HashMap<String, String> s2 = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(2));
            this.representativesArr.add(2, s2);

            HashMap<String, String> reps = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(3));
            this.representativesArr.add(3, reps);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.bulb = findViewById(R.id.bulb2);

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

        ImageView image = findViewById(R.id.imageView2);

        if (!curRep.get("photo").equals("")) {
            Picasso.get().load(curRep.get("photo").trim()).placeholder(R.drawable.head_deep_blue)
                    .resize(130, 130).into(image);
        }

        TextView textView2 = findViewById(R.id.splashTitle);
        textView2.setText(curRep.get("name"));

        TextView partyState = findViewById(R.id.partyState);
        partyState.setText(curRep.get("party") + " (" + curRep.get("location") + ")");

        Button phone = findViewById(R.id.phone);
        try {
            phone.setText(curRep.get("phone"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Button webSite = findViewById(R.id.webSite);
        webSite.setText(curRep.get("weblink"));

    }

    public Context getContext() {
        return this;
    }

    public void back(View view) {
        Intent intent = new Intent(this, CongressionalView.class);
        intent.putExtra("address", "address");
        finish();
        startActivity(intent);
    }

    public void loadSite(View view) {
        String url = curRep.get("weblink");
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(intent);
    }

    public void loadCall(View view) {
        String phoneNumber = curRep.get("phone");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void loadFacebook(View view) {
        String url = curRep.get("facebook");
        Uri webPage;
        if (!url.equals("")) {
            webPage = Uri.parse("https://www.facebook.com/" + url);
        } else {
            Toast.makeText(this, "This person does not have a Facebook page.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(intent);
    }

    public void loadTwitter(View view) {
        String url = curRep.get("twitter");
        Uri webPage;
        if (!url.equals("")) {
            webPage = Uri.parse("https://twitter.com/" + url);
        } else {
            Toast.makeText(this, "This person does not have a Twitter page.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(intent);
    }

    public void loadYoutube(View view) {
        String url = curRep.get("youtube");
        Uri webPage;
        if (!url.equals("")) {
            if (url.length() == 24) {
                webPage = Uri.parse("https://youtube.com/channel/" + url);
            } else {
                webPage = Uri.parse("https://youtube.com/" + url);
            }
        } else {
            Toast.makeText(this, "This person does not have a Youtube page.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(intent);
    }

    public void votingInfo(View view) {
        animation.cancel();

        // Bring info view to front.
        ImageView bulb = findViewById(R.id.bulb2);
        bulb.animate().alpha(0f).setDuration(1000).start();
        ConstraintLayout votingView = findViewById(R.id.votingView2);
        votingView.animate().alpha(1f).setDuration(1000).start();
        votingView.bringToFront();
        Button yes = findViewById(R.id.yes2);
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
        Button no = findViewById(R.id.no2);
        no.setClickable(true);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInfo(view);
            }
        });
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
                        intent.putExtra("returnTo", "DetailedView");
                        intent.putExtra("rep", repNum);
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
        ConstraintLayout votingView = findViewById(R.id.votingView2);
        votingView.animate().alpha(0f).setDuration(1000).start();
        Button yes = findViewById(R.id.yes2);
        yes.setClickable(false);
        Button no = findViewById(R.id.no2);
        no.setClickable(false);
        votingView.removeView(votingView);
        this.bulb.animate().cancel();

        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        shared.edit().putString("votingInfo", "no");
        votingInfo = "no";
    }
}