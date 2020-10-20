package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreen extends AppCompatActivity {

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.splashmusic);
        VideoView video = (VideoView) findViewById(R.id.videoSplash);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.splashscreen);
                video.start();
            }
        });
        final Intent intent = new Intent(this, MainActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView title = findViewById(R.id.splashTitle);
                title.animate().alpha(1f).setDuration(7000);
                mPlayer.start();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(intent);
                video.stopPlayback();
                mPlayer.stop();
                finish();
            }
        }, 7000);
    }
}