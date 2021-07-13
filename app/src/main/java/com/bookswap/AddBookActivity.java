package com.bookswap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bookswap.Model.BookPost;
import com.bookswap.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddBookActivity extends AppCompatActivity {

    private TextInputLayout bookName,bookDesc,location,bookTags;

    final int PICK_CODE=1;

    String currentDate="";
    String currentTime ="";

    Button choooseImage,uploadPost;
    ImageView prevImage;

    private Uri filepath; // store the path of image we are uploading

    CircleImageView profile_image;
    TextView username;


    String profile_url = "";

    String UserName = "";

    ProgressDialog progressDialog;
    //firebase;
    private FirebaseUser firebaseUser;
    private StorageReference mStorageRef;
    private DatabaseReference reference;


    String Sdate ="";
    String Stime = "";

    Intent intent;
     String latitude = "";
     String longitude = "";
     String address="";
     String city="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_add_book );

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

        getDateTime();

        intent = getIntent ();
        address = intent.getStringExtra ( "address" );
        city = intent.getStringExtra ( "city");
        latitude =  intent.getStringExtra ( "latitude" );
        longitude = intent.getStringExtra ( "longitude" );


        profile_image = findViewById ( R.id.profile_image );
        username = findViewById ( R.id.username );

        choooseImage = findViewById ( R.id.chooseImage );
        prevImage = findViewById ( R.id.prev_img );
        uploadPost = findViewById ( R.id.upload_post );


        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        bookName = findViewById ( R.id.bookName );
        bookDesc = findViewById ( R.id.bookDesc );
        location = findViewById ( R.id.location );
        bookTags = findViewById ( R.id.bookTags );

        final String UID =firebaseUser.getUid ();

        //firebase
        reference = FirebaseDatabase.getInstance ().getReference ("Users");

        reference.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists ()) {
                    User user = snapshot.child ( UID ).getValue ( User.class );
                    assert user != null;
                    profile_url = user.getImageURL ().toString ();
                    UserName = user.getUsername ().toString ();

                    username.setText ( Objects.requireNonNull ( user ).getUsername () );

                    if(user.getImageURL ().equals ( "default" ))
                    {
                        profile_image.setImageResource ( R.drawable.userphoto );
                    }
                    else
                    {
                        Glide.with ( getApplicationContext () ).load ( user.getImageURL () ).placeholder ( R.drawable.new_loader ).into ( profile_image );
                    }
                }
                else
                {
                    //Toast.makeText ( getApplicationContext (), "Something went wrong", Toast.LENGTH_SHORT ).show ();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );


        reference = FirebaseDatabase.getInstance ().getReference ().child ( "BookPosts" );

        mStorageRef = FirebaseStorage.getInstance ().getReference ().child ( "Users" ).child ( Objects.requireNonNull ( firebaseUser.getEmail ()) );

        // choose the image you want to upload to firebase
        choooseImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                showFileChooser ();
            }
        } );

        // upload the images into firebase
        uploadPost.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        } );

    }


    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == PICK_CODE && resultCode == RESULT_OK)
        {
            assert data != null;
            Uri Imageuri = data.getData ();
            CropImage.activity( Imageuri )
                    .setAspectRatio ( 2,2 )
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filepath = result.getUri ();

                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap ( getApplicationContext ().getContentResolver (),filepath );
                } catch (IOException e) {
                    e.printStackTrace ();
                }

                prevImage.setImageBitmap ( bitmap );

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == PICK_CODE && resultCode == RESULT_OK && data !=null && data.getData () != null)
        {
            filepath = data.getData ();


            //shows image which you are uploading to firebase
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap (getApplicationContext ().getContentResolver (),filepath );
            } catch (IOException e) {
                e.printStackTrace ();
            }

            prevImage.setImageBitmap ( bitmap );

        }
    }

    void showFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*"); // * means shows all types images from your phone (jpeg,png,etc)
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_CODE);
    }

    void uploadData()
    {
        //check filepath i.e image is selected or not

        if(filepath != null)
        {
            final ProgressDialog progressDialog =new ProgressDialog ( this );
            progressDialog.setTitle ( "Uploading..." );
            progressDialog.setCancelable ( false );
            progressDialog.show ();

            StorageReference store;

            //create the child from the storagereference path
            store = mStorageRef.child ( "BookPosts/" +System.currentTimeMillis () +"."+getFileExtension ( filepath ) );



            // putFile() method is used to add data to firebase storage
            store.putFile ( filepath ).addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // disable the progress bar
                    progressDialog.dismiss ();

                    // get the URL of image to store in database
                    taskSnapshot.getMetadata ().getReference ().getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                        @Override
                        public void onSuccess(Uri uri) {
                            String book_name = bookName.getEditText ().getText ().toString ();
                            String book_desc = bookDesc.getEditText ().getText ().toString ();
                            String book_tags = bookTags.getEditText ().getText ().toString ();
                            String mDate = Sdate;
                            String mTime = Stime;
                            String book_image = String.valueOf ( uri );
                            String mUID = firebaseUser.getUid ();
                            String profileURL = profile_url;
                            String userName = UserName;
                            String mLocation = address+", "+city+"";
                            String mLatitude = String.valueOf (latitude);
                            String mLongitude = String.valueOf (longitude);


                            final BookPost bookPost = new BookPost ( book_name,book_desc,mLocation,mLatitude,mLongitude,book_tags,book_image,profileURL,userName,mDate,mTime,mUID );
                            //uploads the data using setValue() method

                            reference.push ().setValue ( bookPost ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText ( getApplicationContext (), "Uploaded Successfull :) ", Toast.LENGTH_SHORT ).show ();
                                    filepath = null;
                                    prevImage.setImageResource ( R.drawable.ic_photo );
                                    bookName.getEditText ().setText ( "" );
                                    bookDesc.getEditText ().setText ( "" );
                                    bookTags.getEditText ().setText ( "" );
                                    location.getEditText ().setText ( "" );
                                }
                            } );


                        }
                    } );

                }
            } ).addOnFailureListener ( new OnFailureListener () {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText ( getApplicationContext (), "Failed...try again", Toast.LENGTH_SHORT ).show ();
                }
            } ).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred () / taskSnapshot.getTotalByteCount ());
                    progressDialog.setMessage ( "it takes few seconds..." );
                }
            } );


        }
        else {
            Toast.makeText ( getApplicationContext (), "Image selected is invalid or no image is selected", Toast.LENGTH_SHORT ).show ();
        }

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getApplicationContext ().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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


//        currentTime = new SimpleDateFormat("hh:mm:ss ", Locale.getDefault()).format(new Date());
//
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
//            final_hr = hr;
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
