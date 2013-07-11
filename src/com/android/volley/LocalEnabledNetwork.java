package com.android.volley;

import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Util;

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
		if(Util.isLocalCall(request.getUrl())) {
			return new NetworkResponse(null);
		} else {
			return super.performRequest(request);
		}
	}
}
