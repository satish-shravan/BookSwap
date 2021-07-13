package com.bookswap.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bookswap.Model.User;
import com.bookswap.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username;

    private DatabaseReference reference;
    private FirebaseUser fuser;

    private StorageReference storageReference;
    private  static final int GALLARY_PICK = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);

        TabLayout tabLayout = view.findViewById ( R.id.tab_layout );
        ViewPager viewPager = view.findViewById ( R.id.viewpager );

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter ( getChildFragmentManager () );

        viewPagerAdapter.addFragment ( new UserPostsFragment (),"Posts" );
        //viewPagerAdapter.addFragment ( new UserHistoryFragment (),"History" );

        viewPager.setAdapter ( viewPagerAdapter );
        tabLayout.setupWithViewPager ( viewPager );

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null) {
                    return;
                }
                User user = dataSnapshot.getValue( User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.userphoto);
                } else {
                    Glide.with(getActivity ()).load(user.getImageURL()).placeholder ( R.drawable.new_loader ).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        return view;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super ( fm );
            this.fragments = new ArrayList<> (  );
            this.titles = new ArrayList<> (  );

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get ( position );
        }

        @Override
        public int getCount() {
            return fragments.size ();
        }

        public void  addFragment(Fragment fragment,String title)
        {
            fragments.add ( fragment );
            titles.add ( title );
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get ( position );
        }
    }

    private void openImage() {
        Intent gallaryintent = new Intent (  );
        gallaryintent.setType ( "image/*" );
        gallaryintent.setAction ( Intent.ACTION_GET_CONTENT );
        startActivityForResult ( Intent.createChooser ( gallaryintent,"SELECT IMAGE"),GALLARY_PICK );
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == GALLARY_PICK && resultCode == RESULT_OK)
        {
            Uri Imageuri = data.getData ();
//            CropImage.activity( Imageuri )
//                    .setAspectRatio ( 4,4 )
//                    .setGuidelines( CropImageView.Guidelines.ON)
//                    .start(getContext (),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap ( getActivity ().getContentResolver (),imageUri );
                } catch (IOException e) {
                    e.printStackTrace ();
                }

                image_profile.setImageBitmap ( bitmap );

               // uploadData ();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadData()
    {
        //check filepath i.e image is selected or not

        if(imageUri != null)
        {
            final ProgressDialog progressDialog =new ProgressDialog ( getContext () );
            progressDialog.setTitle ( "Uploading \n Please wait ..." );
            progressDialog.show ();

            StorageReference store;

            //create the child from the storagereference path
            store = storageReference.child ( System.currentTimeMillis () +"."+ getFileExtension ( imageUri ) );



            // putFile() method is used to add data to firebase storage
            store.putFile ( imageUri ).addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // disable the progress bar
                    progressDialog.dismiss ();

                    // get the URL of image to store in database
                    taskSnapshot.getMetadata ().getReference ().getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadURl = String.valueOf ( uri );

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", ""+downloadURl);
                            reference.updateChildren(map);


                        }
                    } );

                }
            } ).addOnFailureListener ( new OnFailureListener () {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText ( getContext (), "Failed..try again", Toast.LENGTH_SHORT ).show ();
                }
            } ).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred () / taskSnapshot.getTotalByteCount ());
                    progressDialog.setMessage ( "Uploaded  "+ ((int)(progress)) + "%" +" \nit takes few seconds..." );
                }
            } );


        }
        else {
            Toast.makeText ( getContext (), "Image selected is invalid or no image is selected", Toast.LENGTH_SHORT ).show ();
        }

    }

     */

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_PICK && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }




}
