package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener    {

    private Button loginButton;
    private Button guestButton;
    private Button signupButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Log.i("menu", ("jestesmy w maine"));
        loginButton = findViewById(R.id.loginButton);
        email = findViewById(R.id.emailForm);
        password = findViewById(R.id.passwordForm);
        signupButton =findViewById(R.id.signup_Button);
        guestButton =findViewById(R.id.guest_button);

        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        guestButton.setOnClickListener(this);






//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.loginButton) {
            Log.i("login", ("chcemy być w menu_admin zalogowani"));
            logIn();
        }
        if (i == R.id.signup_Button) {
            Log.i("login", ("chcemy być w rejestracji"));
            signup();
        }
        if (i == R.id.guest_button) {
            Log.i("login", ("chcemy być w menu_admin niezalogowani"));
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
    }

    public void signup(){
        startActivity(new Intent(MainActivity.this, SignUpAcitivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseAuth.getInstance().signOut();
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }

    }



    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.child("type").getValue(String.class);
                Log.i("logowanie", type);

                if(type.equals("admin")){
                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                }
                if(type.equals("user")){
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                }
                if(type.equals("pub")){
                   String pubName = dataSnapshot.child("pubName").getValue(String.class);
                    Intent i = new Intent(MainActivity.this,
                            MenuActivityPubWorker.class);
                    i.putExtra("nazwa", pubName);
                    startActivity(i);

//                    startActivity(new Intent(MainActivity.this, MenuActivityPubWorker.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("logowanie", "onCancelled", databaseError.toException());
            }
        });
    //    startActivity(new Intent(MainActivity.this, MenuActivity.class));
        finish();
    }
    private void logIn() {
        Log.d("logowanie", "signing in");

        boolean validated  = true;
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.error_required));
            validated = false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(getString(R.string.error_required));
            validated = false;
        } else {
            password.setError(null);
        }

        if(!validated){
            return;
        }

        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("logowanie", "signIn:onComplete:" + task.isSuccessful());
//                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(MainActivity.this,
                                    "FirebaseAuthInvalidUserException", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(MainActivity.this,
                                    "FirebaseAuthInvalidCredentialsException", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

