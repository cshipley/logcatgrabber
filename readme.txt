Logcat Grabber

Use the LogcatGrabber class to capture logcat output for your app. It will keep a current log file
and previous one. When the current exceeds a particular threshold, it will automatically roll the
log files.

ActivityLogcatGrabber.java shows how to attached the 1 or 2 logfiles to an email and send it. Prior
to doing so it will output the device model, Android version, and memory statistics.

This is a great way to add simple support to an app.

Usage:

In The application class initialize and startup the grabber:

    public class ApplicationLogcatGrabber extends Application {

        @Override
        public void onCreate() {
            super.onCreate();

            LogcatGrabber.initialize(getApplicationContext());
            LogcatGrabber.instance().startCaptureLogFile();
        }
    }

Capture can be started and stopped anytime with:

    LogcatGrabber.instance().startCaptureLogFile();

or

    LogcatGrabber.instance().stopCaptureLogFile();


