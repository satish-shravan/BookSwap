package com.bookswap.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.MessageActivity;
import com.bookswap.Model.Chat;
import com.bookswap.Model.User;
import com.bookswap.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private  boolean ischat;

    private String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUsers,boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from ( mContext ).inflate (R.layout.user_item,parent,false);
        return new UserAdapter.viewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = mUsers.get ( position );
        holder.username.setText ( user.getUsername () );
        if(user.getImageURL ().equals ( "default" ))
        {
            holder.profile_image.setImageResource ( R.drawable.userphoto );
        }
        else {
            Glide.with ( mContext ).load ( user.getImageURL () ).placeholder ( R.drawable.new_loader ).into ( holder.profile_image );
        }

        if (ischat){
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat)
        {
            if (user.getIs_online ().equals ( "online" ))
            {
                holder.img_on.setVisibility ( View.VISIBLE );
                holder.img_off.setVisibility ( View.GONE );
            }
            else if(user.getIs_online ().equals ( "offline" ))
            {
                holder.img_on.setVisibility ( View.GONE );
                holder.img_off.setVisibility ( View.VISIBLE );
            }
        }
        else {
            holder.img_on.setVisibility ( View.GONE );
            holder.img_off.setVisibility ( View.GONE );
        }

        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( mContext, MessageActivity.class );
                intent.putExtra ( "userID",user.getId () );
                intent.putExtra ( "bookName","" );
                intent.putExtra ( "userName", user.getUsername () );
                mContext.startActivity ( intent );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mUsers.size ();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public ImageView img_on;
        public ImageView img_off;
        public TextView last_msg;



        public viewHolder(@NonNull View itemView) {
            super ( itemView );

            username = itemView.findViewById ( R.id.username );
            profile_image = itemView.findViewById ( R.id.profile_image );
            img_on = itemView.findViewById ( R.id.img_on );
            img_off = itemView.findViewById ( R.id.img_off );
            last_msg = itemView.findViewById ( R.id.last_msg );
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue( Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
