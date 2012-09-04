package com.matsuhiro.android.itsutopi;

import java.util.List;

import com.matsuhiro.android.widget.MarqueeView;
import com.matsuhiro.android.widget.SystemOverlayLinearLayout;
import com.matsuhiro.android.yahoo.topic.Message;
import com.matsuhiro.android.yahoo.topic.YahooTopicGetterTask;
import com.matsuhiro.android.yahoo.topic.YahooTopicGetterTask.YahooTopicListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class TelopWidgetService extends Service {
    private static final String TAG = "YAHOOTOPIC";
    
    private static boolean mIsDisplayed = false;
    long INTERVAL = 600;
    public static final String START_ACTION = "start";
    public static final String STOP_ACTION = "stop";
    public static final String INTERVAL_ACTION = "interval";
    
    private static MarqueeView mMarqueeView = null;
    private LinearLayout mLinearLayout;
    private SystemOverlayLinearLayout mLayout;
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Intent i = new Intent();
        i.setClassName("com.matsuhiro.android.itsutopi", "com.matsuhiro.android.itsutopi.TelopWidgetService");
        i.setAction(INTERVAL_ACTION);
        
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        if (START_ACTION.equals(intent.getAction()) || INTERVAL_ACTION.equals(intent.getAction())) {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+(INTERVAL*1000), pi);
        } else if (STOP_ACTION.equals(intent.getAction())) {
            manager.cancel(pi);
        }
        if (mMarqueeView != null) {
            requestTelopData();
        }
        if (mIsDisplayed != false) {
            return;
        }
        
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLinearLayout = (LinearLayout)inflater.inflate(R.layout.systemoverlayout, null);
        mLayout = (SystemOverlayLinearLayout)mLinearLayout.findViewById(R.id.overlay);
        mLayout.setOnClickListener(new SystemOverlayLinearLayout.OnClickListener() {
            public void onClick(View rootview) {
                Uri uri = Uri.parse("http://dailynews.yahoo.co.jp/fc/domestic/");
                Intent tobrowser = new Intent(Intent.ACTION_VIEW, uri);
                tobrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(tobrowser);
                
            }
        });
        
        mMarqueeView = (MarqueeView)mLayout.findViewById(R.id.marquee_view);
//        mMarqueeView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Uri uri = Uri.parse("http://dailynews.yahoo.co.jp/fc/domestic/");
//                Intent tobrowser = new Intent(Intent.ACTION_VIEW, uri);
//                tobrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(tobrowser);
//                
//            }
//        });
        
        mLayout.addWindow();
        
        
        mIsDisplayed = true;
        requestTelopData();
    }
    
    @Override
    public void onDestroy() {
        mIsDisplayed = false;
        mMarqueeView.clearMarquee();
        mLayout.removeAllViews();
        mLayout.removeWindow();
        mLayout = null;
        mMarqueeView = null;
    }
    
    private void requestTelopData() {
        new YahooTopicGetterTask(new YahooTopicListener() {
            public void notifyTopics(List<Message> messages) {
                String text = "";
                for (Message msg : messages) {
                    Log.v(TAG,"title:"+msg.getTitle());
                    text += msg.getTitle()+"  |  ";
                }
                
                mMarqueeView.setText(text);
                if (!mMarqueeView.isStarted()) {
                    mMarqueeView.startMarquee();
                }
            }

            public void notifyError() {
                // TODO Auto-generated method stub
                
            }
            
        }).execute((Void)null);
    }
}
