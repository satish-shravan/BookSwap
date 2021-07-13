package com.bookswap.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.Adapter.UserAdapter;
import com.bookswap.Model.User;
import com.bookswap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;

    private List<User> mUsers;

    private EditText search_user;

    private  FirebaseUser firebaseUser;

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate ( R.layout.fragment_users, container, false );

            recyclerView = view.findViewById ( R.id.recycler_view );

            recyclerView.setHasFixedSize ( true );
            recyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity () ) );

            mUsers = new ArrayList<> (  );

            firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

            readUsers();

            search_user = view.findViewById ( R.id.search_users );

            search_user.addTextChangedListener ( new TextWatcher () {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchUsers(s.toString ().toLowerCase ());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            } );



            return view;
    }

    private void searchUsers(String s)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

        Query query = FirebaseDatabase.getInstance ().getReference ("Users").orderByChild ( "search" )
                .startAt ( s )
                .endAt(s+"\uf8ff");

        query.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ())
                {
                    User user = dataSnapshot.getValue (User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId ().equals ( firebaseUser.getUid () ))
                    {
                        mUsers.add ( user );
                    }
                }

                userAdapter = new UserAdapter ( getContext (),mUsers,false );
                recyclerView.setAdapter ( userAdapter );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void readUsers()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("Users");

        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear ();
                for(DataSnapshot snapshot1:snapshot.getChildren ())
                {
                    User user = snapshot1.getValue (User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId ().equals ( firebaseUser.getUid () ))
                    {
                        mUsers.add ( user );
                    }
                }

                userAdapter = new UserAdapter ( getActivity (),mUsers,false );
                recyclerView.setAdapter ( userAdapter );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}
