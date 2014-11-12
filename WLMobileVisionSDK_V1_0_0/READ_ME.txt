Wazzat Lab Mobile Vision SDK (FOR ANDROID ONLY)
Version- 0.1.0 (beta beta)

How to include the library in your project ?

1) Copy the contents of libs folder in Android <project-root-folder>/libs.
	libs directory contains 3 files
		1) armeabi*/wlimagescanner.so
			Compiled library. This library is automatically included in the .apk file at the time of launch.			
		2) armeabi*/libopencv_java.so
			Opencv compiled libraries.This library is automatically included in the .apk file at the time of launch.
		3) WLSDK.jar
			When this is placed in libs directory, then android sdk should automatically pick it up and include it in its build path. If not then manually add this external jar file in the Java build path.						
2) Copy the content of assets folder in <project-root-folder>/assets.
3) Make sure to refresh the project root folder (when using Eclipse) so that Android sdk can pick up the new changes
4) Thats it! 



How to use it?

1) Files present in asset folder is used for data matching. If you have acquired your own dataset from the Wazzat Labs Dashboard, then make sure to replace the existing files with your new ones.

2) Use one of the following methods to start card scanning
/**
 * Scans the provided image matrix and returns the recognized digits.
* 
* @param width - Frame width of the image.
* @param height - Frame Height of the image
* @param frameData - Data matrix of the provided image. 
* @return Recognized text from the image.
*/
public static String scan(int width, int height, byte frameData[]);


/**
 * Scans the provided image from the provided path and sends the result back.
* 
* @param filePath - Absolute path of the image.
* @return Recognized text from the image.
*/
public static String scan(String filePath);


2) Steps to follow to start the scan

	Step 1) Copy the ocr_digits_svm.dat file (provided in asset folder) to the <project-root-folder>. SDK require this file to process the card images.
	
	Step 2) SDK require few information about the application before it can start processing. 
	        Client MUST call the following method before Scan method is called.
			
			WLClientAcitivityInfo.setContext(Context context);    
			
	Step 3) Now simply call the scan method as follows:
					
			1- String result = WLCardScanner.scan( int ImageWidth(),int ImageHeight(),byte[] ImageDate);
			
							or
							
			2- String result WLCardScanner.scan(String imagePath);
			

			
			