package cn.yuelei.timerecorder.cameramodule;

import android.content.Context;
import android.hardware.Camera;

public class CameraWrapper {
    private static final String TAG = "meeeCameraWrapper";
    private Context mContext;
    private Camera mCamera;
    
    public CameraWrapper(Context context){
        mContext = context;
    }
}
