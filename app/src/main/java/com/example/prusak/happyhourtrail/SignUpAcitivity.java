package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Prusak on 2018-01-18.
 */

public class SignUpAcitivity extends AppCompatActivity implements View.OnClickListener    {


    private Button signupButton;
    private String type;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText passwordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Log.i("main", ("jestesmy w signupie"));
        email = findViewById(R.id.email_form);
        password = findViewById(R.id.password_form);
        passwordRepeat = findViewById(R.id.repeat_password_form);
        signupButton =findViewById(R.id.sign_up_Button);
        signupButton.setOnClickListener(this);


    }


    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_up_Button) {
            Log.i("logowanie", ("chcemy się zarejestrować"));
            signup();
        }
    }

    public void signup(){

        if(! validate()  ){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("logowanie", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("users").child(user.getUid()).child("email").setValue(email.getText().toString());
                            mDatabase.child("users").child(user.getUid()).child("type").setValue("user");
                            Toast.makeText(SignUpAcitivity.this, "Zarejestrowano", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpAcitivity.this, MapActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("logowanie", "createUserWithEmail:failure", task.getException());
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(SignUpAcitivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpAcitivity.this, MainActivity.class));

                        }

                        // ...
                    }
                });


    }

    public boolean validate(){
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})"; //one digit, one lowercase, one uppercase, 6-20
        boolean validated = true;

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(getString(R.string.error_required));
            validated = false;
        } else if (!Pattern.compile(PASSWORD_PATTERN).matcher(password.getText().toString()).matches()) {
            password.setError(getString(R.string.error_password_invalid));
            validated = false;
        } else if (TextUtils.isEmpty(passwordRepeat.getText().toString())) {
            passwordRepeat.setError(getString(R.string.error_required));
            validated = false;
        } else if (!Pattern.compile(PASSWORD_PATTERN).matcher(passwordRepeat.getText().toString()).matches()) {
            passwordRepeat.setError(getString(R.string.error_password_invalid));
            validated = false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.error_required));
            validated = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError(getString(R.string.error_email_invalid));
            validated = false;
        }else if (!passwordRepeat.getText().toString().equals(password.getText().toString())) {
            passwordRepeat.setError(getString(R.string.error_password_not_same));
            validated = false;
        }

        return validated;
    }



    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.child("type").getValue(String.class);
                Log.i("logowanie", type);

                if(type.equals("admin")){
                    startActivity(new Intent(SignUpAcitivity.this, MenuActivity.class));
                }
                if(type.equals("user")){
                    startActivity(new Intent(SignUpAcitivity.this, MapActivity.class));
                }
                if(type.equals("pub")){
                    startActivity(new Intent(SignUpAcitivity.this, MenuActivityPubWorker.class));
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





}
