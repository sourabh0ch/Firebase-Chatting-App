package com.easy.easychat.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.easy.easychat.R;
import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private View mMainView;
    private TextView userName, status, mobileNo, tvHeader, tvCamera, tvGallery, tvCancel;
    private ImageView ivLogOut, ivcamera;
    private CircleImageView ivUserImage;
    private String name, id, uid;
    private Context context;
    private DatabaseReference mUsrDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout llAttachmnet;
    private StorageReference mountainsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog dialog;

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
        dialog = new ProgressDialog(context);
        userName = (TextView) view.findViewById(R.id.userName);
        mobileNo = (TextView) view.findViewById(R.id.phoneNo);
        status = (TextView) view.findViewById(R.id.status);
        ivUserImage = (CircleImageView) view.findViewById(R.id.user_image);
        ivcamera = (ImageView) view.findViewById(R.id.camera);
        llAttachmnet = (LinearLayout) view.findViewById(R.id.ll_attachment_option);
        tvCamera = (TextView) view.findViewById(R.id.tvCamera);
        tvGallery = (TextView) view.findViewById(R.id.tvGallery);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        mUsrDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mAuth.getCurrentUser().getUid();
        mUsrDatabase = FirebaseDatabase.getInstance().getReference().child(CommonConstants.USERS);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
        mountainsRef = storageRef.child(CommonConstants.USER_PROFILE_STORAGE + "/" + uid + ".jpg");

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());
        getUserInfo(uid);
        //getUserProfile();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getUserProfile();
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
                    Picasso.with(context).load(userThumb).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
                            .placeholder(R.drawable.circle_image_group).into(ivUserImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfile() {

        storageRef.child(CommonConstants.USER_PROFILE_STORAGE + "/" + uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                mUsrDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child(CommonConstants.THUMB_IMAGE).setValue(uri.toString());
                        Picasso.with(context).load(uri.toString()).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
                                .placeholder(R.drawable.circle_image_group).into(ivUserImage);
                        dialog.dismiss();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failed to load the image", Toast.LENGTH_SHORT).show();
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
                    dialog.setTitle("Uploading...");
                    dialog.setProgress(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] datas = baos.toByteArray();

                        UploadTask uploadTask = mountainsRef.putBytes(datas);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                updateUserProfile(taskSnapshot.getUploadSessionUri().toString());
                                //taskSnapshot.getUploadSessionUri().toString();
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

    private void updateUserProfile(final String  uploadSessionUri) {
        getUserProfile();
//        mUsrDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                dataSnapshot.getRef().child(CommonConstants.THUMB_IMAGE).setValue(uploadSessionUri);
//                Picasso.with(context).load(uploadSessionUri).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside()
//                        .placeholder(R.drawable.circle_image_group).into(ivUserImage);
//                dialog.dismiss();
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("User", databaseError.getMessage());
//            }
//        });
    }

}
