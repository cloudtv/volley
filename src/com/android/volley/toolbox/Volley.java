package com.android.volley.toolbox;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;

import com.android.volley.LocalEnabledNetwork;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

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

	public File getCacheDir(Context context, String path) {
		File cacheDir;
		// Find the dir to save cached images
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(Environment.getExternalStorageDirectory(), path);
		else
			cacheDir = context.getCacheDir();
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

}