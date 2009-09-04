package edu.stanford.prpl.junction.impl;

import java.net.URI;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import edu.stanford.prpl.junction.api.activity.ActivityDescription;
import edu.stanford.prpl.junction.api.activity.JunctionActor;

public class AndroidJunctionMaker extends JunctionMaker {
	private static AndroidJunctionMaker anonInstance = null;
	
	private static String JX_LAUNCHER_NAME = "Junction AppLaunch";
	private static String JX_LAUNCHER_URL = "http://prpl.stanford.edu/android/JunctionAppLauncher.apk";
	private static String JX_LAUNCHER_PACKAGE = "edu.stanford.prpl.junction.applaunch";
	
	public static AndroidJunctionMaker getInstance() {
		if (anonInstance == null) {
			anonInstance = new AndroidJunctionMaker();
		}
		
		return anonInstance;
	}
	
	public static AndroidJunctionMaker getInstance(String switchboard) {
		// todo: singleton per-URL?
		return new AndroidJunctionMaker(switchboard);
	}
	
	protected AndroidJunctionMaker() {
		super();
	}
	
	
	protected AndroidJunctionMaker(String switchboard) {
		super(switchboard);
	}
	
	
	/**
	 * Joins a Junction Activity based on the android.app.Activity's bundle.
	 * 
	 * @param bundle
	 * @param actor
	 * @return
	 */
	public Junction newJunction(Activity activity, JunctionActor actor) {
		return newJunction(activity.getIntent().getExtras(),actor);
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
	
	public boolean isJoinable(Activity a) {
		if (a.getIntent() == null || a.getIntent().getExtras() == null) return false;
		if (a.getIntent().getExtras().containsKey("junctionVersion")) return true;
		return false;
	}
	
	/**
	 * Finds a pre-existing Junction activity by scanning for a QR code.
	 * @param context
	 */
	public void findActivityByScan(Context context) {
		Intent intent = new Intent("junction.intent.action.join.SCAN");
		intent.putExtra("package", context.getPackageName());
		IntentLauncher.launch(context, 
							intent,
							"edu.stanford.prpl.junction.applaunch",
							"http://prpl.stanford.edu/android/JunctionAppLauncher.apk",
							"Junction AppLaunch");
	}
	
	/**
	 * Invites another actor by some unspecified means.
	 * @param context
	 * @param Junction
	 * @param role
	 */
	public void inviteActor(Context context, edu.stanford.prpl.junction.api.activity.Junction junction, String role) {
		Intent intent = new Intent("junction.intent.action.invite.ANY");
		intent.putExtra("package", context.getPackageName());
		intent.putExtra("uri", junction.getInvitationURI(role).toString());
		//intent.putExtra("activityDescriptor", junction.getActivityDescription().getJSON());
		
		IntentLauncher.launch(context, 
							intent,
							JX_LAUNCHER_PACKAGE,
							JX_LAUNCHER_URL,
							JX_LAUNCHER_NAME);
	}
	
	/**
	 * Invites an actor to an activity by presenting a QR code on screen. 
	 * @param context
	 * @param junction
	 * @param role
	 */
	public void inviteActorByQR(Context context, edu.stanford.prpl.junction.api.activity.Junction junction, String role) {
		Intent intent = new Intent("junction.intent.action.invite.QR");
		intent.putExtra("package", context.getPackageName());
		intent.putExtra("uri", junction.getInvitationURI(role).toString());
		//intent.putExtra("activityDescriptor", junction.getActivityDescription().getJSON());
		
		IntentLauncher.launch(context, 
							intent,
							JX_LAUNCHER_PACKAGE,
							JX_LAUNCHER_URL,
							JX_LAUNCHER_NAME);
	}
	
	/**
	 * Scan for a Listening service and send it a 'join activity' request.
	 * @param context
	 * @param junction
	 * @param role
	 */
	public void inviteActorByScan(Context context, edu.stanford.prpl.junction.api.activity.Junction junction, String role) {
		Intent intent = new Intent("junction.intent.action.invite.SCAN");
		intent.putExtra("package", context.getPackageName());
		intent.putExtra("uri",junction.getInvitationURI(role).toString());
		//intent.putExtra("activityDescription", junction.getActivityDescription().getJSON().toString());
		//intent.putExtra("role",role);
		
		IntentLauncher.launch(context, 
							intent,
							JX_LAUNCHER_PACKAGE,
							JX_LAUNCHER_URL,
							JX_LAUNCHER_NAME);
	}
	
	/**
	 * Send an invitation to join an activity by text message.
	 * @param context
	 * @param junction
	 * @param role
	 */
	public void inviteActorBySMS(Context context, edu.stanford.prpl.junction.api.activity.Junction junction, String role) {
		Intent intent = new Intent("junction.intent.action.invite.TEXT");
        String uri = junction.getInvitationURI(role).toString();
        intent.putExtra("invitation", uri);
        
        IntentLauncher.launch(context, 
				intent,
				JX_LAUNCHER_PACKAGE,
				JX_LAUNCHER_URL,
				JX_LAUNCHER_NAME);
	}
	
	/**
	 * Send an invitation to join an activity by text message.
	 * @param context
	 * @param junction
	 * @param role
	 * @param phoneNumber
	 */
	public void inviteActorBySMS(Context context, edu.stanford.prpl.junction.api.activity.Junction junction, String role, String phoneNumber) {
		Intent intent = new Intent("junction.intent.action.invite.TEXT");
        String uri = junction.getInvitationURI(role).toString();
        intent.putExtra("invitation", uri);
        intent.putExtra("phoneNumber",phoneNumber);
        
        IntentLauncher.launch(context, 
				intent,
				JX_LAUNCHER_PACKAGE,
				JX_LAUNCHER_URL,
				JX_LAUNCHER_NAME);
	}
	
	
	/**
	 * Creates a new Junction or joins an existing one.
	 * If a new junction is created, the given role is instantiated.
	 */
	/*
	public void findActivityByScan(Context context, ActivityDescription desc, String role) {
		Intent intent = new Intent("junction.intent.action.join.SCAN");
		intent.putExtra("package", context.getPackageName());
		intent.putExtra("role",role);
		intent.putExtra("activityDescription", desc.getJSON().toString());
		IntentLauncher.launch(context, 
							intent,
							"edu.stanford.prpl.junction.applaunch",
							"http://prpl.stanford.edu/android/JunctionAppLauncher.apk",
							"Junction AppLaunch");
	}
	*/
}
