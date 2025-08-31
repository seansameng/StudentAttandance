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

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
    
    private List<ClassEntity> classesList;
    private boolean isStudent;
    private OnClassClickListener listener;
    
    public interface OnClassClickListener {
        void onClassClick(ClassEntity classObj);
    }
    
    public ClassesAdapter(List<ClassEntity> classesList, boolean isStudent) {
        this.classesList = classesList;
        this.isStudent = isStudent;
    }
    
    public void setOnClassClickListener(OnClassClickListener listener) {
        this.listener = listener;
    }
    
    public void updateClasses(List<ClassEntity> newClasses) {
        this.classesList = newClasses;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassEntity classObj = classesList.get(position);
        holder.bind(classObj);
    }
    
    @Override
    public int getItemCount() {
        return classesList != null ? classesList.size() : 0;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName;
        private TextView tvSubject;
        private TextView tvTeacher;
        private TextView tvSchedule;
        private TextView tvRoom;
        private TextView tvEnrolledCount;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvEnrolledCount = itemView.findViewById(R.id.tv_enrolled_count);
            
            // Set click listener using a method reference approach
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        // Get the current classes list and listener
                        List<ClassEntity> currentClasses = ClassesAdapter.this.classesList;
                        OnClassClickListener currentListener = ClassesAdapter.this.listener;
                        if (currentClasses != null && position < currentClasses.size()) {
                            currentListener.onClassClick(currentClasses.get(position));
                        }
                    }
                }
            });
        }
        
        public void bind(ClassEntity classObj) {
            try {
                // Add null checks and default values
                String className = classObj.getClassName() != null ? classObj.getClassName() : "Unknown Class";
                String subject = classObj.getSubject() != null ? classObj.getSubject() : "No Subject";
                String teacherName = classObj.getTeacherName() != null ? classObj.getTeacherName() : "No Teacher";
                String schedule = classObj.getSchedule() != null ? classObj.getSchedule() : "No Schedule";
                String room = classObj.getRoom() != null ? classObj.getRoom() : "No Room";
                
                tvClassName.setText(className);
                tvSubject.setText(subject);
                tvTeacher.setText("Teacher: " + teacherName);
                tvSchedule.setText("Schedule: " + schedule);
                tvRoom.setText("Room: " + room);
                
                // Show enrolled count for teachers/admins
                if (!isStudent) {
                    int enrolledCount = classObj.getEnrolledStudentsCount();
                    int maxStudents = classObj.getMaxStudents();
                    tvEnrolledCount.setText(String.format("Enrolled: %d/%d", enrolledCount, maxStudents));
                    tvEnrolledCount.setVisibility(View.VISIBLE);
                } else {
                    tvEnrolledCount.setVisibility(View.GONE);
                }
                
                // Debug logging
                android.util.Log.d("ClassesAdapter", "Bound class: " + className + ", Subject: " + subject);
                
            } catch (Exception e) {
                android.util.Log.e("ClassesAdapter", "Error binding class data", e);
                // Set fallback values
                tvClassName.setText("Error loading class");
                tvSubject.setText("Error");
                tvTeacher.setText("Error");
                tvSchedule.setText("Error");
                tvRoom.setText("Error");
                tvEnrolledCount.setVisibility(View.GONE);
            }
        }
    }
}
