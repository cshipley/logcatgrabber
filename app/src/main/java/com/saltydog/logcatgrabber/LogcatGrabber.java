package com.saltydog.logcatgrabber;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by curtis on 8/26/14.
 */
public class LogcatGrabber
{
	// Singleton instance
	private static LogcatGrabber _logcatGrabber;

	/**
	 * Initialize the log cat grabber. Best done from application object.
	 * @param context
	 * @return
	 */
	public static LogcatGrabber initialize(Context context) {
		if ( _logcatGrabber != null)
			return _logcatGrabber;

		_logcatGrabber = new LogcatGrabber();
		_logcatGrabber.context = context;

		return _logcatGrabber;
	}

	/**
	 * Get the instance
	 * @return
	 */
	public static LogcatGrabber instance() {
		return _logcatGrabber;
	}


	private boolean enabled = false;
	private Thread thread = null;
	private long maxLogfileSize = 200000;

	private Context context;


	private File priorLogfile;
	private File thisLogfile;
	private BufferedOutputStream logOutput;

	/**
	 * Gets the Uris for the two log files if they exist
	 * @return
	 */
	public ArrayList<Uri> getLogfileUris() {

		ArrayList<Uri> list = new ArrayList<Uri>();

		if ( thisLogfile != null && thisLogfile.exists())
			list.add(Uri.fromFile(thisLogfile));

		if ( priorLogfile != null && priorLogfile.exists())
			list.add(Uri.fromFile(priorLogfile));

		return list;
	}

	/**
	 * Stops log file capture
	 */
	public void stopCaptureLogFile()
	{
		enabled = false;
		thread.interrupt();
	}

	/**
	 * (Re)starts the log file capture.
	 */
	public void startCaptureLogFile() {

		if ( thread != null)
			return;

		setup();

		enabled = true;
		thread = new Thread( new Runnable() {
			@Override
			public void run() {
				try {
					captureLogFile();
				} catch ( Exception e) {
					if ( e instanceof InterruptedException)
						Log.d("529", "Capture thread stopped");
					else
						e.printStackTrace();
				}
				cleanup();
				thread = null;
			}
		});

		thread.start();
	}

	/**
	 * The "work" loop for the log file capture.
	 */
	private void captureLogFile() {
		try {

			rollFilesIfNeeded();

			Process process = Runtime.getRuntime().exec("logcat ");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line = "";
			while ( enabled && (line = bufferedReader.readLine()) != null) {
				logOutput.write(line.getBytes());
				logOutput.write("\n".getBytes());
				logOutput.flush();

				rollFilesIfNeeded();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Internal setup for the log cat grabber
	 */
	private void setup() {

		if ( thisLogfile == null)
			thisLogfile = new File(context.getExternalFilesDir(null), "logcat529.txt");

		if ( priorLogfile == null)
			priorLogfile = new File(context.getExternalFilesDir(null), "logcat529_1.txt");

		try {

			if ( thisLogfile.exists() == false)
				thisLogfile.createNewFile();

			if ( logOutput != null) {
				logOutput.close();
				logOutput = null;
			}
			logOutput = new BufferedOutputStream(new FileOutputStream(thisLogfile));
		} catch ( FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch ( IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Cleanup
	 */
	private void cleanup() {

		try {

			if ( logOutput != null) {
				logOutput.close();
				logOutput = null;
			}
		} catch ( IOException ioe) {
			ioe.printStackTrace();
		}

		thisLogfile = null;
		priorLogfile = null;
	}


	/**
	 * Renames the current one to the previous name. Removes the previous if neexxary.
	 */
	private void rollFilesIfNeeded() {

		// see if we need to save current to old
		if ( thisLogfile.length() > maxLogfileSize ) {

			if ( priorLogfile.exists())
				priorLogfile.delete();

			if ( thisLogfile.exists())
				thisLogfile.renameTo(priorLogfile.getAbsoluteFile());

			priorLogfile = thisLogfile;

			thisLogfile  = new File(context.getExternalFilesDir(null), "logcat529.txt");

			try {

				if ( thisLogfile.exists() == false)
					thisLogfile.createNewFile();

				if ( logOutput != null) {
					logOutput.close();
					logOutput = null;
				}
				logOutput = new BufferedOutputStream(new FileOutputStream(thisLogfile));

			} catch ( FileNotFoundException fnf) {
				fnf.printStackTrace();
			} catch ( IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}

