package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.server.response.FastParser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailedView extends AppCompatActivity {

    HashMap<String, String> curRep;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);



        SharedPreferences shared = this.getSharedPreferences("com.example.represent", Context.MODE_PRIVATE);
        ArrayList<HashMap<String, String>> representativesArr = new ArrayList<>();

        int rep = this.getIntent().getIntExtra("rep", 1);
        try {
            ArrayList<String> repSerialized = (ArrayList<String>) ObjectSerializer.deserialize(shared.getString("representatives", ObjectSerializer.serialize(new ArrayList<>())));
            curRep = (HashMap<String, String>) ObjectSerializer.deserialize(repSerialized.get(rep));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ImageView image = findViewById(R.id.imageView2);

        if (!curRep.get("photo").equals("")) {
            Picasso.get().load(curRep.get("photo").trim()).placeholder(R.drawable.head_deep_blue)
                    .resize(130, 130).into(image);
        }

        TextView textView2 = findViewById(R.id.textView2);
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
}