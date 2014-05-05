package com.camera.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.os.Environment;

public class Util {
	@SuppressLint("SimpleDateFormat")
	public static String getSavePath() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateandTime = sdf.format(new Date());
		String fileName = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/MyCameraApp/" + File.separator + "IMG_"
				+ currentDateandTime + ".jpg";
		return fileName;
	}
}
