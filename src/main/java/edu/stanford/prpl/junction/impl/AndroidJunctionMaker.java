package edu.stanford.prpl.junction.impl;

import java.net.URL;

public class AndroidJunctionMaker extends JunctionMaker {
	
	public static AndroidJunctionMaker getInstance() {
		return new AndroidJunctionMaker();
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
}
