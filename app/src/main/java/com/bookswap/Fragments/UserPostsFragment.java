package com.bookswap.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bookswap.Adapter.UserAdapter;
import com.bookswap.Adapter.UserPostAdapter;
import com.bookswap.Model.BookPost;
import com.bookswap.Model.Chatlist;
import com.bookswap.Model.User;
import com.bookswap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserPostsFragment extends Fragment {

    public UserPostsFragment() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;

    private UserPostAdapter mUserPostAdapter;
    private List<BookPost> mBookPosts;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    ProgressBar mProgressBar;

    private int book_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate ( R.layout.fragment_user_posts, container, false );

        mProgressBar = view.findViewById ( R.id.progressBar );

        final TextView txt = view.findViewById ( R.id.textView );

        txt.setVisibility ( View.GONE );

        mProgressBar.setVisibility ( ProgressBar.VISIBLE );
        mProgressBar.setBackgroundColor ( Color.TRANSPARENT );

        recyclerView = view.findViewById ( R.id.recycler_view );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getContext () ) );

        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

        mBookPosts = new ArrayList<> (  );

        reference = FirebaseDatabase.getInstance().getReference("BookPosts");
        reference.keepSynced ( true );

        reference.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ())
                {
                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                    mBookPosts.clear();
                    book_count =  0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        BookPost bookPost = snapshot.getValue(BookPost.class);

                        if(bookPost.getUID ().equals ( firebaseUser.getUid () ))
                        {
                            mBookPosts.add ( bookPost );
                            //book_count+=1;
                        }

                        Collections.reverse ( mBookPosts );
                        mUserPostAdapter =  new UserPostAdapter ( getContext (),mBookPosts );
                        recyclerView.setAdapter (mUserPostAdapter);
                    }

                    if(mBookPosts.size () == 0)
                    {
                        mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                        txt.setVisibility ( View.VISIBLE );
                    }
                }
                else {
                    // progressDialog.dismiss ();
                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                    //Toast.makeText ( getActivity (), "No Posts found", Toast.LENGTH_SHORT ).show ();
                    txt.setVisibility ( View.VISIBLE );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
            }
        });

        return view;
    }
}
