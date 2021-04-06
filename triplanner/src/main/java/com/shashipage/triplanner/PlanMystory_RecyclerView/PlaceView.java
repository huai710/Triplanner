package com.shashipage.triplanner.PlanMystory_RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shashipage.triplanner.R;

import java.util.HashMap;

public class PlaceView extends MyViewHolders {
    public final TextView place,time;
    public final Button placeinformation;
    public final EditText story;


    public PlaceView(@NonNull View itemView) {
        super(itemView);
        place = itemView.findViewById(R.id.planmystory_edit_place);
        placeinformation = itemView.findViewById(R.id.planmystory_edit_placeinformation);
        time = itemView.findViewById(R.id.planmystory_edit_time2);
        story=itemView.findViewById(R.id.planmystory_edit_story2);
    }

    /**將資料綁到介面PlaceView的內容*/
    @Override
    public void bindViewHolder(HashMap<String, String> hashMap) {
        place.setText(hashMap.get("PLACE"));
        time.setText(hashMap.get("TIME"));
        story.setText(hashMap.get("STORY"));
//        story.setKeyListener(null);
//        story.setEnabled(true);
        String aa = story.getText().toString().trim();
        Object bb = story.getTag();
        int cc = 0;
    }

}
