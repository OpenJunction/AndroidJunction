package edu.stanford.prpl.junction.impl;

import java.net.URL;

import android.os.Bundle;

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
		
		
		
		return null;
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
}
