package com.test.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.example.test.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class PaperDetectionActivity extends Activity implements
		OnTouchListener, CvCameraViewListener2 {
	private static final String TAG = "PaperDetectionActivity";

	private MenuItem saveImage;

	/* Basic Variables */
	private final int ACCUMULATOR_THRESHOLD = 50;
	private final int MINLINELENGTH = 100;
	private final int MAXLINEGAP = 80;
	private final int HYSTERESIS_THRESHOLD1 = 50;
	private final int HYSTERESIS_THRESHOLD2 = 400;
	private final int HOUGH_LINE_COUNT = 5;

	/* OpenCv Variables */
	private Mat mRgba;
	private Mat mGray;
	private Mat lines;
	private Tutorial3View mOpenCvCameraView;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView
						.setOnTouchListener(PaperDetectionActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.color_blob_detection_surface_view);
		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		lines = new Mat();

	}

	@Override
	public void onCameraViewStopped() {
		mRgba.release();

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		Mat cannyMat = new Mat();
		mGray.submat(1, mGray.rows() - 1, 1, mGray.cols() - 1).copyTo(cannyMat);
		Imgproc.Canny(mGray, cannyMat, HYSTERESIS_THRESHOLD1,
				HYSTERESIS_THRESHOLD2, 3, false);
		Imgproc.HoughLinesP(cannyMat, lines, 1, Math.PI / 180,
				ACCUMULATOR_THRESHOLD, MINLINELENGTH, MAXLINEGAP);

		for (int x = 0; x < lines.cols() && x < HOUGH_LINE_COUNT; x++) {
			double[] vec = lines.get(0, x);
			if (vec != null) {
				double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
				Point start = new Point(x1, y1);
				Point end = new Point(x2, y2);
				Core.line(mRgba, start, end, new Scalar(255, 0, 0), 3);
			}
		}
		return mRgba;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item == saveImage) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String currentDateandTime = sdf.format(new Date());
			String fileName = Environment.getExternalStorageDirectory()
					.getPath()
					+ "/Pictures/MyCameraApp/"
					+ File.separator
					+ "IMG_" + currentDateandTime + ".jpg";
			Highgui.imwrite(fileName, mRgba);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		saveImage = menu.add("Save Image");
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO 处理点击事件
		return false;
	}

}
