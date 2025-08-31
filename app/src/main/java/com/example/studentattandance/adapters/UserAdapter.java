package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    
    private List<UserEntity> userList;
    private OnUserClickListener listener;
    
    public interface OnUserClickListener {
        void onUserClick(UserEntity user);
        void onEditUser(UserEntity user);
        void onDeleteUser(UserEntity user);
    }
    
    public UserAdapter(List<UserEntity> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserEntity user = userList.get(position);
        holder.bind(user);
    }
    
    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    public void updateData(List<UserEntity> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }
    
    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvFullName, tvEmail, tvRole;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(userList.get(position));
                }
            });
        }
        
        public void bind(UserEntity user) {
            try {
                // Add null checks and default values
                String username = user.getUsername() != null ? user.getUsername() : "Unknown";
                String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                String lastName = user.getLastName() != null ? user.getLastName() : "";
                String email = user.getEmail() != null ? user.getEmail() : "No Email";
                String role = user.getRole() != null ? user.getRole() : "Unknown Role";
                
                tvUsername.setText(username);
                tvFullName.setText(firstName + " " + lastName);
                tvEmail.setText(email);
                tvRole.setText(role);
                
                // Add long click for edit/delete options
                itemView.setOnLongClickListener(v -> {
                    showUserOptions(user);
                    return true;
                });
                
                // Debug logging
                android.util.Log.d("UserAdapter", "Bound user: " + username + ", Role: " + role);
                
            } catch (Exception e) {
                android.util.Log.e("UserAdapter", "Error binding user data", e);
                // Set fallback values
                tvUsername.setText("Error loading user");
                tvFullName.setText("Error");
                tvEmail.setText("Error");
                tvRole.setText("Error");
            }
        }
        
        private void showUserOptions(UserEntity user) {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(itemView.getContext(), itemView);
            popup.inflate(R.menu.user_options_menu);
            
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_user) {
                    if (listener != null) {
                        listener.onEditUser(user);
                    }
                    return true;
                } else if (itemId == R.id.action_delete_user) {
                    if (listener != null) {
                        listener.onDeleteUser(user);
                    }
                    return true;
                }
                return false;
            });
            
            popup.show();
        }
    }
}
