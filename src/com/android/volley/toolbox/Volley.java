package com.android.volley.toolbox;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import cloudtv.util.Util;

import com.android.volley.LocalEnabledNetwork;
import com.android.volley.Network;
import com.android.volley.RequestQueue;

public class Volley
{
	public RequestQueue newRequestQueue(Context context, HttpStack stack, String diskPath, int diskUsageBites) {
		File cacheDir = getCacheDir(context, diskPath);

		String userAgent = "";
		try {
			String packageName = context.getPackageName();

			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch(NameNotFoundException e) {
		}

		if(stack == null) {
			if(Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}

		return getRequestQueue(stack, cacheDir, diskUsageBites);
	}

	public RequestQueue getRequestQueue(HttpStack stack, File cacheDir, int diskUsageBites) {
		Network network = new LocalEnabledNetwork(stack);

		DiskBasedCache diskBaseCache = new DiskBasedCache(cacheDir, diskUsageBites);
		RequestQueue queue = new RequestQueue(diskBaseCache, network);
		queue.start();
		return queue;
	}

	public static File getCacheDir(Context context, String path) {
		File cacheDir;
		// Find the dir to save cached images
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			// cacheDir = new File(Environment.getExternalStorageDirectory(), path);
			cacheDir = new File(Util.getExternalStorage(context), path);
		else
			cacheDir = context.getCacheDir();
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

}
