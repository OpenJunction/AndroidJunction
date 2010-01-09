package edu.stanford.prpl.junction.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.util.Log;

public class WaitForInternet {
	
	/**
	 * Check for internet connectivity.
	 * The calling context must have permission to
	 * access the device's network state.
	 * 
	 * If the calling context does not have permission, an exception is thrown.
	 * 
	 * @param WaitForInternetCallback
	 * @return
	 */
	public static void setCallback(final WaitForInternetCallback callback) {
		final ConnectivityManager connMan = (ConnectivityManager) callback.mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final MutableBoolean isConnected = new MutableBoolean(connMan
				.getActiveNetworkInfo() != null
				&& connMan.getActiveNetworkInfo().isConnected());
		if (isConnected.value) {
			callback.onConnectionSuccess();
			return;
		}
		
		final MutableBoolean isRetrying = new MutableBoolean(true);

		/* dialog */
		final AlertDialog.Builder connDialog = new AlertDialog.Builder(callback.mActivity);
		connDialog.setTitle("Network not available");
		connDialog
				.setMessage("Your phone cannot currently access the internet.");
		connDialog.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						synchronized (isRetrying) {
							isRetrying.notify();
						}
					}
				});
		connDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						synchronized (isRetrying) {
							isRetrying.value = false;
							isRetrying.notify();
						}
					}
				});

		new Thread() {
			public void run() {
				while (!isConnected.value && isRetrying.value) {
					callback.mActivity.runOnUiThread(new Thread() {
						@Override
						public void run() {
							connDialog.show();
						}
					});

					synchronized (isRetrying) {
						try {
							isRetrying.wait();
						} catch (InterruptedException e) {
							Log.w("junction", "Error waiting for retry lock", e);
						}
					}

					isConnected.value = (connMan.getActiveNetworkInfo() != null && connMan
							.getActiveNetworkInfo().isConnected());
				}
				
				if (isConnected.value) {
					callback.onConnectionSuccess();
				} else {
					callback.onConnectionFailure();
				}
			}
		}.start();
	}
}

// am I an idiot? is there a class for this?
class MutableBoolean {
	public boolean value = true;

	public MutableBoolean(boolean v) {
		value = v;
	}
}