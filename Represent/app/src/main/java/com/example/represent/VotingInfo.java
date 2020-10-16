package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class VotingInfo extends AppCompatActivity {

    String returnTo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_info);
        returnTo = this.getIntent().getStringExtra("returnTo");
    }

    public void back(View view) {
        if (returnTo.equals("CongressionalView")) {
            Intent intent = new Intent(this, CongressionalView.class);
            intent.putExtra("address", "address");
            finish();
            startActivity(intent);
        }
    }
}