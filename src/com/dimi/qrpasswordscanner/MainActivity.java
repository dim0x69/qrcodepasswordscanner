package com.dimi.qrpasswordscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    protected ImageScanner scanner;

    static {
        System.loadLibrary("iconv");
    } 
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intialize the QRCode-Scanner. 
        // Used in previewCallback class
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // create the SurfaceView and add it to the FrameLayout
        mPreview = new CameraPreview(this, mCamera, previewCallback);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    

    @Override
    protected void onPause() {
    	super.onPause();
    	// release the Camera in the SurfaceView and here
    	mPreview.setCamera(null);
    	mCamera.release();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// re-initiate the Camera
    	mCamera = getCameraInstance();
    	mPreview.setCamera(mCamera);
    }
    
    public static Camera getCameraInstance(){
    	// Somehow safe way to get a Camera instance
    	Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        return c;
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.action_settings:
		    	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		    	if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){	
		    		startActivity(new Intent(this, SettingsActivity.class));
		    	} else{
		    		startActivity(new Intent(this, OldSettingsActivity.class));
		    	}
    	}
		return true;
    }   

    /**
     * Called from the Camera-instance to deliver preview frames
     * we try to find QRCodes in this frames. 
     * 
     * if we find one we stop the preview, copy the found string to the clipboard and start
     * a background thread to clear the clipboard after a specified time
     * 
     * if we find multiple values we take the first
     */
    PreviewCallback previewCallback = new PreviewCallback() {
        @SuppressWarnings("deprecation")
		public void onPreviewFrame(byte[] data, Camera camera) {
        	Log.d("blubb","blubb");
        	Size size;
        	try{
        		Camera.Parameters parameters = camera.getParameters();
        		size = parameters.getPreviewSize();
        	}catch(Exception e){
        		return;
        	}

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            
            if (result != 0) {
            	try{
            		camera.setPreviewCallback(null);
            		camera.stopPreview();
            	}catch(Exception e){}
                
                SymbolSet syms = scanner.getResults();
                if(syms.size() > 1)
                	Toast.makeText(getApplicationContext(), "multiple results found. I'll take the first.", Toast.LENGTH_SHORT).show();
                Object[] symArray = syms.toArray();
                Symbol sym = (Symbol)symArray[0];
                symArray = null;
                
                SharedPreferences sharedPrefManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int wait;
                try{
                	wait = Integer.parseInt(sharedPrefManager.getString("clear_wait", "30"));
                }catch(NumberFormatException e){
                	wait = 30;
                }

                Toast.makeText(getApplicationContext(), "clearing in "+Integer.toString(wait)+" seconds",Toast.LENGTH_SHORT).show();
                
            	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            	if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){	
            		android.content.ClipboardManager clipboardM = (android.content.ClipboardManager) getSystemService("clipboard");
            		clipboardM.setPrimaryClip(ClipData.newPlainText("", sym.getData()));
            	} else{
            		android.text.ClipboardManager clipboardM = (android.text.ClipboardManager) getSystemService("clipboard");
            		clipboardM.setText(sym.getData());
            	}
            	sym = null;
            	Handler h = new Handler();
            	
            	final Runnable r = new Runnable() {
					
					@Override
					public void run() {
						
		               	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	                	if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
	                		android.content.ClipboardManager clipboardM = (android.content.ClipboardManager) getSystemService("clipboard");
	                		clipboardM.setPrimaryClip(ClipData.newPlainText("", ""));
	                	}else{
	                		android.text.ClipboardManager clipboardM = (android.text.ClipboardManager) getSystemService("clipboard");
	                		clipboardM.setText("");
	                	}
						Toast.makeText(getApplicationContext(), "Clipboard cleared.",Toast.LENGTH_LONG).show();
					}
				};
				
				h.postDelayed(r,wait*1000);
			}
        }
        
    };
}