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

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    
    private List<ClassEntity> classList;
    private OnClassClickListener listener;
    
    public interface OnClassClickListener {
        void onClassClick(ClassEntity classEntity);
    }
    
    public ClassAdapter(List<ClassEntity> classList, OnClassClickListener listener) {
        this.classList = classList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassEntity classEntity = classList.get(position);
        holder.bind(classEntity);
    }
    
    @Override
    public int getItemCount() {
        return classList.size();
    }
    
    public void updateData(List<ClassEntity> newClassList) {
        this.classList = newClassList;
        notifyDataSetChanged();
    }
    
    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSubject, tvSchedule, tvRoom, tvTeacher;
        
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClassClick(classList.get(position));
                }
            });
        }
        
        public void bind(ClassEntity classEntity) {
            tvClassName.setText(classEntity.getClassName());
            tvSubject.setText(classEntity.getSubject());
            tvSchedule.setText(classEntity.getSchedule());
            tvRoom.setText(classEntity.getRoom());
            tvTeacher.setText("Teacher ID: " + classEntity.getTeacherId());
        }
    }
}
