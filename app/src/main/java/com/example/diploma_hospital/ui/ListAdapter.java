package com.example.diploma_hospital.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.NoteView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<NoteView> mList;
    private Context mContext;
    private final LayoutInflater mLayoutInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date, name, number;

        public ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.tvDate);
            name = v.findViewById(R.id.tvUser);
            number = v.findViewById(R.id.tvNum);
        }
    }

    public ListAdapter(List<NoteView> plist, Context pContext) {
        this.mList = plist;
        this.mContext = pContext;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = this.mLayoutInflater.inflate(R.layout.my_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //set data
        NoteView weightData = mList.get(position);
        holder.date.setText(weightData.getDate());
        holder.name.setText(weightData.getGuestName());
        holder.number.setText(weightData.getNum());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
