package com.getstrength.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        determinePaneLayout();


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String answer = null;
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                answer = "You are connected to a WiFi Network";
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                answer = "You are connected to a Mobile Network";
        } else
            answer = "No internet Connectivity";
        Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
    }

    private void determinePaneLayout() {
        FrameLayout fragmentDetails = (FrameLayout) findViewById(R.id.details_frag);
        if (fragmentDetails != null) {
            isTwoPane = true;

        } else {
            isTwoPane = false;
        }


    }



}


