package com.camera.view.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.camera.utils.ScreenShot;
import com.camera.utils.Util;
import com.camera.view.MyGLSurfaceView;
import com.example.test.R;

public class MainActivity extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	private static final int IMAGE_SWITCHER_ACTIVITY_REQUEST_CODE = 300;
	private Uri fileUri;

	private ImageView camer;
	private ImageView file;
	private ImageView loadModel;
	private ImageView translate;
	private ImageView rotate;
	private ImageView scale;
	private ImageView threeDiemension;
	private ImageView background;
	private MyGLSurfaceView myGLSurfaceView;

	public static boolean capture = false;
	public static int w;
	public static int h;
	public static Bitmap captureBmp;
	private Display display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_layout);
		display = getWindowManager().getDefaultDisplay();
		myGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.view3d);
		background = (ImageView) findViewById(R.id.background);
		camer = (ImageView) findViewById(R.id.camer);
		camer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转到Camera界面
				// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				//
				// fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				// intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				//
				// startActivityForResult(intent,
				// CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

				Intent intent = new Intent(MainActivity.this,
						ColorBlobDetectionActivity.class);
				startActivity(intent);

				// Intent intent = new Intent(MainActivity.this,
				// PaperDetectionActivity.class);
				// startActivity(intent);
			}
		});

		file = (ImageView) findViewById(R.id.file);
		file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						ImageSwitcherAndGalleryActivity.class);
				startActivityForResult(intent,
						IMAGE_SWITCHER_ACTIVITY_REQUEST_CODE);
			}
		});

		loadModel = (ImageView) findViewById(R.id.load_model);
		loadModel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// FIXME 要根据用户选择加载obj模型
				Intent intent = new Intent(MainActivity.this,
						OpenGLES20Activity.class);
				startActivity(intent);
			}
		});

		translate = (ImageView) findViewById(R.id.translate);
		translate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myGLSurfaceView.setTranslateFlag();
			}
		});

		rotate = (ImageView) findViewById(R.id.rotate);
		rotate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myGLSurfaceView.setRotateFlag();
			}
		});

		scale = (ImageView) findViewById(R.id.scale);
		scale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myGLSurfaceView.setScaleFlag();
			}
		});

		threeDiemension = (ImageView) findViewById(R.id.three_diemension);
		threeDiemension.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// FIXME 功能没正确实现
				capture = true;
				if (captureBmp != null) {
					Bitmap bg = ((BitmapDrawable) background.getDrawable())
							.getBitmap();
					// ScreenShot.savePic(Util.toConformBitmap(bg, captureBmp),
					// new File(Util.getSavePath()));
					// ScreenShot.savePic(bg, new File(Util.getSavePath()));
					ScreenShot.savePic(captureBmp, new File(Util.getSavePath()));
				}
			}
		});
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		// if (resultCode == RESULT_OK) {
		// // Image captured and saved to fileUri specified in the Intent
		// Toast.makeText(this, "Image saved to:\n" + data.getData(),
		// Toast.LENGTH_LONG).show();
		// } else if (resultCode == RESULT_CANCELED) {
		// // User cancelled the image capture
		// } else {
		// // Image capture failed, advise user
		// }
		// }
		//
		// if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
		// if (resultCode == RESULT_OK) {
		// // Video captured and saved to fileUri specified in the Intent
		// Toast.makeText(this, "Video saved to:\n" + data.getData(),
		// Toast.LENGTH_LONG).show();
		// } else if (resultCode == RESULT_CANCELED) {
		// // User cancelled the video capture
		// } else {
		// // Video capture failed, advise user
		// }
		// }

		if (requestCode == IMAGE_SWITCHER_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String imagePath = data
						.getStringExtra(ImageSwitcherAndGalleryActivity.SELECTED_PICTURE_PATH);
				Log.v("MainActivity", imagePath);
				Bitmap bm = BitmapFactory.decodeFile(imagePath);
				background.setImageBitmap(bm);
				background.setLayoutParams(new RelativeLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				// Toast.makeText(this, imagePath, Toast.LENGTH_LONG).show();
				background.invalidate();
				w = ((BitmapDrawable) background.getDrawable()).getBitmap()
						.getWidth();
				h = ((BitmapDrawable) background.getDrawable()).getBitmap()
						.getHeight();
			}
		}
	}
}
