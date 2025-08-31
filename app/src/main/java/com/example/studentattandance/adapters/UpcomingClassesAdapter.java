package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.models.Class;

import java.util.List;

public class UpcomingClassesAdapter extends RecyclerView.Adapter<UpcomingClassesAdapter.ViewHolder> {
    
    private List<Class> classes;
    
    public UpcomingClassesAdapter(List<Class> classes) {
        this.classes = classes;
    }
    
    public void updateClasses(List<Class> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Class classObj = classes.get(position);
        holder.bind(classObj);
    }
    
    @Override
    public int getItemCount() {
        return classes != null ? classes.size() : 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName;
        private TextView tvSubject;
        private TextView tvTeacher;
        private TextView tvSchedule;
        private TextView tvRoom;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
            tvSchedule = itemView.findViewById(R.id.tv_schedule);
            tvRoom = itemView.findViewById(R.id.tv_room);
        }
        
        public void bind(Class classObj) {
            tvClassName.setText(classObj.getClassName());
            tvSubject.setText(classObj.getSubject());
            tvTeacher.setText(classObj.getTeacherName());
            tvSchedule.setText(classObj.getSchedule());
            tvRoom.setText(classObj.getRoom());
        }
    }
}
