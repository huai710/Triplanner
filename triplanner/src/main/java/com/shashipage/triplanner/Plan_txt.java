package com.shashipage.triplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

;

public class Plan_txt extends AppCompatActivity {
    String ss = "";
    TextView t001;
    int txtid=0;
    private static final String DB_File="astray.db",DB_TABLE="itinerary01",DB_TABLE2="itinerary02";
    DbHelper db=null;
    private Toolbar toolbar;
    private int DBversion = DbHelper.VERSION;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_txt);
        db = new DbHelper(getApplicationContext(),DB_File,null,DBversion);
        setupViewComponent();
    }

    private void setupViewComponent() {
        t001 = findViewById(R.id.plan_txt);
        Intent it = getIntent();
        txtid = it.getIntExtra("pointIndex",-1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);
        ss = db.getTxt08(txtid);
        t001.setText(ss);
    }

    //------Menu-----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=new Intent();
        switch (item.getItemId()){
            case R.id.menu_finish:
                ss = t001.getText().toString();
                db.updateTxt08(txtid,ss);
                Plan_txt.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
