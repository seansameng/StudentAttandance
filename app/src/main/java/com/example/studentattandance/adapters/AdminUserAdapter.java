package com.example.studentattandance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {
    
    private List<UserEntity> users = new ArrayList<>();
    private Context context;
    private DataRepository dataRepository;
    private OnUserActionListener actionListener;
    
    public interface OnUserActionListener {
        void onEditUser(UserEntity user);
        void onDeleteUser(UserEntity user);
        void onViewUser(UserEntity user);
    }
    
    public AdminUserAdapter(Context context, OnUserActionListener listener) {
        this.context = context;
        this.dataRepository = new DataRepository(context);
        this.actionListener = listener;
    }
    
    public void updateUsers(List<UserEntity> newUsers) {
        this.users = newUsers != null ? newUsers : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserEntity user = users.get(position);
        if (user != null) {
            holder.bind(user, actionListener);
        }
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvRole, tvName;
        private Button btnEdit, btnDelete, btnView;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvName = itemView.findViewById(R.id.tv_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnView = itemView.findViewById(R.id.btn_view);
        }
        
        public void bind(UserEntity user, OnUserActionListener listener) {
            if (user.getUsername() != null) {
                tvUsername.setText(user.getUsername());
            }
            
            if (user.getRole() != null) {
                tvRole.setText(user.getRole());
            }
            
            String fullName = "";
            if (user.getFirstName() != null) {
                fullName += user.getFirstName();
            }
            if (user.getLastName() != null) {
                fullName += " " + user.getLastName();
            }
            tvName.setText(fullName.trim());
            
            // Setup click listeners
            btnView.setOnClickListener(v -> {
                if (listener != null) listener.onViewUser(user);
            });
            
            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditUser(user);
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteUser(user);
            });
        }
    }
}
