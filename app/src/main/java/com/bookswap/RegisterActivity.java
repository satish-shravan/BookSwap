package com.bookswap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username,email,password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );

        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( "Register" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

        auth = FirebaseAuth.getInstance ();

        username = findViewById ( R.id.username );
        email = findViewById ( R.id.email );
        password = findViewById ( R.id.password );

        btn_register = findViewById ( R.id.btn_register );

        progressDialog = new ProgressDialog ( this );

        btn_register.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText ().toString ();
                String txt_email = email.getText ().toString ();
                String txt_password = password.getText ().toString ();

                if(TextUtils.isEmpty ( txt_username ) || TextUtils.isEmpty ( txt_email ) || TextUtils.isEmpty ( txt_password ))
                {
                    Toast.makeText ( RegisterActivity.this, "All fiels are required", Toast.LENGTH_SHORT ).show ();

                }
                else if(txt_password.length () <6)
                {
                    Toast.makeText ( RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT ).show ();
                }
                else
                {
                    register ( txt_username,txt_email,txt_password);
                }
            }
        } );



    }

    private  void register(final String username, String email, String password)
    {
        progressDialog.setTitle ( "Register" );
        progressDialog.setMessage ( "Wait a second" );
        progressDialog.show ();
        progressDialog.setCancelable ( false );

            auth.createUserWithEmailAndPassword ( email, password )
                    .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful ())
                            {
                                progressDialog.dismiss ();
                                FirebaseUser firebaseUser = auth.getCurrentUser ();
                                String userid = firebaseUser.getUid ();

                                reference = FirebaseDatabase.getInstance ().getReference ("Users").child ( userid );

                                HashMap<String,String> hashMap = new HashMap<> (  );

                                hashMap.put ( "id",userid );
                                hashMap.put ( "username",username );
                                hashMap.put ( "imageURL","default" );
                                hashMap.put ( "is_online","offline" );
                                hashMap.put ( "search",username.toLowerCase () );
                                hashMap.put ( "address","" );
                                hashMap.put ( "city","" );
                                hashMap.put ( "latitude","" );
                                hashMap.put ( "longitude","" );


                                reference.setValue ( hashMap ).addOnCompleteListener ( new OnCompleteListener<Void> () {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful ())
                                        {
                                            Intent intent = new Intent ( RegisterActivity.this,MainActivity.class );
                                            intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                            startActivity ( intent );
                                            finish ();
                                        }
                                    }
                                } );
                            }
                            else
                            {
                                progressDialog.dismiss ();
                                Toast.makeText ( RegisterActivity.this, "You can't register with this email and password", Toast.LENGTH_SHORT ).show ();
                            }
                        }
                    } );
    }
}
