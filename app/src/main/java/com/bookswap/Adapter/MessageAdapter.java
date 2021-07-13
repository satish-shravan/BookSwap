package com.bookswap.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.Model.Chat;
import com.bookswap.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.w3c.dom.Text;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    private Context mContext;
    private List<Chat> mChats;
    private String imageURL;

    public MessageAdapter(Context mContext,List<Chat> mChats,String imageURL) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from ( mContext ).inflate ( R.layout.chat_item_right,parent,false);
            return new MessageAdapter.viewHolder ( view );
        }
        else {
            View view = LayoutInflater.from ( mContext ).inflate ( R.layout.chat_item_left, parent, false );
            return new MessageAdapter.viewHolder ( view );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.viewHolder holder, int position) {

        Chat chat = mChats.get ( position );

        holder.show_message.setText ( chat.getMessage ().toString () );

        holder.show_date_time.setText ( chat.getDateTime ());

        if(imageURL.equals ( "default" ))
        {
            holder.profile_image.setImageResource ( R.drawable.userphoto );
        }
        else {
            Glide.with ( mContext ).load ( imageURL ).placeholder ( R.drawable.new_loader ).into ( holder.profile_image );
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size ();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {

        public TextView show_message,show_date_time;
        public ImageView profile_image;


        public viewHolder(@NonNull View itemView) {
            super ( itemView );

            show_message = itemView.findViewById ( R.id.show_message );
            profile_image = itemView.findViewById ( R.id.profile_image );
            show_date_time = itemView.findViewById ( R.id.show_date_time );

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();

        if(mChats.get ( position ).getSender ().equals ( firebaseUser.getUid () ))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}

