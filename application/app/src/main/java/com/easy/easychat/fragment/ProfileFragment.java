package com.easy.easychat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private View mMainView;
    private TextView userName, status, mobileNo, tvHeader, tvCamera, tvGallery,tvCancel;
    private ImageView ivLogOut, ivcamera,ivUserImage;
    private String name, id, uid;
    private Context context;
    private DatabaseReference mUsrDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout llAttachmnet;
    private StorageReference mountainsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_profile, container, false);
        inflateToollbar();
        initView(mMainView);
        initOnClickListener();
        return mMainView;
    }

    private View initView(View view) {
        context = getActivity();
        userName = (TextView) view.findViewById(R.id.userName);
        mobileNo = (TextView) view.findViewById(R.id.phoneNo);
        status = (TextView) view.findViewById(R.id.status);
        ivUserImage = (ImageView)view.findViewById(R.id.user_image);
        ivcamera = (ImageView) view.findViewById(R.id.camera);
        llAttachmnet = (LinearLayout) view.findViewById(R.id.ll_attachment_option);
        tvCamera = (TextView) view.findViewById(R.id.tvCamera);
        tvGallery = (TextView) view.findViewById(R.id.tvGallery);
        tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        mUsrDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
         uid = mAuth.getCurrentUser().getUid();
        mUsrDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
         mountainsRef = storageRef.child(CommonConstants.USER_PROFILE_STORAGE+"/"+uid+".jpg");

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());
        getUserInfo(uid);
        getUserProfile();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserProfile();
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

    private void getUserProfile(){
        StorageReference islandRef = storageRef.child(CommonConstants.USER_PROFILE_STORAGE+"/"+uid+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024*5;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("bitmap", bytes.toString());
                if (bytes != null){

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ivUserImage.setImageBitmap(bitmap);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void initOnClickListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAttachmnet.setVisibility(View.GONE);
            }
        });
        ivcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAttachmnet.setVisibility(View.VISIBLE);
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        llAttachmnet.setVisibility(View.GONE);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CommonConstants.SELECT_IMAGE);
                    }
                });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstants.SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] datas = baos.toByteArray();

                        UploadTask uploadTask = mountainsRef.putBytes(datas);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getActivity(), "Upload successfully", Toast.LENGTH_SHORT).show();
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
