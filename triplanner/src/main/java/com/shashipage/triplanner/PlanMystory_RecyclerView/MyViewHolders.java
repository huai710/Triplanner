package com.shashipage.triplanner.PlanMystory_RecyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public abstract class MyViewHolders extends RecyclerView.ViewHolder{
    public MyViewHolders(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bindViewHolder(HashMap<String,String> hashMap);
}
