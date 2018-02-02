package com.tms.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.utils.L;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Decodes images to {@link android.graphics.Bitmap}, scales them to needed size
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 *
 * @see com.nostra13.universalimageloader.core.assist.ImageScaleType
 * @see com.nostra13.universalimageloader.core.assist.ViewScaleType
 * @see com.nostra13.universalimageloader.core.download.ImageDownloader
 * @see com.nostra13.universalimageloader.core.DisplayImageOptions
 */
public class DecorderImage {

	private static final String LOG_IMAGE_SUBSAMPLED = "Original image (%1$dx%2$d) is going to be subsampled to %3$dx%4$d view. Computed scale size - %5$d";
	private static final String LOG_IMAGE_SCALED = "Subsampled image (%1$dx%2$d) was scaled to %3$dx%4$d";
	private Matrix m;
	// private final Uri imageUri;
	private int orientation;
	private ImageLoaderAsync mImageLoaderAsync;
	private static DecorderImage instance;
	private boolean loggingEnabled;


	public static DecorderImage getInstance(Context context) {
		if (instance == null) {
			instance = new DecorderImage(context);
		}
		return instance;
	}

	private DecorderImage(Context context) {
		mImageLoaderAsync = ImageLoaderAsync.getInstance(context);
	}

	private DecorderImage() {

	}

	private int defineExifOrientation(String imageUri) {
		int rotation = 0;

		try {
			ExifInterface exif = new ExifInterface(imageUri);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (exifOrientation) {

			case ExifInterface.ORIENTATION_NORMAL:
				rotation = 0;

				break;

			case ExifInterface.ORIENTATION_ROTATE_90:
				rotation = 90;

				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				rotation = 180;

				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				rotation = 270;

				break;
			}
		} catch (IOException e) {
			L.w("Can't read EXIF tags from file [%s]", imageUri);
		}

		return rotation;
	}

	/**
	 * This method is used to decode image
	 *
	 * @param imageUri
	 *            set uri of sdcard
	 * @param width
	 *            set width of image
	 * @param height
	 *            set height of image
	 * @return bitmap
	 * @throws java.io.IOException
	 */
	public Bitmap decode(Uri imageUri, int width, int height)
			throws IOException {
		orientation = defineExifOrientation(imageUri.getPath());
		ImageSize targetSize = new ImageSize(width, height, orientation);
		return decode(imageUri, targetSize, ImageScaleType.EXACTLY,
				ViewScaleType.FIT_INSIDE);
	}

	public Bitmap roatedBitmap(Uri imageUri, int widht, int height,
			int orientation) throws IOException {
		ImageSize targetSize = new ImageSize(widht, height, orientation);
		return decode(imageUri, targetSize, ImageScaleType.EXACTLY,
				ViewScaleType.FIT_INSIDE);
	}

	/**
	 * This method is used to decode image
	 *
	 * @param imageUri
	 *            set uri of gallery like (content://)
	 * @param width
	 *            set width of image
	 * @param height
	 *            set height of image
	 * @param context
	 *            context to pass
	 * @return bitmap
	 * @throws java.io.IOException
	 */
	public Bitmap decodeGalleryUriContent(Uri imageUri, int width, int height,
			Context context) throws IOException {
		orientation = defineExifOrientation(imageUri.getPath());
		Bitmap bitmap = Media.getBitmap(context.getContentResolver(), imageUri);
		ImageSize targetSize = new ImageSize(width, height, orientation);
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		// Cursor cursor = context.getContentResolver().query(imageUri,
		// filePathColumn, null, null, null);
		// cursor.moveToFirst();
		// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		// String picturePath = cursor.getString(columnIndex);
		return scaleImageExactly(bitmap, targetSize, ImageScaleType.EXACTLY,
				ViewScaleType.FIT_INSIDE);
	}

	/**
	 * This method is used decodeImage according to aspect ratio
	 *
	 * @param bitmap
	 *            set bitmap
	 * @param imageView
	 *            set imageview
	 * @return bitmap
	 */
	public void decodeAspectRatio(Bitmap bitmap, ImageView imageView) {
		SetAspectRatio(bitmap, imageView);
	}

	/**
	 * This method is used to decode bitmap
	 *
	 * @param bitmap
	 *            set bitmap
	 * @param width
	 *            set width
	 * @param height
	 *            set height
	 * @return bitmap
	 */
	public Bitmap decodeAspectRatio(Bitmap bitmap, int width, int height) {
		ImageSize targetSize = new ImageSize(width, height);
		return scaleImageExactly(bitmap, targetSize, ImageScaleType.EXACTLY,
				ViewScaleType.FIT_INSIDE);
	}

	@SuppressWarnings("unused")
	private ImageSize getImageSizeScaleTo(ImageView imageView, int width,
			int height) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();

		LayoutParams params = imageView.getLayoutParams();
		int width1 = params.width; // Get layout width parameter
		if (width1 <= 0)
			width1 = getFieldValue(imageView, "mMaxWidth"); // Check maxWidth
															// parameter
		if (width1 <= 0)
			width1 = width;
		if (width1 <= 0)
			width1 = displayMetrics.widthPixels;

		int height1 = params.height; // Get layout height parameter
		if (height1 <= 0)
			height1 = getFieldValue(imageView, "mMaxHeight"); // Check maxHeight
																// parameter
		if (height1 <= 0)
			height1 = height;
		if (height1 <= 0)
			height1 = displayMetrics.heightPixels;

		return new ImageSize(width1, height1);
	}

	// private ImageSize SetAspectRatio(Bitmap img, ImageView myView) {
	//
	// int height, width, w;
	// int layoutHeight = myView.getHeight();//
	// myView.getDrawable().getIntrinsicHeight();
	// int layoutWidth = myView.getWidth();// getDrawable().getIntrinsicWidth();
	//
	// if (layoutHeight < 1) {
	// layoutHeight = myView.getDrawable().getIntrinsicHeight();
	// }
	// if (layoutWidth < 1) {
	// layoutWidth = myView.getDrawable().getIntrinsicWidth();
	// }
	//
	// if (img != null) {
	// height = img.getHeight() > layoutHeight ? layoutHeight : img
	// .getHeight();
	// int ratio = ((img.getHeight() - height) * 100 / img.getHeight());
	// int wi = (img.getWidth() * ratio) / 100;
	// width = img.getWidth() - wi;
	// w = width;
	// int width1 = width > layoutWidth ? layoutWidth : width;
	// if (width > 0) {
	// ratio = ((width - width1) * 100 / width);
	// int hi = (height * ratio) / 100;
	// height = height - hi;
	// w = width1;
	// }
	// return new ImageSize(w, height);
	// } else {
	// return new ImageSize(layoutWidth, layoutHeight);
	// }
	//
	// }
	private void SetAspectRatio(Bitmap img, ImageView myView) {
		try {
			int height, width, w;
			int layoutHeight = myView.getDrawable().getIntrinsicHeight();
			int layoutWidth = myView.getDrawable().getIntrinsicWidth();
			if (img != null) {
				height = img.getHeight() > layoutHeight ? layoutHeight : img
						.getHeight();
				int ratio = ((img.getHeight() - height) * 100 / img.getHeight());
				int wi = (img.getWidth() * ratio) / 100;
				width = img.getWidth() - wi;
				w = width;
				int width1 = width > layoutWidth ? layoutWidth : width;
				if (width > 0) {
					ratio = ((width - width1) * 100 / width);
					int hi = (height * ratio) / 100;
					height = height - hi;
					w = width1;
				}

				LayoutParams params = myView.getLayoutParams();
				params.height = height;
				params.width = w;
				myView.setLayoutParams(params);

				myView.setImageBitmap(img);
				myView.setMaxHeight(img.getHeight());
				img = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			L.e(e);
		}
		return value;
	}

	/**
	 * Decodes image from URI into {@link android.graphics.Bitmap}. Image is scaled close to
	 * incoming {@link com.nostra13.universalimageloader.core.assist.ImageSize image size} during decoding (depend on incoming
	 * image scale type).
	 *
	 * @param targetSize
	 *            Image size to scale to during decoding
	 * @param scaleType
	 *            {@link com.nostra13.universalimageloader.core.assist.ImageScaleType Image scale type}
	 *
	 * @return Decoded bitmap
	 * @throws java.io.IOException
	 */
	public Bitmap decode(Uri imageUri, ImageSize targetSize,
			ImageScaleType scaleType) throws IOException {
		return decode(imageUri, targetSize, scaleType, ViewScaleType.FIT_INSIDE);
	}

	/**
	 * Decodes image from URI into {@link android.graphics.Bitmap}. Image is scaled close to
	 * incoming {@link com.nostra13.universalimageloader.core.assist.ImageSize image size} during decoding (depend on incoming
	 * image scale type).
	 *
	 * @param targetSize
	 *            Image size to scale to during decoding
	 * @param scaleType
	 *            {@link com.nostra13.universalimageloader.core.assist.ImageScaleType Image scale type}
	 * @param viewScaleType
	 *            {@link com.nostra13.universalimageloader.core.assist.ViewScaleType View scale type}
	 *
	 * @return Decoded bitmap
	 * @throws java.io.IOException
	 */
	public Bitmap decode(Uri imageUri, ImageSize targetSize,
			ImageScaleType scaleType, ViewScaleType viewScaleType)
			throws IOException {

		Options decodeOptions = getBitmapOptionsForImageDecoding(imageUri,
				targetSize, scaleType, viewScaleType);
		InputStream imageStream = new FileInputStream(imageUri.getPath());
		Bitmap subsampledBitmap;
		try {
			subsampledBitmap = BitmapFactory.decodeStream(imageStream, null,
					decodeOptions);
		} finally {
			imageStream.close();
		}
		if (subsampledBitmap == null) {
			return null;
		}

		// Scale to exact size if need
		if (scaleType == ImageScaleType.EXACTLY
				|| scaleType == ImageScaleType.EXACTLY_STRETCHED) {
			subsampledBitmap = scaleImageExactly(subsampledBitmap, targetSize,
					scaleType, viewScaleType);
		}

		return subsampledBitmap;
	}

	private Options getBitmapOptionsForImageDecoding(Uri imageUri,
			ImageSize targetSize, ImageScaleType scaleType,
			ViewScaleType viewScaleType) throws IOException {
		Options decodeOptions = new Options();
		decodeOptions.inSampleSize = computeImageScale(imageUri, targetSize,
				scaleType, viewScaleType);
		decodeOptions.inPreferredConfig = mImageLoaderAsync.getOption()
				.getDecodingOptions().inPreferredConfig;
		return decodeOptions;
	}

	private int computeImageScale(Uri imageUri, ImageSize targetSize,
			ImageScaleType scaleType, ViewScaleType viewScaleType)
			throws IOException {
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();
		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = new FileInputStream(imageUri.getPath());
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
		} finally {
			imageStream.close();
		}

		int scale = 1;
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		int widthScale = imageWidth / targetWidth;
		int heightScale = imageHeight / targetHeight;

		if (viewScaleType == ViewScaleType.FIT_INSIDE) {
			if (scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2
					|| scaleType == ImageScaleType.IN_SAMPLE_INT) {
				while (imageWidth / 2 >= targetWidth
						|| imageHeight / 2 >= targetHeight) { // ||
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
			} else {
				scale = Math.max(widthScale, heightScale); // max
			}
		} else { // ViewScaleType.CROP
			if (scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2
					|| scaleType == ImageScaleType.IN_SAMPLE_INT) {
				while (imageWidth / 2 >= targetWidth
						&& imageHeight / 2 >= targetHeight) { // &&
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
			} else {
				scale = Math.min(widthScale, heightScale); // min
			}
		}

		if (scale < 1) {
			scale = 1;
		}

		if (loggingEnabled)
			L.d(LOG_IMAGE_SUBSAMPLED, imageWidth, imageHeight, targetWidth,
					targetHeight, scale);
		return scale;
	}

	private Bitmap scaleImageExactly(Bitmap subsampledBitmap,
			ImageSize targetSize, ImageScaleType scaleType,
			ViewScaleType viewScaleType) {
		float srcWidth = subsampledBitmap.getWidth();
		float srcHeight = subsampledBitmap.getHeight();

		float widthScale = srcWidth / targetSize.getWidth();
		float heightScale = srcHeight / targetSize.getHeight();

		int destWidth;
		int destHeight;
		if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale)
				|| (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
			destWidth = targetSize.getWidth();
			destHeight = (int) (srcHeight / widthScale);
		} else {
			destWidth = (int) (srcWidth / heightScale);
			destHeight = targetSize.getHeight();
		}

		Bitmap scaledBitmap;
		if ((scaleType == ImageScaleType.EXACTLY && destWidth < srcWidth && destHeight < srcHeight)
				|| (scaleType == ImageScaleType.EXACTLY_STRETCHED
						&& destWidth != srcWidth && destHeight != srcHeight)) {
			if (orientation != 0) {
				m = new Matrix();
				m.postRotate(orientation);
				scaledBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0,
						subsampledBitmap.getWidth(),
						subsampledBitmap.getHeight(), m, true);
			} else {
				scaledBitmap = Bitmap.createScaledBitmap(subsampledBitmap,
						destWidth, destHeight, true);
			}

			if (loggingEnabled)
				L.d(LOG_IMAGE_SCALED, (int) srcWidth, (int) srcHeight,
						destWidth, destHeight);
		} else {
			scaledBitmap = subsampledBitmap;

		}
		if (scaledBitmap != subsampledBitmap) {
			subsampledBitmap.recycle();
		}
		return scaledBitmap;
	}

	void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

}