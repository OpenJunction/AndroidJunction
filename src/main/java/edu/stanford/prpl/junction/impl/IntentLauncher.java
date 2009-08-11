/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.stanford.prpl.junction.impl;

import java.util.List;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

/**
 * <p>A utility class which helps launch a remote intent, that may or may not be available on the user's system.
 *
 * <p>Integration is essentially as easy as calling {@link #initiateScan(Activity, componentName, appName)} and waiting
 * for the result in your app.</p>
 *
 * <p>There are a few steps to using this integration. First, your {@link Activity} must implement
 * the method {@link Activity#onActivityResult(int, int, Intent)} and include a line of code like this:</p>
 *
 * <p>{@code
 * public void onActivityResult(int requestCode, int resultCode, Intent intent) {
 *   IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
 *   if (scanResult != null) {
 *     // handle scan result
 *   }
 *   // else continue with any other code you need in the method
 *   ...
 * }
 * }</p>
 *
 * <p>This is where you will handle a scan result.
 * Second, just call this in response to a user action somewhere to begin the scan process:</p>
 *
 * <p>{@code integrator.initiateScan();}</p>
 *
 * <p>You can use {@link #initiateScan(Activity, String, String, String, String)} or
 * {@link #initiateScan(Activity, int, int, int, int)} to customize the download prompt with
 * different text labels.</p>
 *
 * <p>Some code, particularly download integration, was contributed from the Anobiit application.</p>
 *
 *
 * This class has been modified to run general-purpose intents. The original work was done by
 * Sean Owen and Fred Lin, and the modifications by Ben Dodson.
 *
 *
 * @author Sean Owen
 * @author Fred Lin
 * @author Ben Dodson
 */
public final class IntentLauncher {

  public static final int REQUEST_CODE = 0x070ADE2; // get it?

  private IntentLauncher() {
  }

  
  
  public static boolean launch(Context context, Intent intent, String appName) {
	  return launch(context,
			  intent,
			  null,
			  null,
			  appName,
			  "Install %APP%?",
              "This application requires %APP%, which is not currently installed. Would you like to install it?",
              "Yes",
              "No");
  }
  
  public static boolean launch(Context context, Intent intent, String packageName, String downloadRef, String appName) {
	  return launch(context,
			  intent,
			  packageName,
			  downloadRef,
			  appName,
			  "Install %APP%?",
              "This application requires %APP%, which is not currently installed. Would you like to install it?",
              "Yes",
              "No");
  }
  
  
  public static boolean launch(Context context,
		  String action,
		  String packageName,
		  String stringAppName) {
	  

	 return launch(context,
			  action,
			  packageName,
			  packageName,
			  stringAppName,
			  "Install %APP%?",
              "This application requires %APP%, which is not currently installed. Would you like to install it?",
              "Yes",
              "No");
  }
  
  
  public static boolean launch(Context context,
							  String action,
							  String packageName,
							  String downloadTerm,
							  String stringAppName) {
	  
	  return launch(context,
			  action,
			  packageName,
			  downloadTerm,
			  stringAppName,
			  "Install %APP%?",
              "This application requires %APP%, which is not currently installed. Would you like to install it?",
              "Yes",
              "No");
  }
  

  /**
   * Launches an intent.
   *
   * @param stringTitle title of dialog prompting user to download Barcode Scanner
   * @param stringMessage text of dialog prompting user to download Barcode Scanner
   * @param stringButtonYes text of button user clicks when agreeing to download Barcode Scanner (e.g. "Yes")
   * @param stringButtonNo text of button user clicks when declining to download Barcode Scanner (e.g. "No")
   * @return the contents of the barcode that was scanned, or null if none was found
   * @throws InterruptedException if timeout expires before a scan completes
   */
  public static boolean launch(Context context,
		  						  String action,
		  						  String packageName,
		  						  String downloadTerm,
		  						  String stringAppName,
                                  String stringTitle,
                                  String stringMessage,
                                  String stringButtonYes,
                                  String stringButtonNo) {
	
	  Intent intentLaunch = new Intent(action);
	  List<ResolveInfo>resolved = context.getPackageManager().queryIntentActivities(intentLaunch, 0);
	  if (resolved.size() > 0) {
		  if (null != packageName) {
			  ActivityInfo info = null;
			  int i = 0;
			  while (info == null && i < resolved.size()) {
				  if (resolved.get(i).activityInfo.packageName.equals(packageName)) {
					  info = resolved.get(i).activityInfo;
				  }
				  i++;
			  }
			  
			  if (info == null) {
				  showDownloadDialog(context, downloadTerm, stringAppName, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
			  } else {
				  ComponentName component = new ComponentName(info.packageName,info.name);
 				  intentLaunch.setComponent(component);
				  if (context instanceof Activity) {
			    		((Activity)context).startActivityForResult(intentLaunch, REQUEST_CODE);
				  } else {
					  	context.startActivity(intentLaunch);
				  }
				  return true;
			  }
			  
		  } else {
			  if (context instanceof Activity) {
		    		((Activity)context).startActivityForResult(intentLaunch, REQUEST_CODE);
			  } else {
				  	context.startActivity(intentLaunch);
			  }
			  return true;
		  }
	  } else {
		  
		  if (null != packageName) {
			  showDownloadDialog(context, packageName, stringAppName, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
		  } else {
			  // error
		  }
	  }
	  return false;
  }

  
  /**
   * Launches an intent.
   *
   * @param stringTitle title of dialog prompting user to download Barcode Scanner
   * @param stringMessage text of dialog prompting user to download Barcode Scanner
   * @param stringButtonYes text of button user clicks when agreeing to download Barcode Scanner (e.g. "Yes")
   * @param stringButtonNo text of button user clicks when declining to download Barcode Scanner (e.g. "No")
   * @return the contents of the barcode that was scanned, or null if none was found
   * @throws InterruptedException if timeout expires before a scan completes
   */
  public static boolean launch(Context context,
		  						  Intent intentLaunch,
		  						  String packageName,
		  						  String downloadRef,
		  						  String stringAppName,
                                  String stringTitle,
                                  String stringMessage,
                                  String stringButtonYes,
                                  String stringButtonNo) {
	
    try {

			List<ResolveInfo> resolved = context.getPackageManager()
					.queryIntentActivities(intentLaunch, 0);
			if (resolved.size() > 0) {
				if (null != packageName) {
					ActivityInfo info = null;
					int i = 0;
					while (info == null && i < resolved.size()) {
						if (resolved.get(i).activityInfo.packageName
								.equals(packageName)) {
							info = resolved.get(i).activityInfo;
						}
						i++;
					}

					if (info != null) {
						ComponentName component = new ComponentName(
								info.packageName, info.name);
						intentLaunch.setComponent(component);
						if (context instanceof Activity) {
							((Activity) context).startActivityForResult(
									intentLaunch, REQUEST_CODE);
						} else {
							context.startActivity(intentLaunch);
						}

						return true;
					}
				}

			}
			throw new ActivityNotFoundException();	   	  
    } catch (ActivityNotFoundException e) {
    	ComponentName component = intentLaunch.getComponent(); 
    	if (downloadRef != null){
    		showDownloadDialog(context, downloadRef, stringAppName, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
    	} else if (component != null) {
    		showDownloadDialog(context, component.getPackageName(), stringAppName, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
    	} else {
    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    	    alertDialog.setTitle("Error launching application");
    	    alertDialog.setMessage("Could not launch the required application.");
    	    alertDialog.show();
    	}
    }
    return false;
  }
  
  
  private static void showDownloadDialog(final Context context,
		  								 final String downloadTerm,
		  								 final String stringAppName,
                                         String stringTitle,
                                         String stringMessage,
                                         String stringButtonYes,
                                         String stringButtonNo) {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
    downloadDialog.setTitle(stringTitle.replace("%APP%", stringAppName));
    downloadDialog.setMessage(stringMessage.replace("%APP%", stringAppName));
    downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
    	if (downloadTerm.contains("://")) {
    		Uri uri = Uri.parse(downloadTerm);
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        context.startActivity(intent);
    	} else {
	        Uri uri = Uri.parse("market://search?q="+downloadTerm);
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        context.startActivity(intent);
    	}
      }
    });
    downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {}
    });
    downloadDialog.show();
  }

}
