package com.example.prusak.happyhourtrail;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.prusak.happyhourtrail.models.Beer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
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
 * Created by Prusak on 2018-01-21.
 */

public class Popup extends Activity implements View.OnClickListener  {

//    private View view;
    private Button b2;
    private ListAdapter adapter ;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    static public Map<String, String> pubs= new HashMap<>();
    static public Map<String, ArrayList<Beer>> pubBeers= new HashMap<>();
    private ListView list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
//        view = getLayoutInflater().inflate(R.layout.popup,
//                null);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getPubLocactions();
        getPubMenu();
        b2 =findViewById(R.id.back2);

        b2.setOnClickListener(this);
        list= (ListView) findViewById(R.id.list);
//        list.setOnClickListener(this);


        ArrayList<Beer> beers = new ArrayList<Beer>();
        for (Map.Entry<String, ArrayList<Beer>> entry : pubBeers.entrySet()) {
            if (getIntent().getExtras().getString("nazwa").equals(entry.getKey())) {
                beers.addAll(entry.getValue());
                Log.i("mapa", " wybrany pub popup "+ entry.getKey() );

            }
        }






        adapter = new ListAdapter(this, R.layout.row, beers);
        for(Beer beer : beers){
            Log.i("mapa", " pubs beers to adapter"+ beer.toString() );

        }

        list.setAdapter(adapter);

        Log.i("mapa", " end of popup" );


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ( i==R.id.back2) {
            Log.i("login", ("chcemy zamknac popup"));
            finish();
        }
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
                Picasso.with(Popup.this).load(p.etykieta).into(img);

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

    public void getPubLocactions(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    pubs.put(String.valueOf(dsp.child("nazwa").getValue(String.class)),String.valueOf(dsp.child("lokalizacja").getValue(String.class)));

                }
                Log.i("mapa", " pubs locations"+ pubs.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs location", databaseError.toException());
            }
        });

        Log.i("mapa", " pubs locations"+ pubs.toString() );


    }
    public void getPubMenu(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    ArrayList<Beer> beers = new ArrayList<>();
                    for(DataSnapshot beerDSP : dsp.child("beers").getChildren()){
                        beers.add(beerDSP.getValue(Beer.class));

                    }
                    pubBeers.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), beers);
                }
                Log.i("mapa", " pubs beers"+ pubBeers.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs beers", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }


}
