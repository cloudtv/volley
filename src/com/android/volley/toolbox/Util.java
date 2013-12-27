package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import cloudtv.util.L;

public class Util
{
	public static final String TAG = "Util";

	public static Bitmap decodeStorageImage(String url, int height, int width) {
		return decodeStorageImage(url, height, width, Config.RGB_565);
	}

	public static Bitmap decodeStorageImage(String url, int height, int width, Config decodeConfig) {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		Bitmap bitmap = null;
		if(height == 0 && height == 0 && decodeConfig != null) {
			decodeOptions.inPreferredConfig = decodeConfig;
			bitmap = BitmapFactory.decodeFile(url, decodeOptions);
		} else {
			// If we have to resize this image, first get the natural bounds.
			decodeOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(url, decodeOptions);
			int actualWidth = decodeOptions.outWidth;
			int actualHeight = decodeOptions.outHeight;

			// Then compute the dimensions we would ideally like to decode to.
			int desiredWidth = getResizedDimension(width, height, actualWidth, actualHeight);
			int desiredHeight = getResizedDimension(height, width, actualHeight, actualWidth);
			// Decode to the nearest power of two scaling factor.
			decodeOptions.inJustDecodeBounds = false;
			// TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
			// decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
			int sampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

			if(isLowMemory()) {// reduce file size in case of OOM
				sampleSize++;
				L.i(TAG, "**** Try Handled OutOfMemory for local photos ****");
			}
			
			// L.d(TAG, "[decodeStorageImage]url:" + url + " desiredWidth :" + desiredWidth + " desiredHeight:"
			// + desiredHeight + " imageSize:" + sampleSize);
			decodeOptions.inSampleSize = sampleSize;
			Bitmap tempBitmap = BitmapFactory.decodeFile(url, decodeOptions);

			// If necessary, scale down to the maximal acceptable size.
			if(tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
				tempBitmap.recycle();
			} else {
				bitmap = tempBitmap;
			}
		}

		return bitmap;
	}

	public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	public static boolean isLowMemory() {
		int totalMemeory = (int) (Runtime.getRuntime().totalMemory() / 1024);
		int maxMemeory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int freeMemeory = (int) (Runtime.getRuntime().freeMemory() / 1024);
		int targetMemory = (int) (maxMemeory * 0.6); // use till 60% of max memory
		int usedMemory = totalMemeory - freeMemeory;
		if(usedMemory > targetMemory) {
			L.d(TAG, "TargetMemory :" + targetMemory + " UsedMemory :" + usedMemory + " maxMemory:" + maxMemeory
					+ " freeMemory :" + freeMemeory + " TotalMemory :" + totalMemeory);
			return true;
		}
		return false;
	}

	public static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if(maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling ratio.
		if(maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if(maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if(resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	public static boolean isLocalCall(String url) {
		if(url.startsWith("http"))
			return false;
		return true;
	}
}
