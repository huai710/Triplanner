package com.shashipage.triplanner;


import android.text.Editable;
import android.text.TextWatcher;
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

public class PlanMystory_Edit_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<HashMap<String,String>> arrayList;
    public OnClick onItemClick;

    //    20210115
    private int t_sub_memory_id;
    private String t_memory_edit;
    private SaveEditListener editListener;


    public PlanMystory_Edit_Adapter(ArrayList<HashMap<String,String>> arrayList) {
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
            final PlaceView placeView = (PlaceView) holder;

            /**設置按鈕的點擊事件*/
            placeView.placeinformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnButtonClick(arrayList.get(position));
                }
            });

            //2021.02.21先定義監聽事件
            final TextWatcher watcher=new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    t_sub_memory_id = Integer.parseInt(arrayList.get(position).get("sub_memory_id"));
                    t_memory_edit = s.toString();
                    editListener.SaveEdit(t_sub_memory_id,t_memory_edit);

                    //2021.02.27
                    HashMap<String, String> hashMap = arrayList.get(position);
                    hashMap.put("STORY", s.toString());
                    arrayList.set(position,hashMap);

                }
            };

            //2021.02.21點選到該筆回憶才設定監聽事件(換了之後要移除)
            placeView.story.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        placeView.story.addTextChangedListener(watcher);
                    }else {
                        placeView.story.removeTextChangedListener(watcher);
                    }
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

    //    **設置EditText取得到的內容能傳回MainActivity*/
    public interface SaveEditListener{
        void SaveEdit(int t_sub_memory_id, String t_memory_edit);
    }

    /**設置將資料傳回Activity的接口*/
    public void setSaveEdit(SaveEditListener editListener){
        this.editListener=editListener;
    }

}
