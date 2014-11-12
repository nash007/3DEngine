package com.example.charminarrestore;


import java.io.File;

import wazzatimagescanner.WLAuthenticate;
import wazzatimagescanner.WLAuthenticateListener;
import wazzatimagescanner.WLClientAcitivityInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements WLAuthenticateListener{
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri fileUri;
	private JNIlib jnilib;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
		jnilib = new JNIlib(0);
		Button start = (Button)findViewById(R.id.startButton);
		
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				    
				    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

				    // start the image capture Intent
				    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		WLClientAcitivityInfo.setContext(this);		
	    
		WLAuthenticate.Authenticate("46d7c87c585d8b33cf13" ); // TOKEN_TEST is the token. User must pass a valid token here.		//
	}
	
	////////////////////////////////////////////////////////////////////
	
	
	public void onAuthenticationSuccess() {
	Toast.makeText(this, "Authentication is Successful", Toast.LENGTH_LONG).show();		
}

// This is called on onProgressUpdate() method of Async Task.
@Override
public void whileAuthenticating() {
	// Do something while authentication is completing.
}

// Pass your own ProgressBar object to record the progress of the authentication.
// Note: Integer valud of progress is between 1-100


// This function is called when authorization is Failed.
// Argument FailureCode tells the reason behind the failure as shown.
//	DOWNLOAD_FAILED = 1;
//	DATA_LOAD_FAILED = 2;
//	TOKEN_EXPIRED = 3;
	public void onAuthenticationFailure(int FailureCode) {
	Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show();          		
}

////////////////////////////////////////////////////////////////////
	
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_OUT.jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	int pFlag;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            
	            ProgressDialog pd = new ProgressDialog(this);
	            
	            Thread t = new Thread(new Runnable() {
	                public void run() {
	                	pFlag = jnilib.FindFeatures(true);
	                	
	                }
	            });
	            pd.show();
	            t.start();
	            try {
					t.join();
					Toast.makeText(MainActivity.this, "Image saved to:\n" +
    	            		fileUri.getPath()+ "\npFlag = "+ String.valueOf(pFlag), Toast.LENGTH_LONG).show();
					Intent i = new Intent(this, DisplayActivity.class);
					startActivity(i);
					pd.dismiss();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}//previewWidtd, previewHeight, stream.toByteArray(), rgba, rgba_overlay, true);//, touchX, touchY, currStep);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAuthenticationDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProgressBar getProgressBar() {
		// TODO Auto-generated method stub
		return null;
	}

}
