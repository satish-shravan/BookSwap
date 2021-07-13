package com.bookswap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText username,email,password;
    Button btn_login;
    TextView forgot_password;

    FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( "Login" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

        auth = FirebaseAuth.getInstance ();

        email = findViewById ( R.id.email );
        password = findViewById ( R.id.password );

        btn_login = findViewById ( R.id.btn_login );

        forgot_password = findViewById ( R.id.forgot_password );

        forgot_password.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent ( getApplicationContext (),ResetPasswordActivity.class ) );
            }
        } );

        progressDialog = new ProgressDialog ( this );

        btn_login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText ().toString ();
                String txt_password = password.getText ().toString ();

                if(TextUtils.isEmpty ( txt_email ) || TextUtils.isEmpty ( txt_password ))
                {
                    Toast.makeText ( LoginActivity.this, "All fiels are required", Toast.LENGTH_SHORT ).show ();
                }
                else
                {
                    progressDialog.setTitle ( "Log in" );
                    progressDialog.setMessage ( "Wait a second" );
                    progressDialog.show ();
                    progressDialog.setCancelable ( false );
                    auth.signInWithEmailAndPassword ( txt_email,txt_password)
                            .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful ())
                                    {
                                        progressDialog.dismiss ();
                                        Intent intent = new Intent ( LoginActivity.this,MainActivity.class );
                                        intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                        startActivity ( intent );
                                        finish ();
                                    }
                                    else
                                    {
                                        progressDialog.dismiss ();
                                        Toast.makeText ( LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT ).show ();
                                    }
                                }
                            } );
                }
            }
        } );


    }
}
