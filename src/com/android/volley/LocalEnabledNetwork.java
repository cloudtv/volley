package com.android.volley;

import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import cloudtv.util.ExceptionLogger;

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

			NetworkResponse response = super.performRequest(request);
            // handle location changed stuff
            if(response.statusCode == HttpStatus.SC_MOVED_PERMANENTLY || response.statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                try {
                    Map<String, String> responseHeaders = new HashMap<String, String>();

                    responseHeaders = response.headers;
                    if (responseHeaders.containsKey("Location")) {
                        String location = responseHeaders.get("Location");
                        request.setUrl(location);
                        // instant retry with new location
                        return performRequest(request);
                    }
                } catch (Exception e) {
                    ExceptionLogger.log(e);
                }
            }
			return response;
		}
	}
}
