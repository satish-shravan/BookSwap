package com.bookswap.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bookswap.Model.BookPost;
import com.bookswap.Model.Chat;
import com.bookswap.Model.User;
import com.bookswap.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.viewHolder>{


    private Context mContext;
    private List<BookPost> mBookPosts;


    public UserPostAdapter(Context mContext,List<BookPost> mBookPosts) {
        this.mContext = mContext;
        this.mBookPosts = mBookPosts;
    }

    @NonNull
    @Override
    public UserPostAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from ( mContext ).inflate ( R.layout.user_book_item, parent, false );

        return new UserPostAdapter.viewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostAdapter.viewHolder holder, int position) {
        BookPost bookPost = mBookPosts.get ( position );

        String date_time = bookPost.getDate ()+" // "+bookPost.getTime ();

        holder.bookName.setText ( bookPost.getBook_name () );
        holder.dateTime.setText (date_time);
        Glide.with ( mContext ).load ( bookPost.getBook_imageURL () ).placeholder ( R.drawable.new_loader ).into ( holder.bookImage );

        holder.mark_as_swap.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("BookPosts");

                reference.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren ())
                        {
                            BookPost bp = dataSnapshot.getValue (BookPost.class);

                            if(bp.getUID ().equals ( bookPost.getUID () ) && bp.getBook_imageURL ().equals ( bookPost.getBook_imageURL () ) && bp.getBook_name ().equals ( bookPost.getBook_name () ))
                            {
                                dataSnapshot.getRef ().removeValue ().addOnSuccessListener ( new OnSuccessListener<Void> () {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText ( mContext, "Book Deleted.", Toast.LENGTH_SHORT ).show ();
                                    }
                                } );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
            }
        } );


    }


    @Override
    public int getItemCount() {
        return mBookPosts.size ();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {

        public TextView bookName;
        public TextView dateTime;
        public  ImageView bookImage;
        public CardView mark_as_swap;

        public viewHolder(@NonNull View itemView) {
            super ( itemView );

            bookName = itemView.findViewById ( R.id.book_name );
            dateTime = itemView.findViewById ( R.id.data_time );
            bookImage = itemView.findViewById ( R.id.book_image );
            mark_as_swap = itemView.findViewById ( R.id.mark_as_swap_card );

        }
    }

}

