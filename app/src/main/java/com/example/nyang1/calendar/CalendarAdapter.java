package com.example.nyang1.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyang1.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private  final ArrayList<String> dayofMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> dayofMonth, CalendarAdapter.OnItemListener onItemListener)
    {
        this.dayofMonth = dayofMonth;
        this.onItemListener = onItemListener;
    }



    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayofMonth.setText(dayofMonth.get(position));
    }

    @Override
    public int getItemCount()
    {
        return dayofMonth.size();
    }
    //onItemClickListener 인터페이스 사용
    //리스트뷰의 한 항목이 선택되었을 때 콜백되어 호출
    public interface  OnItemListener
    {
        void onItemClick(int position, long id);
    }
}
