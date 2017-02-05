package com.prit.ark.xpenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    public final String LOG_TAG=SplashActivity.class.getSimpleName();
    int RC_SIGN_IN=100;
    FirebaseAuth mAuth;
    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
    FirebaseUser mUser;
    String mEncodedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            login();
        }
        else{
//            setContentView(R.layout.activity_splash);
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                    .setLogo(R.drawable.logo)
                    .setTheme(R.style.NoActionBar)
                    .build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
           if(resultCode==RESULT_OK){
                Log.d(LOG_TAG, "Login Successful");
                mUser=FirebaseAuth.getInstance().getCurrentUser();
                mEncodedEmail=Utils.encodeEmail(mUser.getEmail());
                setFirstUser();
                login();
            }
            else if(resultCode==RESULT_CANCELED){
                Utils.displayMessage(this, getString(R.string.login_failed));
            }
            else{
                Utils.displayMessage(this, getString(R.string.unknown_response));
            }
        }
    }

    void login(){
        Intent intent=new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void setFirstUser(){
        final DatabaseReference userRef=mRef.child(Constants.FIREBASE_USERS).child(mEncodedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, dataSnapshot.toString());
                if(dataSnapshot.getValue()==null){
                    Log.d(LOG_TAG, "New User Created");
                    User user=new User(mUser.getDisplayName(), mEncodedEmail);
                    if (mUser.getPhotoUrl()!=null) {
                        Log.d(LOG_TAG, "Null");
                        user.setPhoto(mUser.getPhotoUrl().toString());
                    }
                    userRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, databaseError.getMessage());
            }
        });
    }
}
