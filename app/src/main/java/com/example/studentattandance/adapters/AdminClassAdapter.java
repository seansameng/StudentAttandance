package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.ClassEntity;

import java.util.ArrayList;
import java.util.List;

public class AdminClassAdapter extends RecyclerView.Adapter<AdminClassAdapter.ClassViewHolder> {
    
    private List<ClassEntity> classes = new ArrayList<>();
    
    public void updateClasses(List<ClassEntity> newClasses) {
        this.classes = newClasses != null ? newClasses : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_class, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassEntity classEntity = classes.get(position);
        if (classEntity != null) {
            holder.bind(classEntity);
        }
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSubject, tvSchedule;
        
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
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
        }
    }
}
