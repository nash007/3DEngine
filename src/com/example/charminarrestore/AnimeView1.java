package com.example.charminarrestore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

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
	        canvas.translate(dx, dy);
	        Log.i("Canvas translating: ", Float.toString(dx) + " " + Float.toString(dy));
//	        canvas.scale(sx, sy);
	        Log.i("AnimeView","Drawing again");
	        super.onDraw(canvas);
	        try{
//	          temp = BitmapFactory.decodeFile("/sdcard/charminarAR/overlay3_1.png");
	            canvas.drawBitmap(Sample3View.bmp_overlay, 0, 0, null);
	        }catch(Exception e){
	            
	        }

	        this.invalidate();
	    }
	}