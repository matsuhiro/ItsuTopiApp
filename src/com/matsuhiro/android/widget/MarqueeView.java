package com.matsuhiro.android.widget;

import com.matsuhiro.android.itsutopi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


public class MarqueeView extends View {
	public static final int INFINIT = -1;
    private Paint mTextPaint;
    private String mText;
    private int mAscent;

    private int mRepeatCount;
    private int mRepeatLimit;
    private int mCurrentX;
    private int mTextMoveSpeed;
    private Thread mThread = null;

    private Runnable runnable = new Runnable() {
        public void run() {
            int lastX = getLastX();

            while(mRepeatLimit == INFINIT || mRepeatCount < mRepeatLimit) {
                mCurrentX = getMarqueeStartX();

                long beforeTime = System.currentTimeMillis();
                long afterTime = beforeTime;
                int fps = 30;
                long frameTime = 1000 / fps;

                while(true) {               
                    if(mCurrentX <= lastX) {
                        mRepeatCount += 1;
                        break;
                    }

                    mCurrentX -= mTextMoveSpeed;
                    postInvalidate();
                
                    afterTime = System.currentTimeMillis();
                    long pastTime = afterTime - beforeTime;
                    
                    long sleepTime = frameTime - pastTime;
                    
                    if(sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        }catch(Exception e){}
                    }
                    beforeTime = System.currentTimeMillis();
                }
                
            }
        }
    };
    
    public void clearMarquee() {
        mCurrentX = getMarqueeStartX();
        mRepeatCount = 0;
        mThread = null;
    }

    public void startMarquee() {
        clearMarquee();
        mThread = new Thread(runnable);
        mThread.start();
    }

    public boolean isStarted() {
        if (mThread == null) {
            return false;
        } else {
            return true;
        }
    }
    public MarqueeView(Context context) {
        super(context);
        initMarqueeView();
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMarqueeView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);

        String s = a.getString(R.styleable.MarqueeView_text);
        if(s != null) {
            setText(s);
        }

        int textSize = a.getDimensionPixelOffset(R.styleable.MarqueeView_textSize, 0);
        if (textSize > 0) {
            setTextSize(textSize);
        }

        setTextColor(a.getColor(R.styleable.MarqueeView_textColor, 0xFFFFFFFF));
        setBackgroundColor(a.getColor(R.styleable.MarqueeView_background, 0xFF000000));
        setRepeatLimit(a.getInteger(R.styleable.MarqueeView_repeatLimit, 1));
        setTextMoveSpeed(a.getInteger(R.styleable.MarqueeView_textMoveSpeed, 5));

        a.recycle();
    }

    private final void initMarqueeView() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(16);
        mTextPaint.setColor(0xFFFFFFFF);
        mTextMoveSpeed = 5;
        mRepeatCount = 0;
        setRepeatLimit(1);
        setText("");
        setPadding(0, 0, 0, 0);
        setBackgroundColor(0xFF000000);
    }
    
    public void setRepeatLimit(int repeatLimit) {
        if(repeatLimit > 0) {
            mRepeatLimit = repeatLimit;
        } else if (repeatLimit == INFINIT) {
        	mRepeatLimit = INFINIT;
        } else {
            mRepeatLimit = 1;
        }
    }
    
    public void setTextMoveSpeed(int speed) {
        if(speed > 0) {
            mTextMoveSpeed = speed;
        }
    }

    public void setText(String text) {
        mText = text;
        requestLayout();
        invalidate();
    }

    public void setTextSize(int size) {
        mTextPaint.setTextSize(size);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            measureWidth(widthMeasureSpec),
            measureHeight(heightMeasureSpec)
        );
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = (int) mTextPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        mAscent = (int) mTextPaint.ascent();
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
    private int getMarqueeStartX() {
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int measureText = (int)mTextPaint.measureText(mText);
        int measureWidth = getMeasuredWidth();

        if(display.getWidth() == measureWidth) {
            return measureWidth;
        }else if(measureText > display.getWidth()) {
            return display.getWidth();
        }else if(measureWidth > measureText) {
            return measureWidth;
        }else {
            return measureText;
        }
    }
    
    private int getLastX() {
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
    
        int measureText = (int)mTextPaint.measureText(mText);
        int measureWidth = getMeasuredWidth();

        if(measureText >= display.getWidth()) {
            return -measureText;
        }else if(measureWidth > measureText) {
            return -measureWidth;
        }else {
            return -measureText;
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int x = getPaddingLeft() + mCurrentX;
        int y = getPaddingTop() - mAscent;
        canvas.drawText(mText, x, y, mTextPaint);
    }
}