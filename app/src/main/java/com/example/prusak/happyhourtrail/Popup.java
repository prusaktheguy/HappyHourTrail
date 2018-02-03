package com.example.prusak.happyhourtrail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.prusak.happyhourtrail.models.Beer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.youkai.simpleratingview.SimpleRatingView;

/**
 * Created by Prusak on 2018-01-21.
 */

public class Popup extends Activity implements View.OnClickListener  {

//    private View view;
    private Button b2,report;
    private ListAdapter adapter ;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    static public Map<String, String> pubs= new HashMap<>();
    static public Map<String, ArrayList<Beer>> pubBeers= new HashMap<>();
    static public Map<String, ArrayList<String>> promotionsMap= new HashMap<>();
    static public Map<String, Float> marksCount= new HashMap<>();
    static public Map<String, Float> marks= new HashMap<>();
    static public ArrayList<String > promotions = new ArrayList<>();
    private ListView list;
    private ListView listPromotion;
    private ArrayAdapter adapterPromotion ;
    static public int ocena;
    static public int count;
    static public String pubName;
    static public SmileRating smileRating;
//    SimpleRatingView simpleRatingView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

//        view = getLayoutInflater().inflate(R.layout.popup,
//                null);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        pubName = getIntent().getExtras().getString("nazwa");


        if(pubName.equals("Piw Paw")){pubName ="PiwPaw";}
        if(pubName.contains("Z Innej Beczki")){pubName ="Z innej beczki";}
//        if(pubName.equals("Piw Paw")){pubName ="PiwPaw";}
        if(pubName.contains("Piwote")){pubName ="Piwoteka Narodowa";}

//        mDatabase.child("pubs").child(pubName).child("ocena"+0).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+1).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+2).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+3).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+4).setValue(0);
//

        getPubLocactions();
        getPubMenu();
        getPubPromotion();
        b2 =findViewById(R.id.back2);
        report =findViewById(R.id.report);
//        rate =findViewById(R.id.rate);
//        simpleRatingView=findViewById(R.id.rate);
         smileRating = (SmileRating) findViewById(R.id.rate);
        Log.w("mapa", "user to"+ mAuth.getCurrentUser().getEmail());


        if(mAuth.getCurrentUser()==null){
            Log.w("mapa", "gosc nie ocenia");

            smileRating.setIndicator(true);
        }else{
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("ocena " + pubName ).getValue(Integer.class)!=null){
                            Log.w("mapa", "oceniales juz");
                            smileRating.setSelectedSmile(dataSnapshot.child("ocena " + pubName ).getValue(Integer.class), false);
                            smileRating.setIndicator(true);
                        }
                        Log.w("mapa","stara ocena pubu" +" ocena " + pubName+" to :"+ dataSnapshot.child("ocena " + pubName ).getValue(Integer.class));
                    }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("mapa", "onCancelled pubs location", databaseError.toException());
                }
            });
        }



        b2.setOnClickListener(this);
        report.setOnClickListener(this);
        list= (ListView) findViewById(R.id.list);
        listPromotion= (ListView) findViewById(R.id.promotionList);


//        list.setOnClickListener(this);


        ArrayList<Beer> beers = new ArrayList<Beer>();
        for (Map.Entry<String, ArrayList<Beer>> entry : pubBeers.entrySet()) {
            if (getIntent().getExtras().getString("nazwa").equals(entry.getKey())) {
                beers.addAll(entry.getValue());
                Log.i("mapa", " wybrany pub popup "+ entry.getKey() );

            }
        }
        ArrayList<String> promos = new ArrayList<String>();
        for (Map.Entry<String, ArrayList<String>> entry : promotionsMap.entrySet()) {
            if (getIntent().getExtras().getString("nazwa").equals(entry.getKey())) {
                promos.addAll(entry.getValue());
                Log.i("mapa", " wybrany pub popup "+ entry.getKey() );

            }
        }


         adapterPromotion = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, promos);
        listPromotion.setAdapter(adapterPromotion);



        adapter = new ListAdapter(this, R.layout.row, beers);
        for(Beer beer : beers){
            Log.i("mapa", " pubs beers to adapter"+ beer.toString() );

        }

        list.setAdapter(adapter);

        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                count=0;
                if (!reselected) {
                    switch (smiley) {
                        case SmileRating.BAD:
                            Log.i("ocena", "Bad");
                            ocena = 1;
                            break;
                        case SmileRating.GOOD:
                            Log.i("ocena", "Good");
                            ocena = 3;
                            break;
                        case SmileRating.GREAT:
                            Log.i("ocena", "Great");
                            ocena = 4;
                            break;
                        case SmileRating.OKAY:
                            Log.i("ocena", "Okay");
                            ocena = 2;
                            break;
                        case SmileRating.TERRIBLE:
                            Log.i("ocena", "Terrible");
                            ocena = 0;
                            break;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    mDatabase.child("pubs").child(pubName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dsp) {

//                            int zeros=  dsp.child("ocena0").getValue(int.class);
//                            int ones=dsp.child("ocena1").getValue(int.class);
//                            int twos=dsp.child("ocena2").getValue(int.class);
//                            int threes=dsp.child("ocena3").getValue(int.class);
//                            int fours=dsp.child("ocena4").getValue(int.class);
//                            float count = zeros+ones+twos+threes+fours;
//                            float average = ((ones+(twos*2)+(threes*3)+(fours*4))/count);
//                            marks.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), average);
//                            marksCount.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), count);
                            count =(dsp.child("ocena" + ocena).getValue(int.class));
                            Log.i("mapa", " marks" + ((dsp.child("ocena" + String.valueOf(ocena)).getValue(int.class)) + 1));


//                            Log.i("mapa", " marks" + ((dsp.child("ocena" + ocena).getValue(int.class)) + 1));
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("mapa", "onCancelled pubs marks", databaseError.toException());
                        }
                    });
                    smileRating.setIndicator(true);

                    mDatabase.child("pubs").child(pubName).child("ocena" + ocena).setValue(count + 1);
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ocena " + pubName ).setValue(ocena);

//                mDatabase.child("pubs").child(getIntent().getExtras().getString("nazwa")).child("ocena"+ocena).setValue(email.getText().toString());
//                mDatabase.child("users").child(user.getUid()).child("type").setValue("user");
                }
            }
        });


//        Log.i("mapa", " rated as" +   simpleRatingView.getRating());
//
//        Log.i("mapa", " end of popup" );
//        simpleRatingView.setListener(new SimpleRatingView.OnRatingSelectedListener() {
//            @Override
//            public void onRatingSelected(SimpleRatingView.Rating rating) {
//                Log.i("mapa", " rated as"+  getString(rating.getStringRes()    ) +   simpleRatingView.getRating());
//
//            }
//        });



    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ( i==R.id.back2) {
            Log.i("login", ("chcemy zamknac popup"));
            finish();
        }
        if ( i==R.id.rate) {
            Log.i("login", ("chcemy ocenic uzytkownika"));
//            finish();
        }
        if ( i==R.id.report) {
            Log.i("login", ("chcemy zaraportowac pub"));
            Intent intent = new Intent(Popup.this,
                    Report.class);
            intent.putExtra("nazwa", pubName);
            startActivity(intent);


//            finish();
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
    public void getPubPromotion(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    ArrayList<String> promotions = new ArrayList<>();
                    for(DataSnapshot promoDSP : dsp.child("promotions").getChildren()){
                        promotions.add(promoDSP.getValue(String.class));


                    }
                    promotionsMap.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), promotions);

                }

                Log.i("mapa", " pubs promotions"+ promotions.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs promotions", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }
    public void getMarks(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    int zeros=  dsp.child("ocena0").getValue(int.class);
                    int ones=dsp.child("ocena1").getValue(int.class);
                    int twos=dsp.child("ocena2").getValue(int.class);
                    int threes=dsp.child("ocena3").getValue(int.class);
                    int fours=dsp.child("ocena4").getValue(int.class);
                    float count = zeros+ones+twos+threes+fours;
                    float average = ((ones+(twos*2)+(threes*3)+(fours*4))/count);
                    marks.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), average);
                    marksCount.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), count);

                }

                Log.i("mapa", " marks"+ marks.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs marks", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }

}
