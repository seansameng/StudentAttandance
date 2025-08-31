package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.ClassEntity;

import java.util.List;

public class ClassManagementAdapter extends RecyclerView.Adapter<ClassManagementAdapter.ClassViewHolder> {
    
    private List<ClassEntity> classes;
    private OnClassActionListener actionListener;
    private boolean isAdmin;
    private boolean isTeacher;
    
    public interface OnClassActionListener {
        void onClassAction(ClassEntity classEntity, String action);
    }
    
    public ClassManagementAdapter(List<ClassEntity> classes, OnClassActionListener actionListener, boolean isAdmin, boolean isTeacher) {
        this.classes = classes;
        this.actionListener = actionListener;
        this.isAdmin = isAdmin;
        this.isTeacher = isTeacher;
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_management, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassEntity classEntity = classes.get(position);
        holder.bind(classEntity);
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSubject, tvSchedule, tvRoom, tvTeacher;
        private ImageButton btnView, btnEdit, btnDelete;
        
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
            btnView = itemView.findViewById(R.id.btn_view);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
        
        public void bind(ClassEntity classEntity) {
            if (classEntity.getClassName() != null) {
                tvClassName.setText(classEntity.getClassName());
            }
            
            if (classEntity.getSubject() != null) {
                tvSubject.setText(classEntity.getSubject());
            }
            
            if (classEntity.getSchedule() != null) {
                tvSchedule.setText(classEntity.getSchedule());
            }
            
            if (classEntity.getRoom() != null) {
                tvRoom.setText(classEntity.getRoom());
            }
            
            if (classEntity.getTeacherId() != null) {
                // Try to get teacher name from database
                try {
                    // This will be handled by the fragment when loading data
                    tvTeacher.setText("Teacher: " + classEntity.getTeacherId());
                } catch (Exception e) {
                    tvTeacher.setText("Teacher: " + classEntity.getTeacherId());
                }
            } else {
                tvTeacher.setText("Teacher: Not Assigned");
            }
            
            // Setup role-based button visibility
            btnView.setVisibility(View.VISIBLE); // All roles can view
            
            if (isAdmin || isTeacher) {
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.GONE);
            }
            
            if (isAdmin) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }
            
            // Setup click listeners
            btnView.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onClassAction(classEntity, "view");
                }
            });
            
            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onClassAction(classEntity, "edit");
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onClassAction(classEntity, "delete");
                }
            });
        }
    }
}
