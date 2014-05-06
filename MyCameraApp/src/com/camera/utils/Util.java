package com.camera.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

public class Util {
	private static final String TAG = "com.camera.utils.Util";

	/**
	 * 根据系统当前时间自动生成图片路径名
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSavePath() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateandTime = sdf.format(new Date());
		String fileName = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/MyCameraApp/" + File.separator + "IMG_"
				+ currentDateandTime + ".jpg";
		Log.v(TAG, fileName);
		return fileName;
	}

	/**
	 * 将两个位图合并
	 * 
	 * @param bg
	 *            背景位图
	 * @param fg
	 *            前景位图
	 * @return 合并后的位图
	 */
	public static Bitmap toConformBitmap(Bitmap bg, Bitmap fg) {
		if (bg == null) {
			return null;
		}
		int bgWidth = bg.getWidth();
		int bgHeight = bg.getHeight();
		int fgWidth = fg.getWidth();
		int fgHeight = fg.getHeight();
		// create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
		Bitmap newbmp = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		Paint paint = new Paint();
		paint.setAlpha(125);
		// draw bg into
		cv.drawBitmap(bg, 0, 0, null);// 在 0，0坐标开始画入bg
		// draw fg into
		cv.drawBitmap(fg, 0, 0, paint);// 在 0，0坐标开始画入fg，可以从任意位置画入
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newbmp;
	}
}
