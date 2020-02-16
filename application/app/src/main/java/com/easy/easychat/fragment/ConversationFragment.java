package com.easy.easychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.activity.ChatActivity;
import com.easy.easychat.adapter.ConversationAdapter;
import com.easy.easychat.entity.Conversation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationFragment extends Fragment {
   private Context context;
   private FirebaseAuth auth;
   private RecyclerView rlConversation;
   private DatabaseReference mConvDatabase;
    private DatabaseReference mMsgDatabase;
    private DatabaseReference mUsrDatabase;
    private String current_user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        context = getActivity();
       rlConversation = (RecyclerView)view.findViewById(R.id.recyclerViewConversation);

       auth = FirebaseAuth.getInstance();

        // current user id
       current_user_id = auth.getCurrentUser().getUid();

      mConvDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.CHATS).child(current_user_id);
      mConvDatabase.keepSynced(true);

      mUsrDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USER);

     mMsgDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.MESSAGE).child(current_user_id);


    }

    @Override
    public void onStart() {
        super.onStart();
        getConversationList();
    }

    private void getConversationList(){
        // quer to get list by time stamp;
        Query query = mConvDatabase.orderByChild("time_stamp");

        FirebaseRecyclerAdapter<Conversation,ConvViewHolder> convAdapter = new FirebaseRecyclerAdapter<Conversation,ConvViewHolder>(
                Conversation.class,
                R.layout.activity_chat_list1,
                ConvViewHolder.class,
                query
        ){
            @Override
            protected void populateViewHolder( final ConvViewHolder convViewHolder,final  Conversation conv, int position) {

                final String list_user_id=getRef(position).getKey();
                Query lastMessageQuery = mMsgDatabase.child(list_user_id).limitToLast(1);

                //---IT WORKS WHENEVER CHILD OF mMessageDatabase IS CHANGED---
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data,conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //---ADDING NAME , IMAGE, ONLINE FEATURE , AND OPENING CHAT ACTIVITY ON CLICK----
                mUsrDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child(CommonConstants.NAME).getValue().toString();
                        String userThumb = dataSnapshot.child(CommonConstants.THUMB_IMAGE).getValue().toString();

                        if(dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            convViewHolder.setUserOnline(userOnline);

                        }
                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb,getContext());

                        //--OPENING CHAT ACTIVITY FOR CLICKED USER----
                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id",list_user_id);
                                chatIntent.putExtra("user_name",userName);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            mView =itemView;
        }

        public void setMessage(String message,boolean isSeen){
            TextView userStatusView = (TextView) mView.findViewById(R.id.chat_msg);
            userStatusView.setText(message);

            //--SETTING BOLD FOR NOT SEEN MESSAGES---
            if(isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            }
            else{
                userStatusView.setTypeface(userStatusView.getTypeface(),Typeface.NORMAL);
            }

        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);
        }


        public void setUserImage(String userThumb, Context context) {

            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.group_image);

            //--SETTING IMAGE FROM USERTHUMB TO USERIMAGEVIEW--- IF ERRORS OCCUR , ADD USER_IMG----
            Picasso.with(context).load(userThumb).placeholder(R.drawable.circle_image_group).into(userImageView);
        }


        public void setUserOnline(String onlineStatus) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.userPresence);
            if(onlineStatus.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }


}
