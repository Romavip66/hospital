package com.example.diploma_hospital.ui.doctor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.Note;
import com.example.diploma_hospital.model.NoteView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.MyViewHolder> {
    private List<NoteView> n1;
    private Context context;
    public DoctorAdapter(Context context ,List<NoteView> note){
        n1 = note;
        this.context = context;
    }

    @NonNull
    @Override
    public DoctorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row, parent,false );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.t1.setText(n1.get(position).getGuestName());
        Log.d("smth", n1.get(position).guestName);
        holder.t2.setText(n1.get(position).getDate());
        //
    }

    @Override
    public int getItemCount() {
        return n1.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView t1, t2, t3;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.tvUser);
            t2 = itemView.findViewById(R.id.tvDate);
            t3 = itemView.findViewById(R.id.tvNum);
        }
    }
}
