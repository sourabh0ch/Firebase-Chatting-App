package com.easy.easychat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private View mMainView;
    private TextView userName, status, mobileNo, tvHeader;
    private ImageView ivLogOut;
    private String name, id;
    private Context context;
    private DatabaseReference mUsrDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_profile, container, false);
        inflateToollbar();
        initView(mMainView);
        return mMainView;
    }

    private View initView(View view) {
        context = getActivity();
        userName = (TextView)view.findViewById(R.id.userName);
        mobileNo = (TextView) view.findViewById(R.id.phoneNo);
        status = (TextView) view.findViewById(R.id.status);
        mUsrDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        mUsrDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        getUserInfo(uid);

        return view;
    }

    private void inflateToollbar() {
        Toolbar mToolbar = (Toolbar) mMainView.findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);
        tvHeader = (TextView) mToolbar.findViewById(R.id.header);
        ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
        ivLogOut.setVisibility(View.GONE);

    }

    private void getUserInfo(String uid) {
        mUsrDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String name = dataSnapshot.child(CommonConstants.USER_NAME).getValue().toString();
                userName.setText(name);
                if (dataSnapshot.child(CommonConstants.THUMB_IMAGE).getValue() != null) {
                    String userThumb = dataSnapshot.child(CommonConstants.THUMB_IMAGE).getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
