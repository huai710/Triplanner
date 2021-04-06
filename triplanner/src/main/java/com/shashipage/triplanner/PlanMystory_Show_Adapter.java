package com.shashipage.triplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashipage.triplanner.PlanMystory_RecyclerView.DayView;
import com.shashipage.triplanner.PlanMystory_RecyclerView.MyViewHolders;
import com.shashipage.triplanner.PlanMystory_RecyclerView.PlaceView;

import java.util.ArrayList;
import java.util.HashMap;

class PlanMystory_Show_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<HashMap<String, String>> arrayList;
    private OnClick onItemClick;

    public PlanMystory_Show_Adapter(ArrayList<HashMap<String,String>> arrayList) {
        this.arrayList = arrayList;
    }

    /**取得每個item內的"VIEW_TYPE"*/
    @Override
    public int getItemViewType(int position) {
        int getType = Integer.parseInt(arrayList.get(position).get("VIEW_TYPE"));
        return getType;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /**在上面的"getItemViewType"中取得的
         * @see viewType
         * 為基準，判斷每個item需使用哪個介面*/
        if (viewType == 0) {
            return new DayView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.planmystory_dayrv, parent, false));
        } else{
            return new PlaceView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.planmystory_placerv, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        /**將holder強制轉型為"MyViewHolders"類別，使介面Ａ/Ｂ都可以獲得onBindViewHolder內容*/
        ((MyViewHolders) holder).bindViewHolder(arrayList.get(position));
        /**判斷該item的介面是處於哪一個介面*/
        if (holder instanceof PlaceView){
            PlaceView placeView = (PlaceView) holder;
            placeView.story.setFocusable(false);
            /**設置按鈕的點擊事件*/
            placeView.placeinformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnButtonClick(arrayList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    /**設置點擊方法，使點擊後取得到的內容能傳回MainActivity*/
    public interface OnClick{
        void OnButtonClick(HashMap<String, String> hashMap);
    }
    /**設置將資料傳回Activity的接口*/
    public void setOnButtonClick(OnClick onItemClick){
        this.onItemClick = onItemClick;
    }
}
