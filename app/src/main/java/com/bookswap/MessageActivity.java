package com.bookswap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.Adapter.MessageAdapter;
import com.bookswap.Model.Chat;
import com.bookswap.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 500;
    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    MessageAdapter messageAdapter;
    List<Chat> mChats;

    RecyclerView recyclerView;

    Intent intent;

    String userID="";
    String bookName="";
    String userName="";

    String currentDate="";
    String currentTime ="";

    String Sdate ="";
    String Stime = "";

    String dateTime = "";


    ImageButton btn_send;
    EditText text_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_message );

        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( "" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        toolbar.setNavigationOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //add this code
                //startActivity ( new Intent ( getApplicationContext (),MainActivity.class ).setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
                finish ();
            }
        } );

        recyclerView = findViewById ( R.id.recycler_view );
        recyclerView.setHasFixedSize ( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getApplicationContext () );
        linearLayoutManager.setStackFromEnd ( true );
        recyclerView.setLayoutManager ( linearLayoutManager );


        profile_image = findViewById ( R.id.profile_image );
        username = findViewById ( R.id.username );
        btn_send = findViewById ( R.id.btn_send );
        text_send = findViewById ( R.id.text_send );

        intent = getIntent ();
        userID = intent.getStringExtra ( "userID" );
        bookName = intent.getStringExtra ( "bookName" );
        userName = intent.getStringExtra ( "userName" );

        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();



        if (!bookName.equals (""))
        {
            text_send.setText ( "Hi "+userName+", I am interested in book "+bookName+"." );
        }



        /*

        text_send.addTextChangedListener ( new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString ().trim ().length () > 0)
                    {
                        btn_send.setVisibility ( View.VISIBLE );
                    }
                    else
                    {
                        btn_send.setVisibility ( View.GONE );
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        } );

         */

        text_send.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        btn_send.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText ().toString ().trim ();

                getDateTime ();

                dateTime =Sdate +" // " + Stime;

                if(!msg.equals ( "" ))
                {
                    sendMessage ( firebaseUser.getUid (),userID,msg,dateTime );
                }
                else {
                    Toast.makeText ( MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT ).show ();
                }
                text_send.setText ( "" );
            }
        } );


        reference = FirebaseDatabase.getInstance ().getReference ("Users").child (userID);

        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue ( User.class );
                username.setText ( Objects.requireNonNull ( user ).getUsername () );

                if(user.getImageURL ().equals ( "default" ))
                {
                    profile_image.setImageResource ( R.drawable.userphoto );
                }
                else
                {
                    Glide.with ( getApplicationContext () ).load ( user.getImageURL () ).placeholder ( R.drawable.new_loader ).into ( profile_image );
                }

                readMessage ( firebaseUser.getUid (),userID, user.getImageURL () );


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );


    }

    private void sendMessage(String sender,String receiver,String message,String dateTime)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ();

        HashMap<String,Object> hashMap = new HashMap<> (  );

        hashMap.put ( "sender",sender );
        hashMap.put ( "receiver",receiver );
        hashMap.put ( "message",message );
        hashMap.put ( "dateTime",dateTime );

        reference.child ( "Chats" ).push ().setValue ( hashMap );

        //add user to chat fragment

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userID);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userID)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());


    }


    private void readMessage(String myID,String userID,String imageURL)
    {
        mChats = new ArrayList<> (  );

        reference = FirebaseDatabase.getInstance ().getReference ("Chats");
        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear ();

                for (DataSnapshot dataSnapshot:snapshot.getChildren ())
                {
                    Chat chat = dataSnapshot.getValue (Chat.class);

                    if(chat.getSender ().equals ( myID ) && chat.getReceiver ().equals ( userID ) ||
                         chat.getReceiver ().equals ( myID ) && chat.getSender ().equals ( userID ))
                    {
                        mChats.add ( chat );
                    }

                    messageAdapter = new MessageAdapter ( MessageActivity.this,mChats,imageURL );
                    recyclerView.setAdapter ( messageAdapter );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


    private void is_online(String is_online)
    {
        reference  = FirebaseDatabase.getInstance ().getReference ("Users").child ( firebaseUser.getUid () );

        HashMap<String,Object> hashMap = new HashMap<> (  );
        hashMap.put ( "is_online",is_online );
        reference.updateChildren ( hashMap );
    }

    @Override
    protected void onResume() {
        super.onResume ();
        is_online ( "online" );
    }

    @Override
    protected void onPause() {
        super.onPause ();
        is_online ( "offline" );
    }

    public void getDateTime()
    {
        //time and date
        currentDate = new SimpleDateFormat ("dd-MM-yyyy", Locale.getDefault()).format(new Date ());

        String date[] = currentDate.split ( "-" );

        String day = date[0];
        String temp = date[1];
        String month="";
        String year = date[2];

        month = getMonth ( temp );


        Sdate = day+" "+month+" "+year;

        currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());
        String arr[] = currentTime.split(":");
        String am_pm[] = arr[2].split(" ");

        Stime = arr[0]+":"+arr[1]+" "+am_pm[1].toUpperCase ();

//        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
//
//        String time[] = currentTime.split ( ":" );
//
//        String hour = time[0];
//        String am_pm = "";
//
//        int hr = Integer.parseInt ( hour );
//        int final_hr;
//
//        if(hr > 12)
//        {
//            am_pm = "PM";
//            final_hr = hr - 12;
//        }
//        else if(hr == 12)
//        {
//            am_pm = "PM";
//            final_hr = 12;
//        }
//        else {
//            am_pm = "AM";
//            final_hr = hr+12;
//        }
//
//        Stime = String.valueOf ( final_hr + ":"+ time[1]+" "+ am_pm);
    }

    public String getMonth(String temp)
    {
        String month="";
        if(temp.equals ( "01" ))
        {
            month = "JAN";
        }
        else if(temp.equals ( "02" ))
        {
            month = "FEB";
        }
        else if(temp.equals ( "03" ))
        {
            month = "MAR";
        }
        else if(temp.equals ( "04" ))
        {
            month = "APL";
        }else if(temp.equals ( "05" ))
        {
            month = "MAY";
        }else if(temp.equals ( "06" ))
        {
            month = "JUN";
        }else if(temp.equals ( "07" ))
        {
            month = "JULY";
        }else if(temp.equals ( "08" ))
        {
            month = "AUG";
        }else if(temp.equals ( "09" ))
        {
            month = "SEPT";
        }else if(temp.equals ( "10" ))
        {
            month = "OCT";
        }else if(temp.equals ( "11" ))
        {
            month = "NOV";
        }else if(temp.equals ( "12" ))
        {
            month = "DEC";
        }

        return month;
    }
}
