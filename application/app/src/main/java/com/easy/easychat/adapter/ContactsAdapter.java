package com.easy.easychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easychat.R;
import com.easy.easychat.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<User> userList;
    private Context context;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public ContactsAdapter(List<User> userList){
        this.userList = userList;
    }
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_adapter,parent,false);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userList.remove(mAuth.getCurrentUser().getUid());
        return new ContactsAdapter.ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
        holder.status.setText(user.getStatus());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView status;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            status = (TextView)itemView.findViewById(R.id.status);
            context = itemView.getContext();
        }
    }
}
