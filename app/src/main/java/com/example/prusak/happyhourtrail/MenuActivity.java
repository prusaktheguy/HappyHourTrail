package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Prusak on 2018-01-10.
 */

public class MenuActivity  extends AppCompatActivity implements View.OnClickListener    {

        private Button searchButton;
        private Button updateButton;
        private Button logOutButton;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.menu);

//            mDatabase = FirebaseDatabase.getInstance().getReference();
//            mAuth = FirebaseAuth.getInstance();
            searchButton = findViewById(R.id.button_search);
            updateButton = findViewById(R.id.button_update);
            logOutButton = findViewById(R.id.log_out_button);

            searchButton.setOnClickListener(this);
            logOutButton.setOnClickListener(this);
            updateButton.setOnClickListener(this);

            Log.i("menu", ("jestesmy w menu"));






//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.button_search) {
                Log.i("map", ("chcemy być w mapie"));
                startActivity(new Intent(MenuActivity.this, MapActivity.class));
            } else if (i == R.id.button_update) {
                Log.i("scrappr", ("chcemy być w scrapperze"));
                startActivity(new Intent(MenuActivity.this, UpdateDataActivity.class));
            }else if (i == R.id.log_out_button) {
                Log.i("logowanie", ("wylogoawnie z menu"));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
            }
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */



    }



