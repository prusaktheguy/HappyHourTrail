package com.example.prusak.happyhourtrail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prusak.happyhourtrail.models.Beer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prusak on 2018-01-10.
 */

public class EditPubOffer extends AppCompatActivity implements View.OnClickListener    {

        private Button locationOk;
        private Button backButton;
        private Button AddBeerButton;
    private Button logOutButton;

    private EditText localization;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;
    private ListView BeersList;
    private ListAdapter adapterBeers ;
    static public Map<String, Beer> beersMap= new HashMap<>();
    static public ArrayList<Beer > beers = new ArrayList<>();
    static public String pubName;
    static public String location;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_pub_menu);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            pubName = getIntent().getExtras().getString("nazwa");
            localization=findViewById(R.id.localization);

            getPubBeers();
            locationOk = findViewById(R.id.locOk);
            backButton = findViewById(R.id.backToWorkerMenu);
            AddBeerButton = findViewById(R.id.addBeerButton);
            logOutButton=findViewById(R.id.log_out_button);
            locationOk.setOnClickListener(this);
            AddBeerButton.setOnClickListener(this);
            backButton.setOnClickListener(this);
            logOutButton.setOnClickListener(this);







            Log.i("menu", ("jestesmy w menu edycji piw pracownika pubu"));


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

        BeersList= (ListView) findViewById(R.id.promotionList);
        adapterBeers = new ListAdapter(this, R.layout.row, beers);
        BeersList.setAdapter(adapterBeers);

        BeersList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Beer piwo = (Beer)adapter.getItemAtPosition(position);
                Intent intent = new Intent(EditPubOffer.this,
                        BeerEdit.class);
                intent.putExtra("nazwa", pubName);
                intent.putExtra("nazwaPiwa", piwo.nazwa);
                intent.putExtra("browar", piwo.browar);
                intent.putExtra("etykieta", piwo.etykieta);
                intent.putExtra("cena", piwo.cena);
                intent.putExtra("gatunek", piwo.gatunek);
//                intent.putExtra("promoTitle", beersMap.get(value).nazwa);
                startActivity(intent);
            }
        });
        adapterBeers.notifyDataSetChanged();
    }
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.locOk) {
                Log.i("map", ("zmieniamy lokalizacje"));
                mDatabase.child("pubs").child(pubName).child("lokalizacja").setValue(localization.getText().toString());
                Toast.makeText(this, "Zaktualizowano", Toast.LENGTH_SHORT).show();

            } else if (i == R.id.backToWorkerMenu) {
                Log.i("scrappr", ("chcemy edytować pub"));
                Intent intent = new Intent(EditPubOffer.this,
                        MenuActivityPubWorker.class);
                intent.putExtra("nazwa", pubName);
                startActivity(intent);
            }else if (i == R.id.addBeerButton) {
                Log.i("scrappr", ("chcemy edytować ofertę"));
                Intent intent = new Intent(EditPubOffer.this,
                        BeerEdit.class);
                intent.putExtra("nazwa", pubName);
                intent.putExtra("nazwaPiwa", "");
                intent.putExtra("browar", "");
                intent.putExtra("etykieta", "");
                intent.putExtra("cena", "");
                intent.putExtra("gatunek", "");
                startActivity(intent);
            }else if (i == R.id.log_out_button) {
                Log.i("logowanie", ("wylogoawnie z EditPubOffer"));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EditPubOffer.this, MainActivity.class));
            }
        }



    public void getPubBeers(){


        mDatabase.child("pubs").child(pubName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                localization.setText(dataSnapshot.child("lokalizacja").getValue(String.class));

//                    ArrayList<String> promotions = new ArrayList<>();
                    for(DataSnapshot promoDSP : dataSnapshot.child("beers").getChildren()){
                        if(!beersMap.containsValue((promoDSP.getValue(Beer.class)) )  )  {
                            beersMap.put(promoDSP.getValue(Beer.class).nazwa,promoDSP.getValue(Beer.class));

                    }}

//                promotions.add((promoDSP.getValue(String.class)));

                beers.clear();
         for (Map.Entry<String,Beer> entry : beersMap.entrySet()) {
             beers.add(entry.getValue());
             Log.i("mapa", " wybrany pub promotion "+ entry.getValue() );
         }


                Log.i("mapa", " pubs promotions"+ beers.toString() );
                adapterBeers.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs promotions", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }

    public class ListAdapter extends ArrayAdapter<Beer> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<Beer> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.row, null);
            }
//            if (convertView != null) {
//                return v;
//            }
            Beer p = getItem(position);
//            Log.i("mapa", " adapter beers"+ p.toString() );


            TextView tt1 = (TextView) v.findViewById(R.id.name);
            TextView tt2 = (TextView) v.findViewById(R.id.browar);
            TextView tt3 = (TextView) v.findViewById(R.id.gatunek);
            TextView tt4 = (TextView) v.findViewById(R.id.cena);
            ImageView img = (ImageView) v.findViewById(R.id.imageView2);

            if(img!=null){
//                    img.setImageDrawable(LoadImageFromWebOperations(p.etykieta));
                Picasso.with(EditPubOffer.this).load(p.etykieta).into(img);

            }
            if (tt1 != null) {
                tt1.setText(getString(R.string.beerName)+ "   " +p.nazwa);
                Log.i("mapa", " adapter beer name"+ p.nazwa );

            }

            if (tt2 != null) {
                tt2.setText(getString(R.string.browar)+ "   " +p.browar);
            }

            if (tt3 != null) {
                tt3.setText(getString(R.string.gatunek) + "   " +p.gatunek);
            }
            if (tt4 != null) {
                tt4.setText(getString(R.string.cena) + "   " +  p.cena);
            }


            return v;
        }

    }








    }



