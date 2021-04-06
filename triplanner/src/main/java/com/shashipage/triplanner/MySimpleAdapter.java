package com.shashipage.triplanner;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class MySimpleAdapter extends SimpleAdapter {
    private final Context mContext;

    public MySimpleAdapter(Context context, List<Map<String, Object>> l_item, int location_gview_item, String[] strings, int[] ints) {
        super(context,l_item,location_gview_item,strings,ints);
        this.mContext=context;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View v=super.getView(position,convertView,parent);

        ImageView img_view=(ImageView)v.getTag();
        if (img_view==null){
            img_view=(ImageView)v.findViewById(R.id.l_img);
            v.setTag(img_view);
        }
        //get the URL from the data in list
        String img_URL=((Map)getItem(position)).get("attr_photoURL").toString().trim();
        // create a ProgressDrawable object which we will show as placeholder
        CircularProgressDrawable drawable = new CircularProgressDrawable(mContext);
        drawable.setColorSchemeColors(Color.argb(1, 	252, 235, 167)
                ,Color.argb(1,249, 169, 47),Color.argb(1,	249, 119, 47));
//        drawable.setColorSchemeColors(R.color.Maincolor, R.color.Silver, R.color.Contentcolor);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
//        drawable.setStyle(LARGE);
        // set all other properties as you would see fit and start it
        drawable.start();

        Glide.with(img_view)
                .load(img_URL)
//                .override(120, 80)
                .error(R.drawable.ic_imgerror)
                .placeholder(drawable)
                .centerCrop()
//                .into(newCustomImageViewTarget(imageview, 300, 300));
                .into(img_view);

//        Picasso.get().load(img_URL)
//                .placeholder(R.drawable.loading_anim)
//                .error(R.drawable.img_ns)
////                .centerCrop()
//                .into(img_view);


        return v;
    }
}