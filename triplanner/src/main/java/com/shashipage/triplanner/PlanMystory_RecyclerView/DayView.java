package com.shashipage.triplanner.PlanMystory_RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shashipage.triplanner.R;

import java.util.HashMap;

public class DayView extends MyViewHolders {

    public final TextView day;

    public DayView(@NonNull View itemView) {
        super(itemView);
        day = itemView.findViewById(R.id.planmystory_day);
    }

    /**將資料綁到介面Day的內容*/
    @Override
    public void bindViewHolder(HashMap<String, String> hashMap) {
        day.setText(hashMap.get("DAY"));
    }
}
