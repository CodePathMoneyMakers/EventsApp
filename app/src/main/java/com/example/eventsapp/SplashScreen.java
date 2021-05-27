package com.example.eventsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import com.jgabrielfreitas.core.BlurImageView;

import pl.droidsonroids.gif.GifImageView;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        BlurImageView blurImageView = findViewById(R.id.blurImage);
        blurImageView.setBlur(3);


        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        handler.postDelayed(runnable , 10000);

//        GifImageView gifImageView = findViewById(R.id.mappin);
//        gifImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                        .addSharedElement(gifImageView, "transition").build();
//                Navigation.findNavController(v).navigate(R.id.action_splashScreen_to_loginActivity, null, null, extras);
//            }
//        });



    }
}