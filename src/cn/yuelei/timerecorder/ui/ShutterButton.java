package cn.yuelei.timerecorder.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyCharacterMap.KeyData;
import android.view.KeyEvent;
import android.view.View;
import cn.yuelei.timerecorder.R;
public class ShutterButton extends View {
    private static final String TAG = "SB";
    private Context mContext;
    private Handler mHandler;
    private boolean mIsClicked = false;
    private boolean mIsBGdrawed = false;
    private boolean mIsDetachedFromWindow = false;
    private long mTime;
    private int mNowPointIndex = 0;
    private static int  mPointerNums = 48;
    private static double[] mCosTabs = new double[mPointerNums];
    private static double[] mSinTabs = new double[mPointerNums];
    
    private Paint mPaint = new Paint();
    
    private Thread mThread;
     
    private void  backRefresh() {
        mThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                Log.v(TAG, "backRefresh run");
                while (!mIsDetachedFromWindow) {
                    if (mIsClicked) {
                        if (mIsBGdrawed) {
                            mNowPointIndex++;
                        }
                        synchronized (this) {
                            mHandler.sendEmptyMessage(0xff);
                        }
                       SystemClock.sleep(100);
                    }
                }
                Log.v(TAG, "backRefresh end");
            }
        });
        mThread.start();
    }
    
    static{
        mPointerNums = 48;
        for (int i = 0; i < mPointerNums; i++) {
            double angle = Math.toRadians(90+360.0/48*i);
            mCosTabs[i] = Math.cos(angle); 
            mSinTabs[i] = Math.sin(angle);
        }
    }

    public ShutterButton(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mPaint.setAlpha(180);
        this.setBackgroundResource(R.drawable.off);
        registerHandler();
    }

    public ShutterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint.setAlpha(180);
        this.setBackgroundResource(R.drawable.off);
        registerHandler();
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint.setAlpha(180);
        this.setBackgroundResource(R.drawable.off);
        registerHandler();
    }

    public ShutterButton(Context context) {
        super(context);
        mContext = context;
        mPaint.setAlpha(180);
        this.setBackgroundResource(R.drawable.off);
        registerHandler();
    }
    
    private void registerHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0xff) {
                    postInvalidate();
                }
            };
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        Log.v(TAG, "w/h@" + width + "/" + height);
        int r = (int)(centerX * 3.0f / 4);
        int circleInner = r+10;
        int circleOuter = r+30;
        int squireWidth = (int)(r * 9.0f/20);
        if (!mIsClicked) {
            mPaint.setColor(Color.RED);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(7);
            canvas.drawCircle(centerX, centerY, r+8, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerY, r-2, mPaint);
            Log.v(TAG, "draw C");
            mIsBGdrawed = false;
        } else {
            if (!mIsBGdrawed) {
//                mPaint.setStyle(Paint.Style.FILL);
//                mPaint.setColor(Color.RED);
//                canvas.drawRoundRect(centerX - squireWidth, centerY
//                        - squireWidth, centerX + squireWidth, centerY
//                        + squireWidth, 8, 8, mPaint);
//                drawPrinter(canvas); 
                mIsBGdrawed = true;
            }else if(!mIsDetachedFromWindow)
            {
                mPaint.setColor(Color.WHITE);
                int p = mNowPointIndex % 48;
                mPaint.setStrokeWidth(5);
                canvas.drawLine((float) (centerX + circleInner * mCosTabs[47-p]),
                        (float) (centerY - circleInner * mSinTabs[47-p]),
                        (float) (centerX + circleOuter * mCosTabs[47-p]),
                        (float) (centerY - circleOuter * mSinTabs[47-p]), mPaint);

            }
           
        }
    }

    public void toggleClickState() {
        mIsClicked = !mIsClicked;
        if (mIsClicked) {
            mNowPointIndex = 0;
            mIsDetachedFromWindow = false;
            this.setBackgroundResource(R.drawable.on);
            backRefresh();
        }else{
            this.setBackgroundResource(R.drawable.off); 
            mIsDetachedFromWindow = true;
        }
        
    }
    
    public void toggleOn() {
        mIsClicked = true;
        if (mIsClicked) {
            mNowPointIndex = 0;
            mIsDetachedFromWindow = false;
            this.setBackgroundResource(R.drawable.on);
            backRefresh();
        }else{
            this.setBackgroundResource(R.drawable.off); 
            mIsDetachedFromWindow = true;
        }
        
    }
    
    
    public void toggleOff() {
        mIsClicked = false;
        if (mIsClicked) {
            mNowPointIndex = 0;
            mIsDetachedFromWindow = false;
            this.setBackgroundResource(R.drawable.on);
            backRefresh();
        }else{
            this.setBackgroundResource(R.drawable.off); 
            mIsDetachedFromWindow = true;
        }
        
    }
    
    
    private void drawPrinter(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int r = centerX * 3 / 4;
        int circleInner = r-4;
        int circleOuter = r+8;

        for(int i=0;i<48;i++){
            if (i%4 == 0) {
                mPaint.setStrokeWidth(3);
                canvas.drawLine((float)(centerX+(circleInner-8)*mCosTabs[i]),
                        (float)(centerY-(circleInner-8)*mSinTabs[i]), 
                        (float)(centerX+circleOuter*mCosTabs[i]),
                        (float)(centerY-circleOuter*mSinTabs[i]),mPaint);
          
            } else {
                mPaint.setStrokeWidth(2);
               canvas.drawLine((float)(centerX+circleInner*mCosTabs[i]),
                    (float)(centerY-circleInner*mSinTabs[i]), 
                    (float)(centerX+circleOuter*mCosTabs[i]),
                    (float)(centerY-circleOuter*mSinTabs[i]),mPaint);
       
            }
              }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        Log.v(TAG, "onDetachedFromWindow@"+this);
        mIsDetachedFromWindow = true;
        super.onDetachedFromWindow();
    }
    
    
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility==View.INVISIBLE || visibility==View.GONE) {
            mIsDetachedFromWindow = true;
            mIsClicked = false;
            synchronized (this) {
               mHandler.removeMessages(0xff); 
            }
            this.setBackgroundColor(0x00000000);
            this.setBackgroundResource(R.drawable.off); 
            Log.v(TAG, "onVisibility inv");
        }
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.v(TAG, "onKeyDown ");
        switch (keyCode) {
        case KeyEvent.KEYCODE_POWER:
            ;
        case KeyEvent.KEYCODE_BACK:
            ;
        case KeyEvent.KEYCODE_HOME: {
            mIsDetachedFromWindow = true;
            mIsClicked = false;
            synchronized (this) {
                mHandler.removeMessages(0xff);
            }
            this.setBackgroundColor(0x00000000);
            this.setBackgroundResource(R.drawable.off);
            break;
        }

        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    public boolean isClicked() {
        return mIsClicked;
    }
    
    
    
   

}
