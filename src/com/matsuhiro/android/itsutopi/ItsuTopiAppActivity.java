package com.matsuhiro.android.itsutopi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.matsuhiro.android.widget.MarqueeView;

public class ItsuTopiAppActivity extends Activity {
//    private static String TAG = "YAHOOTOPIC";
    
    MarqueeView mMarqueeView;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent serviceIntent = new Intent(this, TelopWidgetService.class);
        serviceIntent.setAction(TelopWidgetService.START_ACTION);
        this.startService(serviceIntent);
        
        finish();
    }
}