package com.camera.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.test.R;

/**
 * ImageSwitcher��Gallery���չʾSD���е���ԴͼƬ
 */
public class ImageSwitcherAndGalleryActivity extends Activity implements
		OnItemSelectedListener, ViewFactory {
	public static final String SELECTED_PICTURE_PATH = "SELECTED_PICTURE_PATH";

	private String selectedImagePath;
	private List<String> imagePathList;
	private String[] list;
	private ImageSwitcher mSwitcher;
	private Gallery mGallery;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imagePathList = getImagePathFromSD();
		list = imagePathList.toArray(new String[imagePathList.size()]);

		/* �趨Switcher */
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		/* �趨����Switcher��ģʽ */
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		/* �趨���Switcher��ģʽ */
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		mSwitcher.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(ImageSwitcherAndGalleryActivity.this,
				// "������ImageSwitch�ϵ�ͼƬ", Toast.LENGTH_SHORT).show();
				// TODO ������ͼƬ�¼�
				Intent intent = getIntent();
				intent.putExtra(SELECTED_PICTURE_PATH, selectedImagePath);
				ImageSwitcherAndGalleryActivity.this.setResult(RESULT_OK,
						intent);
				ImageSwitcherAndGalleryActivity.this.finish();
			}

		});

		mGallery = (Gallery) findViewById(R.id.mygallery);
		/* ������ImageAdapter���趨��Gallery���� */
		mGallery.setAdapter(new ImageAdapter(this, getImagePathFromSD()));

		mGallery.setOnItemSelectedListener(this);

		/* �趨һ��itemclickListener�¼� */
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// ������Gallery���¼�
				// Toast.makeText(ImageSwitcherAndGalleryActivity.this,
				// "������Gallery�ϵ�ͼƬ", Toast.LENGTH_SHORT).show();
			}
		});

	}

	/** ��SD���л�ȡ��ԴͼƬ��·�� */
	private List<String> getImagePathFromSD() {
		/* �趨Ŀǰ����·�� */
		List<String> it = new ArrayList<String>();

		// �����Լ��������ȡSDCard�е���ԴͼƬ��·��
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Pictures/MyCameraApp";

		File mFile = new File(imagePath);
		File[] files = mFile.listFiles();

		/* �������ļ�����ArrayList�� */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (checkIsImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	/** �ж��Ƿ���Ӧ��ͼƬ��ʽ */
	private boolean checkIsImageFile(String fName) {
		boolean isImageFormat;

		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* ����չ�������;���MimeType */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			isImageFormat = true;
		} else {
			isImageFormat = false;
		}
		return isImageFormat;
	}

	/* ��дBaseAdapter�Զ���һImageAdapter class */
	public class ImageAdapter extends BaseAdapter {
		/* �������� */
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		/* ImageAdapter�Ĺ���� */
		public ImageAdapter(Context c, List<String> li) {
			mContext = c;
			lis = li;
			/*
			 * ʹ��res/values/attrs.xml�е�<declare-styleable>���� ��Gallery����.
			 */
			TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);
			/* ȡ��Gallery���Ե�Index id */
			mGalleryItemBackground = mTypeArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			/* �ö����styleable�����ܹ�����ʹ�� */
			mTypeArray.recycle();
		}

		/* ��д�ķ���getCount,����ͼƬ��Ŀ */
		public int getCount() {
			return lis.size();
		}

		/* ��д�ķ���getItem,����position */
		public Object getItem(int position) {
			return position;
		}

		/* ��д�ķ���getItemId,����position */
		public long getItemId(int position) {
			return position;
		}

		/* ��д����getView,������View���� */
		public View getView(int position, View convertView, ViewGroup parent) {
			/* ����ImageView���� */
			ImageView i = new ImageView(mContext);
			/* �趨ͼƬ��imageView���� */
			Bitmap bm = BitmapFactory.decodeFile(lis.get(position).toString());
			i.setImageBitmap(bm);
			/* �����趨ͼƬ�Ŀ�� */
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			/* �����趨Layout�Ŀ�� */
			i.setLayoutParams(new Gallery.LayoutParams(136, 88));
			/* �趨Gallery����ͼ */
			i.setBackgroundResource(mGalleryItemBackground);
			/* ����imageView���� */
			return i;
		}
	}

	@Override
	public View makeView() {
		ImageView iv = new ImageView(this);
		iv.setBackgroundColor(0xFF000000);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iv.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return iv;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String photoURL = list[position];
		selectedImagePath = list[position];
		Log.i("A", String.valueOf(position));

		mSwitcher.setImageURI(Uri.parse(photoURL));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}