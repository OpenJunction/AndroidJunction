package edu.stanford.prpl.junction.impl;

import android.app.Activity;

public abstract class WaitForInternetCallback {
	protected Activity mActivity;
	
	public WaitForInternetCallback(Activity activity) {
		mActivity=activity;
	}
	
	public abstract void onConnectionSuccess();
	public abstract void onConnectionFailure();
}