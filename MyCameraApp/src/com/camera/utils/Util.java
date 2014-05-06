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
	 * ����ϵͳ��ǰʱ���Զ�����ͼƬ·����
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
	 * ������λͼ�ϲ�
	 * 
	 * @param bg
	 *            ����λͼ
	 * @param fg
	 *            ǰ��λͼ
	 * @return �ϲ����λͼ
	 */
	public static Bitmap toConformBitmap(Bitmap bg, Bitmap fg) {
		if (bg == null) {
			return null;
		}
		int bgWidth = bg.getWidth();
		int bgHeight = bg.getHeight();
		int fgWidth = fg.getWidth();
		int fgHeight = fg.getHeight();
		// create the new blank bitmap ����һ���µĺ�SRC���ȿ��һ����λͼ
		Bitmap newbmp = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		Paint paint = new Paint();
		paint.setAlpha(125);
		// draw bg into
		cv.drawBitmap(bg, 0, 0, null);// �� 0��0���꿪ʼ����bg
		// draw fg into
		cv.drawBitmap(fg, 0, 0, paint);// �� 0��0���꿪ʼ����fg�����Դ�����λ�û���
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// ����
		// store
		cv.restore();// �洢
		return newbmp;
	}
}
