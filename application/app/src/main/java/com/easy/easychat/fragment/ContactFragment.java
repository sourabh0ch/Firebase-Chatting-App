package com.easy.easychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.activity.ProfileActivity;
import com.easy.easychat.entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {
    private Context context;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        context = getActivity();
        mUsersList = (RecyclerView) view.findViewById(R.id.user_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(context));

        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        mUsersDatabaseReference.keepSynced(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.contacts_adapter,
                UserViewHolder.class,
                mUsersDatabaseReference
        ) {

            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User users, int position) {
                viewHolder.setName(users.getUserName());
                viewHolder.setStatus(users.getStatus());
                viewHolder.setImage(users.getImage(), context);
                final String user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);
                    }
                });
            }

        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);
        }


        public void setStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.status);
            userStatusView.setText(status);
        }

        public void setImage(String thumb_image, Context ctx) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_image);
            //Log.e("thumb URL is--- ",thumb_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.circle_image_group).into(userImageView);
        }
    }
}
