package edu.stanford.junction.provider.bluetooth;

import java.util.UUID;

import edu.stanford.junction.SwitchboardConfig;

public class BluetoothSwitchboardConfig implements SwitchboardConfig {
	public static String APP_NAME = "junction";
	//public static UUID APP_UUID = UUID.fromString("88db3e60-fa58-11df-8cff-0800200c9a66");
	
	boolean requireSecurePairing = false;
	UUID mUuid;
	
	public BluetoothSwitchboardConfig() {
		mUuid = UUID.randomUUID();
	}
	
	public BluetoothSwitchboardConfig(boolean requireSecurePairing) {
		this.requireSecurePairing = requireSecurePairing;
		mUuid = UUID.randomUUID();
	}
	
	public UUID getUuid() {
		return mUuid;
	}
	
	protected void setUuid(UUID uuid) {
		mUuid = uuid;
	}
	
	/**
	 * Returns true if secure pairing is required for connections.
	 * Otherwise, if the operating system supports it (>= 2.3.3),
	 * an insecure connection will be used that does not require
	 * pairing.
	 * 
	 */
	public boolean securePairingRequired() {
		return requireSecurePairing;
	}
}
