package com.camera.view.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.test.R;

public class GalleryActivity extends Activity {
	private Gallery gallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);

		gallery = (Gallery) findViewById(R.id.gallery);

		try {
			gallery.setAdapter(new ImageAdapter(this));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private class ImageAdapter extends BaseAdapter {
		private Context context;
		private List<Integer> imgList = new ArrayList<Integer>();
		private List<Object> imgSizes = new ArrayList<Object>();

		public ImageAdapter(Context context) throws IllegalAccessException,
				IllegalArgumentException {
			this.context = context;

			Field[] fields = R.drawable.class.getDeclaredFields();
			for (Field field : fields) {
				if (!"icon".equals(field.getName())) {
					int index = field.getInt(R.drawable.class);

					imgList.add(index);

					int[] size = new int[2];
					Bitmap bmImg = BitmapFactory.decodeResource(getResources(),
							index);
					if (bmImg != null) {
						size[0] = bmImg.getWidth();
						size[1] = bmImg.getHeight();
						imgSizes.add(size);
					}
				}
			}
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView i = new ImageView(context);

			i.setImageResource(imgList.get(position).intValue());
			i.setScaleType(ImageView.ScaleType.FIT_XY);

			int[] size = new int[2];
			if (position < imgSizes.size()) {
				size = (int[]) imgSizes.get(position);
				i.setLayoutParams(new Gallery.LayoutParams(size[0], size[1]));
				return i;
			}
			return i;
		}

	}
}
