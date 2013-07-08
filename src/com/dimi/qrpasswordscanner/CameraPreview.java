/*
 * Barebones implementation of displaying camera preview.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.dimi.qrpasswordscanner;

import java.io.IOException;
import java.util.List;

import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String TAG = "CameraPreview";
	private PreviewCallback previewCallback;

    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera, PreviewCallback pCallback) {
        super(context);
        this.mCamera = camera;
        previewCallback = pCallback;
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    
    

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    	if (mHolder.getSurface() == null){
          return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){}
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    
    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }
        
        stopPreviewAndFreeCamera();
        
        mCamera = camera;
        
        if (mCamera != null) {
            requestLayout();
          
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                e.printStackTrace();
            }
          
            mCamera.startPreview();
        }
    }
    
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
        	mCamera.stopPreview();
            mCamera.setPreviewCallback(null);        
            mCamera.release();
            mCamera = null;
        }
    }
}