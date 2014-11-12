package com.example.charminarrestore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class DisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display);
	//	ImageView origView = new ImageView(this);
		//((ImageView) findViewById(R.id.augment)).setImageBitmap(BitmapFactory.decodeFile("/sdcard/charminarAR/mbgra.jpg"));
	//	origView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/charminarAR/mbgra.jpg"));
		AnimeView1 drawInst = new AnimeView1(this);
		//addContentView(origView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		setContentView(drawInst);
	//	drawInst.bringToFront();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}

	public class AnimeView1 extends View{
		Context pContext;
		Bitmap temp;
		int width,height;
		public AnimeView1(Context context){
			super(context);
			pContext = context;
			width=0;
		}
		public void setCameraDims(int w, int h){
			width = w; height = h;
		}
		@Override
		protected void onDraw(Canvas canvas){
			canvas.drawColor(Color.TRANSPARENT);
			float dx = (canvas.getWidth() - width)/2;
			float dy = (canvas.getHeight() - height)/2;
			//	canvas.translate(dx/2, dy/2);
			Log.i("Canvas translating: ", Float.toString(dx) + " " + Float.toString(dy));
			// canvas.scale(sx, sy);
			Log.i("AnimeView","Drawing again");
			super.onDraw(canvas);
			try{
				 Bitmap temp1 = BitmapFactory.decodeFile("/sdcard/charminarAR/mbgra.jpg");
					canvas.drawBitmap(temp1, 0, 0, null);


				 Bitmap temp = BitmapFactory.decodeFile("/sdcard/charminarAR/mbgra_overlay.png");
				canvas.drawBitmap(temp, 0, 0, null);
			}catch(Exception e){
			}
			this.invalidate();
		}
	}
}
