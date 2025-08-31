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

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {
    
    private List<UserEntity> students;
    
    public StudentListAdapter(List<UserEntity> students) {
        this.students = students;
    }
    
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_simple, parent, false);
        return new StudentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        UserEntity student = students.get(position);
        holder.bind(student);
    }
    
    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }
    
    public void updateStudents(List<UserEntity> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }
    
    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentEmail;
        
        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentEmail = itemView.findViewById(R.id.tv_student_email);
        }
        
        public void bind(UserEntity student) {
            if (student != null) {
                String fullName = student.getFirstName() + " " + student.getLastName();
                tvStudentName.setText(fullName);
                
                if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                    tvStudentEmail.setText(student.getEmail());
                } else {
                    tvStudentEmail.setText("No email");
                }
            }
        }
    }
}
