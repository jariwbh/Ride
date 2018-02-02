package com.tms.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;

/***
 * This is Singleton class. Instance of this class is not created.
 * 
 * @author Amit
 * 
 */
public class ImageLoaderAsync {

	// ImageLoaderAsync
	private static ImageLoaderAsync instance;
	// DisplayImageOption
	private static DisplayImageOptions mDisplayImageOptions;
	public static String IMAGERUI_WEB = "http://"; // from Web
	public static String IMAGERUI_SDCARD = "file:///"; // from SD card
	public static String IMAGERUI_CONTACT = "content://"; // from content
															// provider
	public static String IMAGERUI_ASSETS = "assets://"; // from assets
	public static String IMAGERUI_DRAWABLE = "drawable://";// from drawables
															// (only images,
															// non-9patch)

	private ImageLoaderAsync() {

	}

	/**
	 * This method is used to make instacne of this class if not created or
	 * return already created
	 * 
	 * @param context
	 *            {@link android.content.Context}
	 * @param
	 * @return {@link com.yourtaxi.utility.ImageLoaderAsync}
	 */
	public static synchronized ImageLoaderAsync getInstance(Context context) {
		if (instance == null) {
			instance = new ImageLoaderAsync();
			File cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"Android/data/" + context.getPackageName() + "/cache");
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);
			Log.i("height", "" + metrics.heightPixels);
			Log.i("width", "" + metrics.widthPixels);
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.memoryCacheExtraOptions(metrics.heightPixels,
							metrics.widthPixels)
					// default = device screen dimensions

					.discCacheExtraOptions(metrics.widthPixels,
							metrics.heightPixels, CompressFormat.PNG, 75, null)
					.threadPoolSize(3)
					// default
					.threadPriority(Thread.NORM_PRIORITY - 1)
					// default
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default
					.denyCacheImageMultipleSizesInMemory()
					// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
					.memoryCacheSize(5 * 1024 * 1024)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.threadPoolSize(5)
					// default
					.discCacheSize(5 * 1024 * 1024)
					.discCacheFileCount(100)
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
					// .imageDownloader(new BaseImageDownloader(context)) //
					// default
					// .imageDecoder(new BaseImageDecoder()) // default
					.defaultDisplayImageOptions(
							DisplayImageOptions.createSimple()) // default
					.build();
			ImageLoader.getInstance().init(config);
			L.disableLogging();
			mDisplayImageOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisc(true)
					.delayBeforeLoading(1000)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return instance;
	}

	/**
	 * This method is used to return ImageLoader
	 *
	 * @return {@link com.nostra13.universalimageloader.core.ImageLoader}
	 */
	public ImageLoader getImageLoader() {
		return ImageLoader.getInstance();
	}

	/**
	 * This method is used to return Option
	 *
	 * @return {@link com.nostra13.universalimageloader.core.DisplayImageOptions}
	 */
	public DisplayImageOptions getOption() {
		return mDisplayImageOptions;
	}

}
