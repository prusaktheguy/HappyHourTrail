package com.example.prusak.happyhourtrail;

import android.app.Activity;
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

public class Report extends Activity implements View.OnClickListener  {

    private Button b2;
    private EditText reportText;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    static public int ocena;
    static public String pubName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

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
        b2 =findViewById(R.id.reportButton);
        reportText =findViewById(R.id.editText);
        Log.w("mapa", "user to"+ mAuth.getCurrentUser().getEmail());





        b2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ( i==R.id.reportButton) {
            Log.i("login", ("chcemy zaraportowac pub"));
            if(mAuth.getCurrentUser()==null){
                Log.i("mapa", "gosc raportuje");
                String key = mDatabase.child("reports").push().getKey();
                mDatabase.child("reports").child(pubName).child("guests").child(key).child("report").setValue(reportText.getText().toString());

            }else{
                Log.i("mapa", "user raportuje");

                mDatabase.child("reports").child(pubName).child(mAuth.getCurrentUser().getUid()).child("report").setValue(reportText.getText().toString());
                mDatabase.child("reports").child(pubName).child(mAuth.getCurrentUser().getUid()).child("user").setValue(mAuth.getCurrentUser().getEmail());


            }



            finish();
        }
    }




}
