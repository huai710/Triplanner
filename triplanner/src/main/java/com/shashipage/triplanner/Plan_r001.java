package com.shashipage.triplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class Plan_r001 extends AppCompatActivity {

    private TextView toTxt,del;
    private RelativeLayout r001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_r001);
        setupViewComponent();
    }

    private void setupViewComponent() {
//        toTxt = findViewById(R.id.plan_toTxt);
        toTxt.setOnClickListener(toTxtOn);
//        r001 = findViewById(R.id.plan_r001);
//        del = findViewById(R.id.plan_toTxt2);
        del.setOnClickListener(delOn);
    }

    private View.OnClickListener toTxtOn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intenta = new Intent();
            intenta.setClass(Plan_r001.this, Plan_txt.class);
            startActivity(intenta);
        }
    };
    private View.OnClickListener delOn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            r001.setVisibility(View.GONE);
        }
    };
}
