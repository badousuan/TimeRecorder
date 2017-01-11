package cn.yuelei.timerecorder.cameramodule;

import java.io.IOException;

import android.R.integer;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraWrapper {
    private static final String TAG = "meeeCameraWrapper";
    private Context mContext;
    private Camera mCamera;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mPreviewFormat;
    private int mCaneraState = -1;
    private OnPreviewAvailableListener mListener;

    public interface OnPreviewAvailableListener {
        public void onPreviewAvailable(long timeUs, byte[] yuvBuff, int width,
                int height, int format);
    }

    @SuppressWarnings("deprecation")
    public CameraWrapper(Context context) {
        mContext = context;
        mCamera = Camera.open();
        if (mCamera == null) {
            Log.v(TAG, "camera open failed");
        }
    }

    public void setSurface(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null && mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                Log.v(TAG, "setSurface failed");
                e.printStackTrace();
            }
        }
    }

    public Parameters getParameters() {
        return mCamera.getParameters();
    }
    
    public Camera getCamera() {
        return mCamera;
    }

    public void setParameters(Parameters parameters) {
        mCamera.setParameters(parameters);
        mPreviewWidth = parameters.getPreviewSize().width;
        mPreviewHeight = parameters.getPreviewSize().height;
        mPreviewFormat = parameters.getPictureFormat();
        setOnPreviewAvailable();
    }
    
    @SuppressWarnings("deprecation")
    public void setAutoFocus(boolean isAutoFoucs, AutoFocusCallback cb) {
        if (isAutoFoucs && mCamera != null) {
            mCamera.autoFocus(cb);
        } else {
            mCamera.autoFocus(null);
        }
    }
    
    public void setDisplayOrientation(int degrees) {
        if (mCamera!= null) {
            mCamera.setDisplayOrientation(degrees);
        }
    }
    

    public void setOnPreviewAvailable(OnPreviewAvailableListener lsr) {
        mListener = lsr;
    }
    
    public void signalForPreview(byte[] callbackBuffer) {
        mCamera.addCallbackBuffer(callbackBuffer);
    }

    @SuppressWarnings("deprecation")
    private void setOnPreviewAvailable() {
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                long nowTime = SystemClock.currentThreadTimeMillis() * 1000;
                if (mListener != null) {
                    mListener.onPreviewAvailable(nowTime, data, mPreviewWidth,
                            mPreviewHeight, mPreviewFormat);

                }
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }else{
            Log.v(TAG, "start preview failed");
        }
    }
    
    @SuppressWarnings("deprecation")
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }else{
            Log.v(TAG, "start preview failed");
        }
    }
    
    public void release() {
        if (mCamera != null) {
            mListener = null;
            mCamera.stopPreview();
            mCamera.release();
        }
    }
    
    
}
