package com.easy.easychat.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.easychat.R;
import com.easy.easychat.Utills.AppUtills;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.activity.ChatActivity;
import com.easy.easychat.entity.Conversation;
import com.easy.easychat.entity.Messages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationFragment extends Fragment {
    private Context context;
    private FirebaseAuth auth;
    private RecyclerView rlConversation;
    private DatabaseReference mConvDatabase, mMsgDatabase, mUsrDatabase;
    private String current_user_id;
    private View mMainView;
    private ProgressDialog progressBar;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView userImageView;
    public static final long MILS_IN_A_HOUR = 3600000;
    public static final long MILS_IN_8_HOUR = 28800000;
    public static final long MILS_IN_A_DAY = 86400000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_conversation, container, false);
        initView(mMainView);
        getConversationList();
        return mMainView;
    }

    private View initView(View view) {
        context = getActivity();
        progressBar = new ProgressDialog(context);
        rlConversation = (RecyclerView) view.findViewById(R.id.recyclerViewConversation);

        auth = FirebaseAuth.getInstance();

        // current user id
        current_user_id = auth.getCurrentUser().getUid();

        mUsrDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);

        mMsgDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.MESSAGES).child(current_user_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rlConversation.setHasFixedSize(true);
        rlConversation.setLayoutManager(linearLayoutManager);

        //--RETURNING THE VIEW OF FRAGMENT--
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getConversationList();
    }

    private void getConversationList() {
        progressBar.setTitle("Fetching Conversations...");
        progressBar.setProgress(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        // quer to get list by time stamp;
        Query query = mMsgDatabase.orderByChild(CommonConstants.TIME_STAMP);

        FirebaseRecyclerAdapter<Conversation, ConvViewHolder> convAdapter = new FirebaseRecyclerAdapter<Conversation, ConvViewHolder>(
                Conversation.class,
                R.layout.activity_chat_list1,
                ConvViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conversation conv, int position) {

                final String list_user_id = getRef(position).getKey();
                Log.d("userId", list_user_id);
                Query lastMessageQuery = mMsgDatabase.child(list_user_id).limitToLast(1);

                //---IT WORKS WHENEVER CHILD OF mMessageDatabase IS CHANGED---
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map<String, String> mesage = new HashMap<String, String>();
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            mesage.put(datas.getKey(), datas.getValue().toString());
                        }
                        List<String> mesg = new ArrayList<>();
                        for (String m : mesage.values()) {
                            mesg.add(m);
                        }

                        convViewHolder.setMessage(mesg.get(2), conv.isSeen());
                        convViewHolder.setTime(mesg.get(1).toString());
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

                        final String userName = dataSnapshot.child(CommonConstants.USER_NAME).getValue().toString();
                        String userThumb= null;
                        if (dataSnapshot.child(CommonConstants.THUMB_IMAGE).getValue() != null) {
                            String userThumbImg = dataSnapshot.child(CommonConstants.THUMB_IMAGE).getValue().toString();
                            convViewHolder.setUserImage(userThumbImg, getContext());
                            userThumb = userThumbImg;
                        }

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            convViewHolder.setUserOnline(userOnline);

                        }
                        convViewHolder.setName(userName);

                        progressBar.dismiss();
                        //--OPENING CHAT ACTIVITY FOR CLICKED USER----
                        final String finalUserThumb = userThumb;
                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra(CommonConstants.UID, list_user_id);
                                chatIntent.putExtra(CommonConstants.USER_NAME, userName);
                                chatIntent.putExtra(CommonConstants.THUMB_IMAGE, finalUserThumb);
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
        rlConversation.setAdapter(convAdapter);
    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessage(String message, boolean isSeen) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.chat_msg);
            userStatusView.setText(message);

            //--SETTING BOLD FOR NOT SEEN MESSAGES---
            if (isSeen) {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);
        }

        public void setTime(String time) {
            long timeinLong = Long.parseLong(time);
            TextView timeView = (TextView) mView.findViewById(R.id.chat_msg_time);
            timeView.setText(displayTime(timeinLong));
            Log.d("time", displayTime(timeinLong));
        }


        public void setUserImage(String userProfile, final Context context) {
            //--SETTING IMAGE FROM USERTHUMB TO USERIMAGEVIEW--- IF ERRORS OCCUR , ADD USER_IMG----
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.group_image);
            Picasso.with(context).load(userProfile).placeholder(R.drawable.circle_image_group).into(userImageView);
        }


        public void setUserOnline(String onlineStatus) {

            //ImageView userOnlineView = (ImageView) mView.findViewById(R.id.userPresence);
//            if (onlineStatus.equals("true")) {
//                userOnlineView.setVisibility(View.VISIBLE);
//            } else {
//                userOnlineView.setVisibility(View.INVISIBLE);
//            }
        }
    }

    public static final String displayTime(long time) {
        long current = System.currentTimeMillis();
        long mills = time;

        if (mills <= 0) {
            return "invalid time";
        }

        if ((current - mills) < MILS_IN_A_HOUR) {
            return (current - mills) / (1000 * 60) + " minutes ago";

        } else if ((current - mills) < MILS_IN_8_HOUR) {
            return (current - mills) / (1000 * 60 * 60) + " hours ago";

        } else if ((current - mills) < MILS_IN_A_DAY) {
            return "today";

        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mills);

            return cal.get(Calendar.DAY_OF_MONTH) + " "
                    + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        }

    }
}
