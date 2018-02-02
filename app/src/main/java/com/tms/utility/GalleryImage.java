package com.tms.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class GalleryImage {
	Context context;
public GalleryImage(Context context) {
	// TODO Auto-generated constructor stub
this.context=context;
}

public static Bitmap decodeScaledBitmapFromUrl(Activity context,
String url, int width, int height) {
Bitmap bitmap = null;
try {
InputStream in = new java.net.URL(url).openStream();
bitmap = decodeSampledBitmapFromResourceMemOpt(context, in, width,
		height);
} catch (Exception e) {
e.printStackTrace();
}
return bitmap;
}

public static Bitmap decodeSampledBitmapFromResourceMemOpt(
Activity context, InputStream inputStream, int reqWidth,
int reqHeight) {

byte[] byteArr = new byte[0];
byte[] buffer = new byte[1024];
int len;
int count = 0;

try {
while ((len = inputStream.read(buffer)) > -1) {
	if (len != 0) {
		if (count + len > byteArr.length) {
			byte[] newbuf = new byte[(count + len) * 2];
			System.arraycopy(byteArr, 0, newbuf, 0, count);
			byteArr = newbuf;
		}

		System.arraycopy(buffer, 0, byteArr, count, len);
		count += len;
	}
}

final BitmapFactory.Options options = new BitmapFactory.Options();
options.inJustDecodeBounds = true;
BitmapFactory.decodeByteArray(byteArr, 0, count, options);

options.inSampleSize = calculateInSampleSize(options, reqWidth,
		reqHeight);
options.inPurgeable = true;
options.inInputShareable = true;
options.inJustDecodeBounds = false;
options.inPreferredConfig = Bitmap.Config.ARGB_8888;

int[] pids = { android.os.Process.myPid() };
ActivityManager mAM = (ActivityManager) context
		.getSystemService(Context.ACTIVITY_SERVICE);
android.os.Debug.MemoryInfo myMemInfo = mAM
		.getProcessMemoryInfo(pids)[0];
return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

} catch (Exception e) {
e.printStackTrace();

return null;
}
}


public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
        int reqHeight) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);

    options.inSampleSize = calculateInSampleSize(options, reqWidth,
            reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    Bitmap bmp = BitmapFactory.decodeFile(path, options);
    return bmp;
    }

public static int calculateInSampleSize(BitmapFactory.Options options,
        int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        if (width > height) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        } else {
            inSampleSize = Math.round((float) width / (float) reqWidth);
         }
     }
     return inSampleSize;
    }



}
