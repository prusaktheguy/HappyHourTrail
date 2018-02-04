package com.example.prusak.happyhourtrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Prusak on 2018-01-21.
 */

public class BeerEdit extends Activity implements View.OnClickListener  {

    private Button b2;
    private Button delete;
    private EditText beerNazwaEdit;
    private EditText beerBrowarEdit;
    private EditText beerCenaEdit;
    private EditText beerGatunekEdit;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    static public String originalName;
    static public String pubName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        pubName = getIntent().getExtras().getString("nazwa");

//        mDatabase.child("pubs").child(pubName).child("ocena"+0).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+1).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+2).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+3).setValue(0);
//        mDatabase.child("pubs").child(pubName).child("ocena"+4).setValue(0);
//

//        getPubLocactions();
//        getPubMenu();
//        getPubPromotion();
        b2 =findViewById(R.id.ok);
        delete =findViewById(R.id.delete);
        beerNazwaEdit =findViewById(R.id.name);
        beerBrowarEdit =findViewById(R.id.browar);
        beerCenaEdit =findViewById(R.id.cena);
        beerGatunekEdit =findViewById(R.id.gatunek);
        if(! getIntent().getExtras().getString("nazwaPiwa").isEmpty()){
            beerNazwaEdit.setText(getIntent().getExtras().getString("nazwaPiwa"));
            originalName=getIntent().getExtras().getString("nazwaPiwa");
        }
        if(! getIntent().getExtras().getString("browar").isEmpty()){
            beerBrowarEdit.setText(getIntent().getExtras().getString("browar"));
        }
        if(! getIntent().getExtras().getString("cena").isEmpty()){
            beerCenaEdit.setText(getIntent().getExtras().getString("cena"));
        }
        if(! getIntent().getExtras().getString("gatunek").isEmpty()){
            beerGatunekEdit.setText(getIntent().getExtras().getString("gatunek"));
        }
//        Log.w("mapa", "user to"+ mAuth.getCurrentUser().getEmail());





        b2.setOnClickListener(this);
        delete.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ( i==R.id.ok) {
            Log.i("pubWorker", ("edycja/tworzenie piwa"));
            if(mAuth.getCurrentUser()==null){
                Log.i("mapa", "gosc nie powinien tu byc");
                Intent intent = new Intent(this,
                        EditPubOffer.class);
                intent.putExtra("nazwa", pubName);
                startActivity(intent);
            }else{
                Log.i("mapa", "user dodaje promocje");
                if(originalName!=null && !originalName.equals(beerNazwaEdit.getText().toString())){
                    mDatabase.child("pubs").child(pubName).child("beers").child(originalName).setValue(null);
                }
                mDatabase.child("pubs").child(pubName).child("beers").child(beerNazwaEdit.getText().toString()).child("nazwa").setValue(beerNazwaEdit.getText().toString());
                mDatabase.child("pubs").child(pubName).child("beers").child(beerNazwaEdit.getText().toString()).child("browar").setValue(beerBrowarEdit.getText().toString());
                mDatabase.child("pubs").child(pubName).child("beers").child(beerNazwaEdit.getText().toString()).child("cena").setValue(beerCenaEdit.getText().toString());
                mDatabase.child("pubs").child(pubName).child("beers").child(beerNazwaEdit.getText().toString()).child("gatunek").setValue(beerGatunekEdit.getText().toString());
//                mDatabase.child("reports").child(pubName).child(mAuth.getCurrentUser().getUid()).child("user").setValue(mAuth.getCurrentUser().getEmail());


            }



            Intent intent = new Intent(this,
                    EditPubOffer.class);
            intent.putExtra("nazwa", pubName);

            startActivity(intent);        }
        if ( i==R.id.delete) {
            Log.i("pubWorker", ("usuwanie piwa"));
            mDatabase.child("pubs").child(pubName).child("beers").child(originalName).setValue(null);
            Intent intent = new Intent(this,
                    EditPubOffer.class);
            intent.putExtra("nazwa", pubName);

            startActivity(intent);        }
    }




}
