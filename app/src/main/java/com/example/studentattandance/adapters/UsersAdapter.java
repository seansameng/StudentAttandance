package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    
    private List<UserEntity> usersList;
    private OnUserClickListener listener;
    
    public interface OnUserClickListener {
        void onUserClick(UserEntity user);
    }
    
    public UsersAdapter() {
        this.usersList = new ArrayList<>();
    }
    
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
    
    public void updateUsers(List<UserEntity> users) {
        this.usersList = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user_simple, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserEntity user = usersList.get(position);
        holder.bind(user);
    }
    
    @Override
    public int getItemCount() {
        return usersList.size();
    }
    
    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvFullName, tvRole, tvEmail;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvEmail = itemView.findViewById(R.id.tv_email);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(usersList.get(position));
                }
            });
        }
        
        public void bind(UserEntity user) {
            if (user.getUsername() != null) {
                tvUsername.setText(user.getUsername());
            } else {
                tvUsername.setText("N/A");
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
            
            if (user.getRole() != null) {
                tvRole.setText(user.getRole());
            } else {
                tvRole.setText("N/A");
            }
            
            if (user.getEmail() != null) {
                tvEmail.setText(user.getEmail());
            } else {
                tvEmail.setText("N/A");
            }
        }
    }
}
