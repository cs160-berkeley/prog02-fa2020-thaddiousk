package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class CongressionalView extends AppCompatActivity {

    String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
    String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    String addressTag = "&address=";
    HashMap<String, String> linkMap;
    HashMap<String, String> photoMap;

    private RequestQueue requestQueue;

    public HashMap<String, String> getLinkMap() {
        return linkMap;
    }

    public void setLinkMap(HashMap<String, String> input) {
        linkMap = input;
    }

    public HashMap<String, String> getPhotoMap() {
        return photoMap;
    }

    public void setPhotoMap(HashMap<String, String> photoMap) {
        this.photoMap = photoMap;
    }

    public Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestQueue.stop();
                finish();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Progress Dialog
        /*final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(true);
        dialog.show();*/

        // Make request
        try {
            parseData();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private void parseData() throws JSONException {
        String key = "AIzaSyDy5rAPx5q5u01TReZcLgvH54Xo5OHgFRY";
        String repInfoRequest = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
        String addressTag = "&address=";
        String msg = "address";
        String address = this.getIntent().getStringExtra(msg);

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

        // Url
        String url = repInfoRequest + key + addressTag + address;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
                            HashMap<String, String> imageMap = new HashMap<>();
                            try {
                                if (!s1Obj.getString("photoUrl").equals("")) {
                                    Picasso.get().load(s1Obj.getString("photoUrl").trim())
                                            .placeholder(R.drawable.head_deep_blue).resize(70, 70).into(s1Photo);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            HashMap<String, String> linkMap = new HashMap<>();
                            s1Name.setText(s1Obj.getString("name"));
                            s1Party.setText(s1Obj.getString("party"));
                            s1State.setText(state);
                            try {
                                linkMap.put("s1", (String) s1Obj.getJSONArray("urls").get(0));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                Picasso.get().load(s2Obj.getString("photoUrl").trim())
                                    .placeholder(R.drawable.head_deep_blue).resize(70, 70).into(s2Photo);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            s2Name.setText(s2Obj.getString("name"));
                            s2Party.setText(s2Obj.getString("party"));
                            s2State.setText(state);
                            try {
                                linkMap.put("s1", (String) s2Obj.getJSONArray("urls").get(0));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                Picasso.get().load(repObj.getString("photoUrl").trim())
                                    .placeholder(R.drawable.head_deep_blue).resize(70, 70).into(rPhoto);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            rName.setText(repObj.getString("name"));
                            rParty.setText(repObj.getString("party"));
                            rDistrict.setText("District " + district.substring(district.indexOf("cd:") + 3));
                            try {
                                linkMap.put("s1", (String) repObj.getJSONArray("urls").get(0));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            setLinkMap(linkMap);
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
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void main(View view) {

    }
}