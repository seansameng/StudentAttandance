package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.models.ClassForAttendance;

import java.util.List;

public class ClassForAttendanceAdapter extends RecyclerView.Adapter<ClassForAttendanceAdapter.ClassViewHolder> {
    
    private List<ClassForAttendance> classList;
    private OnClassActionListener listener;
    
    public interface OnClassActionListener {
        void onMarkAttendance(ClassForAttendance classItem);
        void onViewAttendanceHistory(ClassForAttendance classItem);
    }
    
    public ClassForAttendanceAdapter(List<ClassForAttendance> classList, OnClassActionListener listener) {
        this.classList = classList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_class_for_attendance, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassForAttendance classItem = classList.get(position);
        holder.bind(classItem);
    }
    
    @Override
    public int getItemCount() {
        return classList.size();
    }
    
    public void updateData(List<ClassForAttendance> newClassList) {
        this.classList = newClassList;
        notifyDataSetChanged();
    }
    
    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSubject, tvStudentCount, tvSchedule, tvRoom, tvLastAttendance;
        private Button btnMarkAttendance, btnViewAttendance;
        
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvStudentCount = itemView.findViewById(R.id.tv_student_count);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvLastAttendance = itemView.findViewById(R.id.tv_last_attendance);
            btnMarkAttendance = itemView.findViewById(R.id.btn_mark_attendance);
            btnViewAttendance = itemView.findViewById(R.id.btn_view_attendance);
        }
        
        public void bind(ClassForAttendance classItem) {
            tvClassName.setText(classItem.getClassName());
            tvSubject.setText(classItem.getSubject());
            tvStudentCount.setText(classItem.getStudentCountText());
            tvSchedule.setText(classItem.getSchedule());
            tvRoom.setText("Room " + classItem.getRoom());
            tvLastAttendance.setText(classItem.getLastAttendance());
            
            // Set up click listeners
            btnMarkAttendance.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMarkAttendance(classItem);
                }
            });
            
            btnViewAttendance.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewAttendanceHistory(classItem);
                }
            });
        }
    }
}
