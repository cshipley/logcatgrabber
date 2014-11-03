package com.saltydog.logcatgrabber;

import android.app.Application;

/**
 * Created by saltydog on 11/3/14.
 */
public class ApplicationLogcatGrabber extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		LogcatGrabber.initialize(getApplicationContext());
		LogcatGrabber.instance().startCaptureLogFile();
	}
}
