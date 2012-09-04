package com.matsuhiro.android.itsutopi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class TelopWidgetServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, TelopWidgetService.class);
            serviceIntent.setAction(TelopWidgetService.START_ACTION);
            context.startService(serviceIntent);
        }
        
    }

}
