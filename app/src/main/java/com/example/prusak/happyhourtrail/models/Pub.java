package com.example.prusak.happyhourtrail.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prusak on 2018-01-20.
 */

@IgnoreExtraProperties
public class Pub {

    public Pub(Beer[] beers, String lokalizacja) {
        this.beers = beers;
        this.lokalizacja = lokalizacja;
    }

    public Beer[] beers;
    public String lokalizacja;




    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lokalizacja", lokalizacja);
        result.put("beers", beers);
        return result;
    }






}
