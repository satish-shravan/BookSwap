package com.bookswap.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bookswap.Adapter.ViewPageAdapter;
import com.bookswap.MainActivity;
import com.bookswap.Model.BookPost;
import com.bookswap.Pager.VerticalViewPager;
import com.bookswap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    //firebase
    private FirebaseUser firebaseUser;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseRef;

    BookPost bookPost;

    ProgressDialog progressDialog;

    ProgressBar mProgressBar;

    Context context;

    List<BookPost> mBookPosts = new ArrayList<> ();

    public static final double MAX_ALLOWED_DISTANCE = 50.0;

    double myDistance,myLatitude,myLongitude,userLatitude,userLongitude;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_home, container, false );

        mProgressBar = view.findViewById ( R.id.progressBar );

        //initialise auth
        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

        final ImageView img = view.findViewById ( R.id.img );
        final TextView txt = view.findViewById ( R.id.textView );

        final VerticalViewPager verticalViewPager = view.findViewById ( R.id.verticalpager );

        mProgressBar.setVisibility ( ProgressBar.VISIBLE );
        mProgressBar.setBackgroundColor ( Color.TRANSPARENT );

        img.setVisibility ( View.VISIBLE);
        txt.setVisibility ( View.VISIBLE );

        String myUID = firebaseUser.getUid ().toString ();

        //firebase

        mDatabaseRef = FirebaseDatabase.getInstance ().getReference ().child ("BookPosts");
        mDatabaseRef.keepSynced ( true );

        mDatabaseRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if(snapshot.exists ())
                {
                    //progressDialog.dismiss ();
                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                    for(DataSnapshot post : snapshot.getChildren ())
                    {
                        bookPost = post.getValue (BookPost.class);

                        //logic to add nearby bookposts
                        myLatitude =Double.parseDouble (MainActivity.UserData.LATITUDE);
                        myLongitude = Double.parseDouble (MainActivity.UserData.LONGITUDE);
                        userLatitude = Double.parseDouble ( bookPost.getLatitude () );
                        userLongitude = Double.parseDouble ( bookPost.getLongitude () );

                        myDistance = distance(myLatitude,myLongitude,userLatitude,userLongitude);
                        myDistance = myDistance + myDistance/10;


                        if(!bookPost.getUID ().equals ( firebaseUser.getUid () ) && myDistance < MAX_ALLOWED_DISTANCE)
                        {
                            mBookPosts.add ( bookPost );
                            img.setVisibility ( View.GONE);
                            txt.setVisibility ( View.GONE );
                        }
                    }

                    //reverse the list to fetch new posts firsts
                    Collections.reverse ( mBookPosts );

                    verticalViewPager.setAdapter ( new ViewPageAdapter ( getActivity (),mBookPosts ) );
                }
                //else
                    {
                    // progressDialog.dismiss ();
//                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
//                    //Toast.makeText ( getActivity (), "No posts found", Toast.LENGTH_SHORT ).show ();
//                    img.setVisibility ( View.VISIBLE );
//                    txt.setVisibility ( View.VISIBLE );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //progressDialog.dismiss ();
                mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                //Toast.makeText ( getActivity (), "Something went wrong", Toast.LENGTH_SHORT ).show ();
            }
        } );

        return view;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);

        // distance in miles
        dist = dist * 60 * 1.1515;

        //convert into kilometer
        dist = dist * 1.609344;

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
