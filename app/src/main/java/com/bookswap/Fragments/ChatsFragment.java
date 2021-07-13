package com.bookswap.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.Adapter.UserAdapter;
import com.bookswap.Model.Chatlist;
import com.bookswap.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bookswap.*;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter mUserAdapter;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private List<Chatlist> userList;

    ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate ( R.layout.fragment_chats, container, false );

        mProgressBar = view.findViewById ( R.id.progressBar );

        final ImageView img = view.findViewById ( R.id.img );
        final TextView txt = view.findViewById ( R.id.textView );

        img.setVisibility ( View.GONE);
        txt.setVisibility ( View.GONE );

        mProgressBar.setVisibility ( ProgressBar.VISIBLE );
        mProgressBar.setBackgroundColor ( Color.TRANSPARENT );

        recyclerView = view.findViewById ( R.id.recycler_view );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getContext () ) );

       firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

       userList = new ArrayList<> (  );

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.keepSynced ( true );

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ())
                {
                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        userList.add(chatlist);
                    }

                    chatList();
                }
                else {
                    // progressDialog.dismiss ();
                    mProgressBar.setVisibility ( ProgressBar.INVISIBLE );
                    //Toast.makeText ( getActivity (), "No Chats found", Toast.LENGTH_SHORT ).show ();
                    img.setVisibility ( View.VISIBLE );
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

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue( User.class);
                    for (Chatlist chatlist : userList){
                        assert user != null;
                        if (user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                mUserAdapter = new UserAdapter (getContext(), mUsers,true);
                recyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
