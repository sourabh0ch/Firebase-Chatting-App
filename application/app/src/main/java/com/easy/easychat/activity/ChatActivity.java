package com.easy.easychat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.Utills.SharedPrefrenceUtil;
import com.easy.easychat.adapter.MessageAdapter;
import com.easy.easychat.entity.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private ImageView ivLogOut, ivBack;
    private ImageButton ivAttach, ivSend;
    private TextView tvHeader;
    private EditText etMessage;
    private String chatUsername, chatUserId, currentUserId, currentUsername;
    private RecyclerView rvMessasge;
    private DatabaseReference mRootReference;
    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout swipeRefresh;
    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;

    private int itemPos = 0;
    private String mLastKey="";
    private String mPrevKey="";
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        inflateToolBar();
        intiView();
        initOnclickListener();
        mMessageAdapter = new MessageAdapter(messagesList);
        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);

        // mMessagesList.setHasFixedSize(true);
        rvMessasge.setLayoutManager(mLinearLayoutManager);
        rvMessasge.setAdapter(mMessageAdapter);
    }

    private void inflateToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main__toolbar1);
        tvHeader = (TextView) mToolbar.findViewById(R.id.header);
        ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

    }

    private void intiView() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
       currentUserId = mAuth.getCurrentUser().getUid();
        ivBack = (ImageView) findViewById(R.id.hdr_bck_icon);
        chatUsername = getIntent().getStringExtra(CommonConstants.USER_NAME);
        chatUserId = getIntent().getStringExtra(CommonConstants.UID);
        tvHeader.setText(chatUsername);
        rvMessasge = (RecyclerView) findViewById(R.id.rvMesages) ;

        ivLogOut.setVisibility(View.GONE);
        etMessage = (EditText) findViewById(R.id.chatMessageView);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ivSend = (ImageButton)  findViewById(R.id.chatSendButton);
        ivAttach = (ImageButton)  findViewById(R.id.chatAddButton);
        loadMessage();
    }

    private void initOnclickListener() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.this.finish();
            }
        });

        tvHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(ChatActivity.this, ProfileActivity.class);
                profileIntent.putExtra(CommonConstants.UID, chatUserId);
                profileIntent.putExtra(CommonConstants.USER_NAME, chatUsername);
                startActivity(profileIntent);
            }
        });

        //----LOADING 10 MESSAGES ON SWIPE REFRESH----
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemPos = 0;
                mCurrentPage++;
                loadMoreMessages();;

            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatActivity.this, "Can't Send Empty Messages", Toast.LENGTH_SHORT).show();
                }

                if (!TextUtils.isEmpty(message)) {
                    String current_user_ref = CommonConstants.MESSAGES+"/" + currentUserId + "/" + chatUserId;
                    String chat_user_ref = CommonConstants.MESSAGES+"/" + chatUserId + "/" + currentUserId;

                    DatabaseReference user_message_push = mRootReference.child(CommonConstants.MESSAGES)
                            .child(currentUserId).child(chatUserId).push();

                    String push_id = user_message_push.getKey();

                    addToConversationDatabase(currentUserId, chatUserId, push_id, message);

                    Map messageMap = new HashMap();
                    messageMap.put("message", message);
                    messageMap.put("seen", false);
                    messageMap.put("type", "text");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", currentUserId);
                    //messageMap.put("to",chatUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.e("CHAT_ACTIVITY","Cannot add message to database");
                            }
                            else{
                                Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                                etMessage.setText("");
                            }
                        }
                    });


                }
            }
        });

    }

    private void addToConversationDatabase(String currentUserId, String chatUserId, String push_id, String message) {
        String current_user_ref = CommonConstants.CONVERSATIONS+"/" + currentUserId + "/" + chatUserId;
        DatabaseReference user_conv_push = mRootReference.child(CommonConstants.CONVERSATIONS)
                .child(currentUserId).child(chatUserId).push();

        Map convDataMap = new HashMap();
        convDataMap.put(CommonConstants.CURRENT_USER_ID, currentUserId);
        convDataMap.put(CommonConstants.CHAT_USER_ID, chatUserId);
        //convDataMap.put(CommonConstants.MESSAGE_ID, push_id);
        convDataMap.put(CommonConstants.MESSAGE, message);
        convDataMap.put(CommonConstants.TIME_STAMP, ServerValue.TIMESTAMP);


        Map convUserMap = new HashMap();
        convUserMap.put(current_user_ref + "/" + push_id, convDataMap);

        mRootReference.updateChildren(convUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError != null){
                    //Log.e("CHAT_ACTIVITY","Cannot add message to database");
                }
                else{
                    //Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    etMessage.setText("");
                }
            }
        });
    }

    private void loadMessage(){
        DatabaseReference messageRef = mRootReference.child(CommonConstants.MESSAGES).child(currentUserId).child(chatUserId);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = (Messages) dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){
                    String mMessageKey = dataSnapshot.getKey();

                    mLastKey = mMessageKey;
                    mPrevKey = mMessageKey;
                }

                messagesList.add(messages);
                mMessageAdapter.notifyDataSetChanged();

                rvMessasge.scrollToPosition(messagesList.size()-1);

                swipeRefresh.setRefreshing(false);
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
    }

    //---ON REFRESHING 10 MORE MESSAGES WILL LOAD----
    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootReference.child(CommonConstants.MESSAGES).child(currentUserId).child(chatUserId);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = (Messages) dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();


                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++,message);

                }
                else{
                    mPrevKey = mLastKey;
                }

                if(itemPos == 1){
                    String mMessageKey = dataSnapshot.getKey();
                    mLastKey = mMessageKey;
                }


                mMessageAdapter.notifyDataSetChanged();

                swipeRefresh.setRefreshing(false);

                mLinearLayoutManager.scrollToPositionWithOffset(10,0);
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
    }

}
