package com.testsearching;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverCall extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Service Stops", "Ohhhhhhh");
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			   Intent pushIntent = new Intent(context, ServiceTest.class);
			   context.startService(pushIntent);
		}
		//context.startService(new Intent(context, ServiceTest.class));;
	}

}
