package cn.yuelei.timerecorder.ui;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cn.yuelei.timerecorder.R;
import cn.yuelei.timerecorder.cameramodule.CameraWrapper;
public class MainActivity extends Activity {
    private static final String TAG="meeeMActivity";
    private Context mContext;
    private ShutterButton mButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private CameraWrapper mCameraWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_main);
        mContext = this;
        mButton = (ShutterButton)findViewById(R.id.button1);
        mButton.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButton.toggleClickState();
                mButton.invalidate();
                return false;
            }
        });
        mButton.setFocusable(true);
        initSurface();
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
            mCameraWrapper.setParameters(parameters); 
            mCameraWrapper.setDisplayOrientation(90);
            mCameraWrapper.startPreview(); 
            mCameraWrapper.setAutoFocus(true,new Camera.AutoFocusCallback() {
                
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.v(TAG, "Auto focus " + success);
                }
            }); 
        } catch (Exception e) {
            Log.v(TAG, "open Camera failed");
            e.printStackTrace();
        }
    }
    
    private void closeCamera(){
        if (mCameraWrapper != null) {
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

}
