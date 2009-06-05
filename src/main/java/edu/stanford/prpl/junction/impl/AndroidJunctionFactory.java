package edu.stanford.prpl.junction.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.stanford.prpl.junction.api.JunctionAPI;
import edu.stanford.prpl.junction.api.JunctionFactory;

public class AndroidJunctionFactory implements JunctionFactory {
	// TODO: I should probably be a singleton per-junction session.
	// So if the JSONObject activity is the same, return the same object.
	
	// For testing, use the same JM.
	static JunctionManager mJunctionInstance;
	static AndroidJunctionFactory mFactoryInstance;
	
	
	private AndroidJunctionFactory() {
		System.setProperty("org.eclipse.jetty.util.log.class","org.mortbay.ijetty.AndroidLog");
	}
	
	public static AndroidJunctionFactory getInstance() {
		if (null == mFactoryInstance) {
			mFactoryInstance = new AndroidJunctionFactory();
		}
		
		return mFactoryInstance;
	}
	
	public JunctionManager create(Map<String,Object> activity) {
		if (mJunctionInstance == null) {
			mJunctionInstance = new JunctionManager(activity);
		}
		
		return mJunctionInstance;
	}

	public JunctionManager create(URL url) {
		if (mJunctionInstance == null) {
			Map<String,Object> desc = new HashMap<String, Object>();
			desc.put("host",url.toExternalForm());
			
			mJunctionInstance = new JunctionManager(desc);
		}
		
		return mJunctionInstance;
	}

}
