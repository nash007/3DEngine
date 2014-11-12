#include <vector>
#include <stdio.h>
#include <algorithm>
#include <iostream>
#include <ctime>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <string>
#include <map>
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/nonfree/features2d.hpp"
#include "opencv2/ml/ml.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include "opencv2/video/tracking.hpp"
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include <fstream>
#include <android/log.h>
#include <jni.h>

#define SDCARD_PATH "/sdcard/"
//#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))

using namespace std;
using namespace cv;

TermCriteria termcrit(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03);
Size winSize(10,10);

Point2f pt;

Mat img_scene, img_overlay, cur_overlay;
vector<Point2f> points[2];
int flag = 0;

/** create a mask for the source img_objects to highlight the obj regions **/
Mat source_mask;

/** detect SIFT keypoints in the mask regions of source img_objects and in the scene image**/
FastFeatureDetector detector;
SiftFeatureDetector detector1;
vector<KeyPoint> keypoints_source;
vector<KeyPoint> keypoints_scene;

/** compute SIFT descriptors in the source img_objects and in the scene image**/
SiftDescriptorExtractor extractor;
Mat descriptors_source, descriptors_scene, rem_H;

void  HomographicTransformation( vector<Point2f>& obj, vector<Point2f>& scene ){
	vector<uchar> inliers;
	Mat H = findHomography( obj, scene, CV_RANSAC, 5, inliers );
	int count = 0;
	for( int i = 0 ; i < obj.size() ; i++ ){
		if( !inliers[i] ){
			obj.erase( obj.begin() + i );
	 		scene.erase( scene.begin() + i );
	 	}
	 	else {
	 		count++;
	 	}
	}
	if( count > 10 ) {
		rem_H = rem_H * H;
	 	warpPerspective(img_overlay, cur_overlay, rem_H, img_scene.size());
	 	imwrite("/sdcard/charminarAR/curOverlay.jpg", cur_overlay);
	 	flag = 1;
	}
	else {
		flag = 0;
	}
}

// Gets the SIFT keypoints of the image
vector< Point2f > getKeys( Mat& img_scene ){
	detector.detect( img_scene, keypoints_scene );
	//LOGI("Keypoints detected");

	vector<Point2f> currFrameKeys;
	for( int i = 0 ; i < keypoints_scene.size() ; i++ ){
		currFrameKeys.push_back(keypoints_scene[i].pt);
	}
	return currFrameKeys;
}

void Detect( Mat& img_scene ){
	//LOGI("starting object detection");
	detector1.detect( img_scene, keypoints_scene );
	//LOGI("Keypoints detected");

	extractor.compute( img_scene, keypoints_scene, descriptors_scene );
	//LOGI("Descriptors extracted");

	FlannBasedMatcher matcher;
	std::vector< DMatch > matches;
	matcher.match( descriptors_source, descriptors_scene, matches );
	//LOGI("Matching done");

	//-- Quick calculation of max and min distances between keypoints
	double min_dist=1000, max_dist;
	for( int i = 0; i < descriptors_source.rows; i++ )
	{ double dist = matches[i].distance;
	if( dist < min_dist ) min_dist = dist;
	if( dist > max_dist ) max_dist = dist;
	}

	//-- Draw only "good" matches (i.e. whose distance is less than 3*min_dist )
	std::vector< DMatch > good_matches;

	for( int i = 0; i < descriptors_source.rows; i++ )
	{
		if( matches[i].distance <= 4*min_dist ) {
		good_matches.push_back( matches[i]);
		}
	}

	// GEOM FILTER
	good_matches.clear();
	vector<uchar> inliers;
	vector<Point2f> pts1, pts2;
	for (int i = 0; i < matches.size(); i++) {
		pts1.push_back(keypoints_source[matches[i].queryIdx].pt);
		pts2.push_back(keypoints_scene[matches[i].trainIdx].pt);
	}
	Mat F = findFundamentalMat(Mat(pts1), Mat(pts2),
			FM_RANSAC, 3, 0.99, inliers);
	for (int i = 0; i < inliers.size(); i++) {
		if ( (int)inliers[i] ) {
			good_matches.push_back(matches[i]);
		}
	}

	//-- Localize the object
	std::vector<Point2f> obj;
	std::vector<Point2f> scene;

	for( int i = 0; i < good_matches.size(); i++ )
	{
		//-- Get the keypoints from the good matches
		obj.push_back( keypoints_source[ good_matches[i].queryIdx ].pt );
		scene.push_back( keypoints_scene[ good_matches[i].trainIdx ].pt );
	}

	//LOGI("Point Correspondence done");

	Mat img_matches;
	Mat img_object = imread("/sdcard/charminarAR/obj.jpg");
	drawMatches( img_object, keypoints_source, img_scene, keypoints_scene,
			good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
			vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );

	imwrite("/sdcard/charminarAR/matches2.jpg", img_matches);
	//LOGI("saved matches");

	HomographicTransformation(  obj, scene );
	points[1] = scene;
}

extern "C" {
JNIEXPORT jint JNICALL Java_com_example_charminarrestore_JNIlib_LoadSource(JNIEnv* env, jobject thiz, jint flagS )
{
	FileStorage fs("/sdcard/charminarAR/obj-kpts-desc.yml", FileStorage::READ);
	if (!fs.isOpened()) {
		//LOGI("Unable to open the object keypoints file!!!");
		return -1;
	}
	FileNode kpts = fs["kpts"];
	read(kpts, keypoints_source);
	FileNode desc = fs["desc"];
	read(desc, descriptors_source);
	fs.release();

	img_overlay = imread("/sdcard/charminarAR/overlay.png", -1);
	rem_H = Mat::eye(3,3, CV_64F);
	return 0;
}
}

extern "C" {
JNIEXPORT jint JNICALL Java_com_example_charminarrestore_JNIlib_FindFeatures(JNIEnv* env, jobject thiz,  jboolean addRemovePt)
{

	Mat temp = imread("/sdcard/Pictures/MyCameraApp/IMG_OUT.jpg");
	Mat mgray;
	Mat temp2 = imread("/sdcard/charminarAR/obj.jpg");
	resize(temp,mgray,temp2.size(),1.0,1.0,INTER_LINEAR);

	Mat mbgra_overlay(mgray.rows, mgray.cols, CV_8UC4);
	mgray.copyTo(img_scene);

	int pFlag =0;
	points[0].clear();
	points[1].clear();
	Detect( img_scene );
	imwrite("/sdcard/charminarAR/mgray.jpg", mgray);

	cvtColor(cur_overlay,mbgra_overlay,CV_RGB2BGRA);
	cur_overlay.copyTo(mbgra_overlay);
	imwrite("/sdcard/charminarAR/mbgra_overlay.png",mbgra_overlay);
	imwrite("/sdcard/charminarAR/mbgra.jpg", mgray);
	addRemovePt = false;
	pFlag = 10;
	return pFlag;
}
}
