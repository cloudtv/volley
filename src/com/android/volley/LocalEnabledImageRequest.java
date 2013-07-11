package com.android.volley;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import cloudtv.util.Util;
import cloudtv.volley.Volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;

/**
 * Change the parsing of performRequest
 * 
 * @author AJit
 * 
 */
public class LocalEnabledImageRequest extends ImageRequest
{

	public LocalEnabledImageRequest(String url, Listener<Bitmap> listener, int maxWidth, int maxHeight,
			Config decodeConfig, ErrorListener errorListener) {
		super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
	}

	@Override
	protected Response<Bitmap> doParse(NetworkResponse response) {
		if(Volley.isLocalCall(getUrl())) {
			Bitmap bitmap = Util.decodeStorageImage(getUrl(), mMaxHeight, mMaxWidth, mDecodeConfig);
			return Response.success(bitmap, HttpHeaderParser.parseCacheHeaders(response));
		}
		return super.doParse(response);
	}

}
