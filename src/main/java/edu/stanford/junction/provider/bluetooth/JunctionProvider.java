/*
 * Copyright (C) 2010 Stanford University
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


package edu.stanford.junction.provider.bluetooth;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import edu.stanford.junction.Junction;
import edu.stanford.junction.JunctionException;
import edu.stanford.junction.api.activity.ActivityScript;
import edu.stanford.junction.api.activity.JunctionActor;
import edu.stanford.junction.api.messaging.MessageHeader;

public class JunctionProvider extends edu.stanford.junction.provider.JunctionProvider {
	
	public JunctionProvider(BluetoothSwitchboardConfig config) {
		
	}
	
	@Override
	public ActivityScript getActivityScript(URI uri) throws JunctionException {
		JunctionActor actor = new JunctionActor("scriptpuller") {
			
			@Override
			public void onMessageReceived(MessageHeader header, JSONObject message) {
				
			}
		};

		Junction jx = new edu.stanford.junction.provider.bluetooth.Junction(uri,null,actor);
		
		// TODO: this is clearly terrible.
		ActivityScript script = null;
		final int MAX_TIME = 10000; // ms
		final int WAIT = 300; // ms
		int total = 0;
		
		// Set in Junction bluetooth connection thread.
		// pretty gross.
		while (script == null && total < MAX_TIME) {
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {}
			
			script = jx.getActivityScript();
			total += WAIT;
		}
		 
		Log.d("junction","got activity script " + script);
		actor.leave();
		return script;
	}

	@Override
	public Junction newJunction(URI uri, ActivityScript script, JunctionActor actor) throws JunctionException {
		return new edu.stanford.junction.provider.bluetooth.Junction(uri,script,actor);
	}

	@Override
	public URI generateSessionUri() {
		try {
			//String uuid = UUID.randomUUID().toString();
			String uuid = BluetoothSwitchboardConfig.APP_UUID.toString();
			String mac = BluetoothAdapter.getDefaultAdapter().getAddress();
			return new URI("junction://" + mac + "/" + uuid + "#bt");
		} catch (URISyntaxException e) {
			throw new AssertionError("Invalid URI");
		}
	}
}
