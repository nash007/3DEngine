package com.example.charminarrestore;

public class JNIlib {
	public JNIlib(int flagS) {
		System.loadLibrary("opencv_java");
        System.loadLibrary("EngineAR");
        this.LoadSource(flagS);
	}
    public native int LoadSource(int flagS);
    public native int FindFeatures(boolean addRemovePt);//int width, int height, byte yuv[], int[] rgba, int[] rgba_overlay, );
}
