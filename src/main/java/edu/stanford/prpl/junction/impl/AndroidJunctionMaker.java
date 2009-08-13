package edu.stanford.prpl.junction.impl;

import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import edu.stanford.prpl.junction.api.activity.ActivityDescription;
import edu.stanford.prpl.junction.api.activity.JunctionActor;

public class AndroidJunctionMaker extends JunctionMaker {
	private static AndroidJunctionMaker anonInstance = null;
	
	public static AndroidJunctionMaker getInstance() {
		if (anonInstance == null) {
			anonInstance = new AndroidJunctionMaker();
		}
		
		return anonInstance;
	}
	
	public static AndroidJunctionMaker getInstance(URL url) {
		// todo: singleton per-URL?
		return new AndroidJunctionMaker(url);
	}
	
	protected AndroidJunctionMaker() {
		super();
	}
	
	protected AndroidJunctionMaker(URL url) {
		super(url);
	}
	
	/**
	 * Junction creator from a bundle passed from
	 * a Junction Activity Launcher
	 * 
	 * @param bundle
	 * @param actor
	 * @return
	 */
	public Junction newJunction(Bundle bundle, JunctionActor actor) {
		Log.d("junction","Creating junction from bundle.");
		if (bundle == null || !bundle.containsKey("junctionVersion")) {
			Log.d("junction","Could not launch from bundle (" + bundle + ")");
			return null;
		}
		
		try {
			JSONObject desc = new JSONObject(bundle.getString("activityDescriptor"));
			ActivityDescription activityDesc = new ActivityDescription(desc);
			Junction jx = new Junction(activityDesc);
			jx.registerActor(actor);
			
			return jx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void findActivityByScan(Context context) {
		Intent intent = new Intent("junction.intent.action.SCAN");
		intent.putExtra("package", context.getPackageName());
		IntentLauncher.launch(context, 
							intent,
							"edu.stanford.prpl.junction.applaunch",
							"http://prpl.stanford.edu/android/JunctionAppLauncher.apk",
							"Junction AppLaunch");
	}
	
	
	/*
	 * onCreate(Bundle bundle) {
	 * 		super.onCreate(bundle);
	 * 
	 * 	// the if (...) is only required if this Activity can be accessed directly (and not just from our intent)
	 * 		if (AndroidJunctionMaker.isActorRequest(bundle) {
	 * 			Junction jx = AndroidJunctionMaker.getInstance().newJunction(bundle, MyActor.getInstance());
	 *	 	}	
	 * }
	 */
	
	// public static void inviteActor(Context context, Junction activity, String{[]} suggestedRole(s))
	// offer different ways (QR only via alert; full support via remote activity)
	// how to return to this activity? can we just finish() the other?
	
	// TODO: (1) Add an intent to applaunch
	// 		 (2) Create AndroidJunctionMaker.joinActivity(Context c) and use that intent here.
	// 		 (3) Re-enter through the same onCreate syntax as above.
}
