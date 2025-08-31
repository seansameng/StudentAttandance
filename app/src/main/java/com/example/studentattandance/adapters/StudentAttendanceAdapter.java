package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.models.StudentAttendanceItem;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentViewHolder> {
    
    private List<StudentAttendanceItem> studentList;
    private OnAttendanceChangeListener listener;
    
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(StudentAttendanceItem student, boolean isPresent);
    }
    
    public StudentAttendanceAdapter(List<StudentAttendanceItem> studentList, OnAttendanceChangeListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_student_attendance, parent, false);
        return new StudentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentAttendanceItem student = studentList.get(position);
        holder.bind(student);
    }
    
    @Override
    public int getItemCount() {
        return studentList.size();
    }
    
    public void updateData(List<StudentAttendanceItem> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }
    
    public List<StudentAttendanceItem> getStudentList() {
        return studentList;
    }
    
    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentId;
        private CheckBox checkboxPresent, checkboxAbsent;
        
        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            checkboxPresent = itemView.findViewById(R.id.checkbox_present);
            checkboxAbsent = itemView.findViewById(R.id.checkbox_absent);
        }
        
        public void bind(StudentAttendanceItem student) {
            tvStudentName.setText(student.getStudentName());
            tvStudentId.setText("ID: " + student.getStudentIdNumber());
            
            // Set checkbox states
            checkboxPresent.setChecked(student.isPresent());
            checkboxAbsent.setChecked(student.isAbsent());
            
            // Set up checkbox listeners
            checkboxPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    student.setPresent(true);
                    checkboxAbsent.setChecked(false);
                } else {
                    student.setPresent(false);
                }
                if (listener != null) {
                    listener.onAttendanceChanged(student, true);
                }
            });
            
            checkboxAbsent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    student.setAbsent(true);
                    checkboxPresent.setChecked(false);
                } else {
                    student.setAbsent(false);
                }
                if (listener != null) {
                    listener.onAttendanceChanged(student, false);
                }
            });
        }
    }
}
