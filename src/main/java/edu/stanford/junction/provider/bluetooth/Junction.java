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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import edu.stanford.junction.api.activity.ActivityScript;
import edu.stanford.junction.api.activity.JunctionActor;
import edu.stanford.junction.api.messaging.MessageHeader;

public class Junction extends edu.stanford.junction.Junction {
	private static String TAG = "junction";
	private ActivityScript mActivityScript;
	
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private URI mUri;
	private String mSession;
	private String mSwitchboard;
	
	public Junction(URI uri, ActivityScript script, final JunctionActor actor) {
		mActivityScript = script;
		mUri = uri;		
		mSession = uri.getPath().substring(1);
		mSwitchboard = uri.getAuthority();
		
		setActor(actor);
		triggerActorJoin(script == null || script.isActivityCreator());

		// TODO: if (mSwitchboard == mAdapter.getAddress()), start in hub mode.
		Log.d("junction","connecting to junction session: " + mSession);
		BluetoothDevice hub = mAdapter.getRemoteDevice(mSwitchboard);
		mConnectThread = new ConnectThread(hub, UUID.fromString(mSession));
		mConnectThread.start();
	}
	
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public URI getAcceptedInvitation() {
		return mUri;
	}

	@Override
	public ActivityScript getActivityScript() {
		return mActivityScript;
	}

	@Override
	public URI getBaseInvitationURI() {
		return mUri;
	}

	@Override
	public String getSessionID() {
		return mSession;
	}

	@Override
	public String getSwitchboard() {
		return mSwitchboard;
	}

	@Override
	public void doSendMessageToActor(String actorID, JSONObject message) {
		JSONObject jx;
		if (message.has(NS_JX)) {
			jx = message.optJSONObject(NS_JX);
		} else {
			jx = new JSONObject();
			try {
				message.put(NS_JX, jx);
			} catch (JSONException j) {}
		}
		try {
			jx.put("targetActor", actorID);
		} catch (Exception e) {}
		mConnectedThread.write(message.toString().getBytes());
	}

	@Override
	public void doSendMessageToRole(String role, JSONObject message) {
		JSONObject jx;
		if (message.has(NS_JX)) {
			jx = message.optJSONObject(NS_JX);
		} else {
			jx = new JSONObject();
			try {
				message.put(NS_JX, jx);
			} catch (JSONException j) {}
		}
		try {
			jx.put("targetRole", role);
		} catch (Exception e) {}
		mConnectedThread.write(message.toString().getBytes());
	}

	@Override
	public void doSendMessageToSession(JSONObject message) {
		Log.d(TAG,"writing to session: " + message);
		mConnectedThread.write(message.toString().getBytes());
	}

	@Override
	public JunctionActor getActor() {
		return mOwner;
	}
	
	
	
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String mmSession;
        
        public ConnectThread(BluetoothDevice device, UUID uuid) {
            mmDevice = device;
            mmSession = uuid.toString();
            
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            //mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	Log.d(TAG,"trying to connect socket for " + mmDevice.getAddress() + " at " + mmSession);
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG,"failed to connect to bluetooth socket", e);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                
                return;
            }
            Log.d(TAG,"socket connected");
            // Reset the ConnectThread because we're done
            synchronized (Junction.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // TODO: won't work with something over 1k
                    String jsonStr = new String(buffer,0,bytes);
                    JSONObject json = new JSONObject(jsonStr);
                    
                    // TODO: make header proper. Add sender, etc.
                    // Try to role this into framework?
                    String from = "me";
                    MessageHeader header = new MessageHeader(Junction.this,json,from);
                    
                    Junction.this.triggerMessageReceived(header, json);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    //connectionLost();
                    break;
                } catch (JSONException e2) {
                	Log.e(TAG, "not JSON", e2);
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                /*mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
	
}