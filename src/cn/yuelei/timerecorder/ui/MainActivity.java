package cn.yuelei.timerecorder.ui;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.yuelei.timerecorder.R;
import cn.yuelei.timerecorder.cameramodule.CameraWrapper;
public class MainActivity extends Activity {
    private static final String TAG="meeeMActivity";
    private Context mContext;
    private ShutterButton mButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private TextView mTimeView;
    private CameraWrapper mCameraWrapper;
    private boolean mIsShutButtonClicked = false;
    private TimeUpdater mTimeUpdater;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_main);
        mContext = this;
        mHandler = new Handler();
        mTimeView = (TextView)findViewById(R.id.recorded_time);
        initShutterButton();
        initSurface();
    }

    private void initShutterButton() {
        mButton = (ShutterButton)findViewById(R.id.button1);
        /*mButton.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButton.toggleClickState();
                mButton.invalidate();
                return false;
            }
        });*/
        mButton.setFocusable(true);
        mButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.v(TAG, "s on clicked");
                if (!mIsShutButtonClicked) {
                    mButton.toggleOn();
                    mButton.invalidate();
                    openUpdater();
                    mCameraWrapper.setOnPreviewAvailable(new CameraWrapper.OnPreviewAvailableListener() {
                        
                        @Override
                        public void onPreviewAvailable(long timeUs, byte[] yuvBuff, int width,
                                int height, int format) {
                            Log.v(TAG, "preview@"+ timeUs + " w/h=" + width+"/"+height);
                            yuvBuff = null;
                            
                        }
                    });
                    mIsShutButtonClicked = true;
                    //test();
                }else{
                    mButton.toggleOff();
                    mButton.invalidate();
                    hideUpdater();
                    mIsShutButtonClicked = false;
                }
                
            }
        });
    }
    
    private void initSurface(){
        if (mSurfaceView == null) {
            mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
           
        }
        
        if (mSurfaceHolder == null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    closeCamera();
                }
                
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    openCamera();
                }
                
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width,
                        int height) {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
    }

    private void openCamera() {
        closeCamera();
        if (mCameraWrapper == null) {
            mCameraWrapper = new CameraWrapper(mContext);
        }
        try {
            mCameraWrapper.setSurface(mSurfaceHolder); 
            Camera.Parameters parameters = mCameraWrapper.getParameters(); 
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewFpsRange(10000, 10000);
            parameters.setPreviewSize(1920, 1080);
            //parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCameraWrapper.setParameters(parameters); 
            mCameraWrapper.setDisplayOrientation(90);
            mCameraWrapper.startPreview(); 
            mCameraWrapper.setAutoFocus(true,new Camera.AutoFocusCallback() {
                
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.v(TAG, "Auto focus " + success);
                    if (success) {
                        mCameraWrapper.getCamera().cancelAutoFocus();
                    }
                }
            }); 
        } catch (Exception e) {
            Log.v(TAG, "open Camera failed");
            e.printStackTrace();
        }
    }
    
    private void closeCamera(){
        if (mCameraWrapper != null) {
            mCameraWrapper.stopPreview();
            mCameraWrapper.release();
            mCameraWrapper = null;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        
        Log.v(TAG,"onKeyDown---0");
        mButton.toggleOff();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsShutButtonClicked = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsShutButtonClicked = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsShutButtonClicked = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsShutButtonClicked = false;
        mButton.toggleOff();
        mButton.invalidate();
        hideUpdater();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsShutButtonClicked = false;
    }
    
    private void test() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    byte[] previewData = new byte[1920 * 1080 * 3 / 2];
                    mCameraWrapper.signalForPreview(previewData);
                    Log.v(TAG, "apply for preview " + i);
                    SystemClock.sleep(500);
                }
            }
        }).start();

    }
    
    private void openUpdater() {
        if (mTimeUpdater != null) {
            mTimeUpdater.stop();
            mTimeUpdater = null;
        }
        if (mTimeView == null) {
            mTimeView = (TextView) findViewById(R.id.recorded_time);
        }
        mTimeView.setVisibility(View.VISIBLE);
        mTimeUpdater = new TimeUpdater(mContext, 1000,
                new TimeUpdater.OnUpdateListener() {
                    @Override
                    public void onUpdate(String time) {
                        final String nowtime = time;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mTimeView != null) {
                                    mTimeView.setText(nowtime);
                                }
                            }
                        });

                    }
                });
        mTimeUpdater.start();
    }

    private void hideUpdater() {
        if (mTimeUpdater != null) {
            mTimeUpdater.stop();
        }
        if (mTimeView != null) {
            mTimeView.setVisibility(View.INVISIBLE);
        }
    }
    

    
    
}
