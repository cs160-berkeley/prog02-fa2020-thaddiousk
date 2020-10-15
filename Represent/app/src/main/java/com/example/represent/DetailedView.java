package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailedView extends AppCompatActivity {

    HashMap<String, String> curRep;

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
}