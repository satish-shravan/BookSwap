package com.bookswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    Button login,register;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_start );

        login = findViewById ( R.id.login );
        register = findViewById ( R.id.register );

        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity(new Intent ( StartActivity.this,LoginActivity.class ) );
            }
        } );

        register.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity(new Intent ( StartActivity.this,RegisterActivity.class ) );
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        auth = FirebaseAuth.getInstance ();
        if(auth.getCurrentUser () != null)
        {
            startActivity ( new Intent ( StartActivity.this,MainActivity.class ) );
            finish ();
        }
    }
}
