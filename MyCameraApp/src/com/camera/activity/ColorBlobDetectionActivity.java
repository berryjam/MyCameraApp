package com.camera.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.camera.control.ColorBlobDetector;
import com.camera.view.Tutorial3View;
import com.example.test.R;

public class ColorBlobDetectionActivity extends Activity implements
		OnTouchListener, CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";

	/* Basic Variables */
	private final int ACCUMULATOR_THRESHOLD = 100;
	private final int MINLINELENGTH = 100;
	private final int MAXLINEGAP = 80;
	private final int HYSTERESIS_THRESHOLD1 = 50;
	private final int HYSTERESIS_THRESHOLD2 = 400;
	private final int HOUGH_LINE_COUNT = 5;

	private boolean mIsColorSelected = false;
	private boolean getPoints = false;
	private boolean haveCalculated = false;
	private Mat mRgba;
	private Mat mGray;
	private Mat lines;
	private Mat edges;

	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	private double minX, minY, maxX, maxY;

	// Cache
	private Mat mPyrDownMat;
	private Mat mHsvMat;
	private Mat mMask;
	private Mat mDilatedMask;
	private Mat mHierarchy;

	// Lower and Upper bounds for range checking in HSV color space
	private Scalar mLowerBound = new Scalar(0);
	private Scalar mUpperBound = new Scalar(0);

	private MenuItem saveImage;

	private Tutorial3View mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView
						.setOnTouchListener(ColorBlobDetectionActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public ColorBlobDetectionActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.color_blob_detection_surface_view);

		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		saveImage = menu.add("Save Image");
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		if (item == saveImage) {
			getPoints = true;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String currentDateandTime = sdf.format(new Date());
			String fileName = Environment.getExternalStorageDirectory()
					.getPath()
					+ "/Pictures/MyCameraApp/"
					+ File.separator
					+ "IMG_" + currentDateandTime + ".jpg";
			Mat tmp = mRgba;
			Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGBA2BGR);
			Highgui.imwrite(fileName, tmp);
			Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT)
					.show();
		}
		return true;
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat();
		lines = new Mat();
		edges = new Mat();
		mDetector = new ColorBlobDetector();
		mSpectrum = new Mat();
		mBlobColorRgba = new Scalar(255);
		mBlobColorHsv = new Scalar(255);
		SPECTRUM_SIZE = new Size(200, 64);
		CONTOUR_COLOR = new Scalar(255, 0, 0, 255);

		mPyrDownMat = new Mat();
		mHsvMat = new Mat();
		mMask = new Mat();
		mDilatedMask = new Mat();
		mHierarchy = new Mat();
	}

	public void onCameraViewStopped() {
		mRgba.release();
		mGray.release();
		lines.release();
		edges.release();

		mPyrDownMat.release();
		mHsvMat.release();
		mMask.release();
		mDilatedMask.release();
		mHierarchy.release();
	}

	public boolean onTouch(View v, MotionEvent event) {
		int cols = mRgba.cols();
		int rows = mRgba.rows();

		int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
		int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

		int x = (int) event.getX() - xOffset;
		int y = (int) event.getY() - yOffset;

		Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

		if ((x < 0) || (y < 0) || (x > cols) || (y > rows))
			return false;

		Rect touchedRect = new Rect();

		touchedRect.x = (x > 4) ? x - 4 : 0;
		touchedRect.y = (y > 4) ? y - 4 : 0;

		touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols
				- touchedRect.x;
		touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows
				- touchedRect.y;

		Mat touchedRegionRgba = mRgba.submat(touchedRect);

		Mat touchedRegionHsv = new Mat();
		Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv,
				Imgproc.COLOR_RGB2HSV_FULL);

		// Calculate average color of touched region
		mBlobColorHsv = Core.sumElems(touchedRegionHsv);
		int pointCount = touchedRect.width * touchedRect.height;
		for (int i = 0; i < mBlobColorHsv.val.length; i++)
			mBlobColorHsv.val[i] /= pointCount;

		mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

		Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", "
				+ mBlobColorRgba.val[1] + ", " + mBlobColorRgba.val[2] + ", "
				+ mBlobColorRgba.val[3] + ")");

		mDetector.setHsvColor(mBlobColorHsv);

		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

		mIsColorSelected = true;

		touchedRegionRgba.release();
		touchedRegionHsv.release();

		return false; // don't need subsequent touch events
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		Imgproc.threshold(mGray, mGray, 145, 255, Imgproc.THRESH_BINARY_INV);
		Imgproc.GaussianBlur(mGray, mGray, new org.opencv.core.Size(5, 5), 5);

		Imgproc.Canny(mGray, edges, HYSTERESIS_THRESHOLD1,
				HYSTERESIS_THRESHOLD2);
		Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 80, 50, 10);

		if (mIsColorSelected && getPoints && !haveCalculated) {

			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(edges, contours, mHierarchy,
					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

			if (ColorBlobDetector.findMaxContourIdx(contours) != -1) {
				MatOfPoint iContour = contours.get(ColorBlobDetector
						.findMaxContourIdx(contours));
				List<Point> contourPoints = iContour.toList();
				int size = contourPoints.size();
				Collections.sort(contourPoints, new Comparator<Point>() {

					@Override
					public int compare(Point lhs, Point rhs) {
						return Double.compare(lhs.x, rhs.x);
					}
				});
				minX = contourPoints.get(0).x;
				maxX = contourPoints.get(size - 1).x;
				Collections.sort(contourPoints, new Comparator<Point>() {

					@Override
					public int compare(Point lhs, Point rhs) {
						return Double.compare(lhs.y, rhs.y);
					}
				});
				minY = contourPoints.get(0).y;
				maxY = contourPoints.get(size - 1).y;
				Log.i(TAG, "minX:" + minX + " maxX" + maxX + " minY" + minY
						+ " maxY" + maxY);
				Moments mMoments = Imgproc.moments(iContour);
				double x = mMoments.get_m10() / mMoments.get_m00();
				double y = mMoments.get_m01() / mMoments.get_m00();
				Point center = new Point(x, y);
				Core.circle(edges, center, 3, new Scalar(0, 0, 0), 3, 8, 0);
			}
			haveCalculated = true;
		}
		return mRgba;
	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
				4);

		return new Scalar(pointMatRgba.get(0, 0));
	}
}
