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

public class PromoEdit extends Activity implements View.OnClickListener  {

    private Button b2;
    private EditText promotionText;
    private EditText promotionTitleText;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    static public String originalpromoTitle;
    static public String pubName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_promo);

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
        b2 =findViewById(R.id.acceptButton);
        promotionText =findViewById(R.id.editText);
        promotionTitleText =findViewById(R.id.promoTitle);
        if(! getIntent().getExtras().getString("promo").isEmpty()){
            promotionText.setText(getIntent().getExtras().getString("promo"));
        }
        if(! getIntent().getExtras().getString("promoTitle").isEmpty()){
            originalpromoTitle=getIntent().getExtras().getString("promoTitle");
            promotionTitleText.setText(originalpromoTitle);
        }
//        Log.w("mapa", "user to"+ mAuth.getCurrentUser().getEmail());





        b2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ( i==R.id.acceptButton) {
            Log.i("pubWorker", ("edycja/tworzenie promocji"));
            if(mAuth.getCurrentUser()==null){
                Log.i("mapa", "gosc nie powinien tu byc");
                Intent intent = new Intent(this,
                        MainActivity.class);

                startActivity(intent);
            }else{
                Log.i("mapa", "user dodaje promocje");
                if(originalpromoTitle!=null && !originalpromoTitle.equals(promotionTitleText.getText().toString())){
                    mDatabase.child("pubs").child(pubName).child("promotions").child(originalpromoTitle).setValue(null);
                }
                mDatabase.child("pubs").child(pubName).child("promotions").child(promotionTitleText.getText().toString()).setValue(promotionText.getText().toString());
//                mDatabase.child("reports").child(pubName).child(mAuth.getCurrentUser().getUid()).child("user").setValue(mAuth.getCurrentUser().getEmail());


            }



            Intent intent = new Intent(this,
                    MenuActivityPubWorker.class);
           intent.putExtra("nazwa", pubName);
//            intent.putExtra("promoTitle", "");
//            intent.putExtra("promo", "");
            startActivity(intent);        }
    }




}
