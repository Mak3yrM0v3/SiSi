package com.example.luca.ss;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private static ArrayList<Element> data;

    public RecyclerViewAdapter(ArrayList<Element> data){
        this.data=data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_small,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        String code = data.get(i).getCode();
        String value = data.get(i).getValue();
        String user = data.get(i).getUser();
        viewHolder.code.setText(code);
        viewHolder.value.setText(value);
        viewHolder.user.setText(user);
    }

    public void setData(ArrayList<Element> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        protected TextView value, code, user;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            value = itemView.findViewById(R.id.item_value);
            code = itemView.findViewById(R.id.item_code);
            user= itemView.findViewById(R.id.item_user);
        }
    }
}
