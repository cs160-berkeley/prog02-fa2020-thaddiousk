package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.RequestQueue;

public class CongressionalView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        String msg = "address";
        String address = this.getIntent().getStringExtra(msg);

        RequestQueue


    }
}