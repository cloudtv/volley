package com.android.volley;

import cloudtv.volley.Volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;

/**
 * 
 * @author AJit
 * 
 */
public class LocalEnabledNetwork extends BasicNetwork
{

	public LocalEnabledNetwork(HttpStack httpStack) {
		super(httpStack);
	}

	@Override
	public NetworkResponse performRequest(Request<?> request) throws VolleyError {
		if(Volley.isLocalCall(request.getUrl())) {
			return new NetworkResponse(null);
		} else {
			return super.performRequest(request);
		}
	}
}
