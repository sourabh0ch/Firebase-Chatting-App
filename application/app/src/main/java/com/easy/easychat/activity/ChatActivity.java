package com.easy.easychat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private ImageView ivLogOut, ivBack;
    private TextView tvHeader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intiView();
        inflateToolBar();
    }

    private void inflateToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main__toolbar1);
        tvHeader = (TextView)mToolbar.findViewById(R.id.header);
        ivBack = (ImageView)findViewById(R.id.hdr_bck_icon);
        final String name = getIntent().getStringExtra(CommonConstants.USER_NAME);
        final String id = getIntent().getStringExtra(CommonConstants.UID);
        tvHeader.setText(name);
        ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
        ivLogOut.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
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
                profileIntent.putExtra(CommonConstants.UID, id);
                profileIntent.putExtra(CommonConstants.USER_NAME,name);
                startActivity(profileIntent);
            }
        });

    }

    private void intiView(){
    }
}
