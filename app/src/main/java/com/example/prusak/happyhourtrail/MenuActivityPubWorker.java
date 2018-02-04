package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prusak on 2018-01-10.
 */

public class MenuActivityPubWorker extends AppCompatActivity implements View.OnClickListener    {

        private Button searchButton;
        private Button updatePubButton;
        private Button AddPromoButton;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;
    private ListView listPromotion;
    private ArrayAdapter adapterPromotion ;
    static public Map<String, String> promotionsMap= new HashMap<>();
    static public ArrayList<String > promotions = new ArrayList<>();
    static public String pubName;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.menu_pub_worker);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            pubName = getIntent().getExtras().getString("nazwa");
            getPubPromotion();
            searchButton = findViewById(R.id.button_search);
            updatePubButton = findViewById(R.id.editPubMenu);
            AddPromoButton = findViewById(R.id.addPromoButton);
            searchButton.setOnClickListener(this);
            AddPromoButton.setOnClickListener(this);
            updatePubButton.setOnClickListener(this);








            Log.i("menu", ("jestesmy w menu pracownika pubu"));


        }


    @Override
    protected void onStart() {
        super.onStart();

//        ArrayList<String> promos = new ArrayList<String>();
//        for (Map.Entry<String,String> entry : promotionsMap.entrySet()) {
//            promotions.add(entry.getKey());
//            Log.i("mapa", " wybrany pub promotion "+ entry.getValue() );
//
//
//        }

        listPromotion= (ListView) findViewById(R.id.promotionList);
        adapterPromotion = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, promotions);
        listPromotion.setAdapter(adapterPromotion);

        listPromotion.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Intent intent = new Intent(MenuActivityPubWorker.this,
                        PromoEdit.class);
                intent.putExtra("nazwa", pubName);
                intent.putExtra("promo", value);
                intent.putExtra("promoTitle", promotionsMap.get(value));
                startActivity(intent);
            }
        });
        adapterPromotion.notifyDataSetChanged();
    }
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.button_search) {
                Log.i("map", ("chcemy być w mapie"));
                startActivity(new Intent(MenuActivityPubWorker.this, MapActivity.class));
            } else if (i == R.id.editPubMenu) {
                Log.i("scrappr", ("chcemy edytować ofertę"));
                startActivity(new Intent(MenuActivityPubWorker.this, UpdateDataActivity.class));
            }else if (i == R.id.addPromoButton) {
                Log.i("scrappr", ("chcemy edytować ofertę"));
                Intent intent = new Intent(MenuActivityPubWorker.this,
                        PromoEdit.class);
                intent.putExtra("nazwa", pubName);
                intent.putExtra("promoTitle", "");
                intent.putExtra("promo", "");
                startActivity(intent);
            }else if (i == R.id.log_out_button) {
                Log.i("logowanie", ("wylogoawnie z menu_admin"));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivityPubWorker.this, MainActivity.class));
            }
        }



    public void getPubPromotion(){


        mDatabase.child("pubs").child(pubName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                    ArrayList<String> promotions = new ArrayList<>();
                    for(DataSnapshot promoDSP : dataSnapshot.child("promotions").getChildren()){
                        if(!promotions.contains((promoDSP.getValue(String.class)))  &&! promotionsMap.containsValue(promoDSP.getKey()) ) {

                                    promotionsMap.put(promoDSP.getValue(String.class), promoDSP.getKey());


                        }else if (promotionsMap.containsValue(promoDSP.getKey())  && !promotions.contains((promoDSP.getValue(String.class))) )
                        {
                            while (promotionsMap.values().remove((promoDSP.getKey())));
                            promotionsMap.put(promoDSP.getValue(String.class), promoDSP.getKey());

                        }else if (promotionsMap.containsValue(promoDSP.getKey())  && !promotions.contains((promoDSP.getValue(String.class))) )
                        {
                            promotionsMap.remove(promoDSP.getValue(String.class));
                            promotionsMap.put(promoDSP.getValue(String.class), promoDSP.getKey());
                        }
                    }

//                promotions.add((promoDSP.getValue(String.class)));

                promotions.clear();
         for (Map.Entry<String,String> entry : promotionsMap.entrySet()) {
             promotions.add(entry.getKey());
             Log.i("mapa", " wybrany pub promotion "+ entry.getValue() );
         }


                Log.i("mapa", " pubs promotions"+ promotions.toString() );
                adapterPromotion.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs promotions", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }










    }



