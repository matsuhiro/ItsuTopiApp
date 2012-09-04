package com.matsuhiro.android.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class SystemOverlayLinearLayout extends LinearLayout {
//    private static final String TAG = "YAHOOTOPIC";
    private static final float THRESHOLD = 50; 
    private WindowManager.LayoutParams mWMParams;
    private Context mContext;
    private float mFirstX, mFirstY;
    private OnClickListener mListener = null;
    private boolean mIsClicked;
    
    public SystemOverlayLinearLayout(Context context) {
        super(context);
        mContext = context;
    }
    public SystemOverlayLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    public void addWindow() {
        this.setClickable(true);
        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Log.v(TAG, "clicked");
                if (mIsClicked != false && mListener != null) {
                    mListener.onClick(getRootView());
                }
            }
            
        });
        mWMParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(getRootView(), mWMParams);
    }
    
    public void removeWindow() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(getRootView());
    }
    
    
    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mFirstX = ev.getX();
            mFirstY = ev.getY();
            mIsClicked = true;
//            Log.v(TAG, "mIsClicked="+mIsClicked);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            mWMParams.x += (int)ev.getX()-mFirstX;
            mWMParams.y += (int)ev.getY()-mFirstY;
            float abs = (ev.getX()-mFirstX)*(ev.getX()-mFirstX)
                    + (ev.getY()-mFirstY)*(ev.getY()-mFirstY);
            if (abs > THRESHOLD) {
                mIsClicked = false;
//                Log.v(TAG, "mIsClicked="+mIsClicked);
            }
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.updateViewLayout(getRootView(), mWMParams);
        }
        return super.dispatchTouchEvent(ev);
    }
    
    public interface OnClickListener {
        public void onClick(View rootview);
    }
    
    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }
}
