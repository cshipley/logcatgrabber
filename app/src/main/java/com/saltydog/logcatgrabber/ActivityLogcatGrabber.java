package com.saltydog.logcatgrabber;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class ActivityLogcatGrabber extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcatgrabber);

	    findViewById(R.id.send)
			    .setOnClickListener( new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
						sendEmail();
				    }
			    });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_logrific, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	public void sendEmail() {

		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "spurious.thought@gmail.com"});
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@example.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Example.com Support");
		intent.putExtra(Intent.EXTRA_TEXT, "Enter message here...");

		// Output statistics about the device
		Log.d("529", "Android version: "+ Build.VERSION.RELEASE);
		Log.d("529", "Make/Model "+Build.MANUFACTURER+"/"+ Build.MODEL);

		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		Log.d("529", "maxMemory:" + Long.toString(maxMemory));

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
		Log.d("529", "memoryClass:" + Integer.toString(memoryClass));

		ArrayList<Uri> uris = LogcatGrabber.instance().getLogfileUris();

		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		intent.setType("message/rfc822");
		Intent mailer = createEmailOnlyChooserIntent(intent, "UserInfo");
		startActivity(mailer);

	}

	public Intent createEmailOnlyChooserIntent(Intent source,
	                                           CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities = getPackageManager()
				.queryIntentActivities(i, 0);

		for(ResolveInfo ri : activities) {
			Intent target = new Intent(source);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}

		if(!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0),
					chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));

			return chooserIntent;
		} else {
			return Intent.createChooser(source, chooserTitle);
		}
	}
}
