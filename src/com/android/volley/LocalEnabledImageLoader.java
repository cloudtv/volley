package com.android.volley;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * To provide local enable image provider
 * 
 * @author AJit
 * 
 */
public class LocalEnabledImageLoader extends ImageLoader
{

	public LocalEnabledImageLoader(RequestQueue queue, ImageCache imageCache) {
		super(queue, imageCache);
	}

	@Override
	public Request<?> getImageRequest(String requestUrl, final String cacheKey, int maxWidth, int maxHeight) {

		return new LocalEnabledImageRequest(requestUrl, new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				onGetImageSuccess(cacheKey, response);
			}
		}, maxWidth, maxHeight, Config.RGB_565, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onGetImageError(cacheKey, error);
			}
		});
	}

}
