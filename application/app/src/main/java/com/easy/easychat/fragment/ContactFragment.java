package com.easy.easychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.activity.ChatActivity;
import com.easy.easychat.adapter.ContactsAdapter;
import com.easy.easychat.adapter.MessageAdapter;
import com.easy.easychat.entity.Messages;
import com.easy.easychat.entity.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {
    private Context context;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabaseReference;
    private ContactsAdapter contactsAdapter;
    private List<User> usersList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUsersList = (RecyclerView) view.findViewById(R.id.user_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(context));

        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        mUsersDatabaseReference.keepSynced(true);
        //getUseList();

    }

    @Override
    public void onResume() {
        super.onResume();
        getUsersListServiceCall();
    }


    private void getUsersListServiceCall(){

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
//                if (users.getImage()!=null){
//                    viewHolder.setImage(users.getImage(), context);
//                }
                    final String user_id = getRef(position).getKey();
                    if (user_id.equals(mAuth.getCurrentUser().getUid())){
                        viewHolder.mView.setVisibility(View.GONE);
                        viewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }
                    final String userName = users.getUserName();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                Intent chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra(CommonConstants.UID, user_id);
                                chatIntent.putExtra(CommonConstants.USER_NAME, userName);
                                startActivity(chatIntent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

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
            if(thumb_image!=null) {
                Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.circle_image_group).into(userImageView);
            }
        }
    }
}
