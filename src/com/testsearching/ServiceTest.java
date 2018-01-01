package com.testsearching;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ServiceTest extends Service  implements LocationListener{
	private ClientConnection connection;

	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		mTimer = new Timer();
		mTimer.schedule(timerTask, 2000, 2 * 1000);
		connection = new ClientConnection();
	
		
		 locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		 }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_STICKY;
	}

	private Timer mTimer;

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			Log.e("Log", "Running Modified");
		
		
			//			System.out.println(connection.makeConnectionGet("http://52.74.114.172:8080/Quartz_Schedular/services/stopCron"));
		}
	};
	/*
	public void onDestroy() {
		try {
			mTimer.cancel();
			timerTask.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent("com.android.techtrainner");
		intent.putExtra("yourvalue", "torestore");
		sendBroadcast(intent);
	}
*/
	@Override
	public void onLocationChanged(Location location) {
		Log.i("akshay", "lon is : "+location.getLongitude()+"  latt is : "+location.getLatitude());	
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
