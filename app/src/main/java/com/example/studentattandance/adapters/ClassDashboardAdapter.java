package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.ClassEntity;

import java.util.List;

public class ClassDashboardAdapter extends RecyclerView.Adapter<ClassDashboardAdapter.ClassViewHolder> {
    
    private List<ClassEntity> classes;
    
    public ClassDashboardAdapter() {
        this.classes = null;
    }
    
    public void updateClasses(List<ClassEntity> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        if (classes != null && position < classes.size()) {
            ClassEntity classEntity = classes.get(position);
            holder.bind(classEntity);
        }
    }
    
    @Override
    public int getItemCount() {
        return classes != null ? classes.size() : 0;
    }
    
    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSubject, tvSchedule, tvRoom, tvTeacher;
        
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
        }
        
        public void bind(ClassEntity classEntity) {
            if (classEntity != null) {
                tvClassName.setText(classEntity.getClassName());
                tvSubject.setText(classEntity.getSubject());
                tvSchedule.setText(classEntity.getSchedule() != null ? classEntity.getSchedule() : "Schedule TBD");
                tvRoom.setText(classEntity.getRoom() != null ? classEntity.getRoom() : "Room TBD");
                tvTeacher.setText("Teacher ID: " + classEntity.getTeacherId());
            }
        }
    }
}
