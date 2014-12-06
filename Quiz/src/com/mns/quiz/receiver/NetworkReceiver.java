package com.mns.quiz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {

		boolean status = NetworkUtil.getConnectivityStatusString(context);

//		Toast.makeText(context, status?"online":"offline", Toast.LENGTH_LONG).show();
	}
}