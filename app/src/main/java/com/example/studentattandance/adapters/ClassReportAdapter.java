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

public class ClassReportAdapter extends RecyclerView.Adapter<ClassReportAdapter.ClassReportViewHolder> {
    
    private List<ClassEntity> classesList;
    
    public ClassReportAdapter(List<ClassEntity> classesList) {
        this.classesList = classesList;
    }
    
    @NonNull
    @Override
    public ClassReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_class_report, parent, false);
        return new ClassReportViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassReportViewHolder holder, int position) {
        ClassEntity classEntity = classesList.get(position);
        holder.bind(classEntity);
    }
    
    @Override
    public int getItemCount() {
        return classesList != null ? classesList.size() : 0;
    }
    
    public void updateData(List<ClassEntity> newClassesList) {
        this.classesList = newClassesList;
        notifyDataSetChanged();
    }
    
    static class ClassReportViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvClassName;
        private TextView tvSubject;
        private TextView tvSchedule;
        private TextView tvRoom;
        private TextView tvAttendanceRate;
        
        public ClassReportViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvAttendanceRate = itemView.findViewById(R.id.tv_attendance_rate);
        }
        
        public void bind(ClassEntity classEntity) {
            if (classEntity != null) {
                tvClassName.setText(classEntity.getClassName());
                tvSubject.setText(classEntity.getSubject());
                tvSchedule.setText(classEntity.getSchedule());
                tvRoom.setText(classEntity.getRoom());
                
                // TODO: Calculate actual attendance rate for this class
                tvAttendanceRate.setText("85.2%"); // Placeholder for now
            }
        }
    }
}
