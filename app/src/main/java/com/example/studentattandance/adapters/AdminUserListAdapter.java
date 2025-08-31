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

public class AdminUserListAdapter extends RecyclerView.Adapter<AdminUserListAdapter.UserViewHolder> {
    
    private List<UserEntity> userList;
    private OnUserActionListener listener;
    
    @FunctionalInterface
    public interface OnUserActionListener {
        void onUserAction(UserEntity user, String action);
    }
    
    public AdminUserListAdapter(List<UserEntity> userList, OnUserActionListener listener) {
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
                    listener.onUserAction(userList.get(position), "click");
                }
            });
        }
        
        public void bind(UserEntity user) {
            if (user.getUsername() != null) {
                tvUsername.setText(user.getUsername());
            }
            
            String fullName = "";
            if (user.getFirstName() != null) {
                fullName += user.getFirstName();
            }
            if (user.getLastName() != null) {
                if (!fullName.isEmpty()) fullName += " ";
                fullName += user.getLastName();
            }
            tvFullName.setText(fullName.isEmpty() ? "N/A" : fullName);
            
            if (user.getEmail() != null) {
                tvEmail.setText(user.getEmail());
            } else {
                tvEmail.setText("N/A");
            }
            
            if (user.getRole() != null) {
                tvRole.setText(user.getRole());
            } else {
                tvRole.setText("N/A");
            }
            
            // Add long click for edit/delete options
            itemView.setOnLongClickListener(v -> {
                showUserOptions(user);
                return true;
            });
        }
        
        private void showUserOptions(UserEntity user) {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(itemView.getContext(), itemView);
            popup.inflate(R.menu.user_options_menu);
            
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_user) {
                    if (listener != null) {
                        listener.onUserAction(user, "edit");
                    }
                    return true;
                } else if (itemId == R.id.action_delete_user) {
                    if (listener != null) {
                        listener.onUserAction(user, "delete");
                    }
                    return true;
                }
                return false;
            });
            
            popup.show();
        }
    }
}
