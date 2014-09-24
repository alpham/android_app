package com.example.uploadlistener.services;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.uploadlistener.ControlCenterActivity;
import com.example.uploadlistener.R;
import com.example.uploadlistener.tasks.FTPDownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CustomSocketIOService extends Service implements IOCallback {

	public static final int SERVICE_ID = 1111111111;
	public static int UNIQUEID = 689431536;
	public static SocketIO socket;
	public static boolean isAlive = false;
	public static int numMessages = 1;
	private NotificationManager mNotificationManager;
	public static ArrayList<String> list;
	
	public static int id = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		list = new ArrayList<String>();
		try {
			socket = new SocketIO(/*"http://192.168.56.1:5000/"*/"http://still-reaches-1894.herokuapp.com/"); // just for testing..
			socket.connect(this);
			// introducing the doctor to the server 
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		isAlive = true;
		
		Intent i = new Intent(this, ControlCenterActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setContentTitle("Patients app service")
				.setContentText("Service is ON.").setContentIntent(pi)
				.setSmallIcon(R.drawable.doctor_small)
				.setTicker("Service is ON.").setOngoing(true);
		Notification notifi = builder.build();
		notifi.flags |= Notification.FLAG_NO_CLEAR;
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(
				CustomSocketIOService.SERVICE_ID, notifi);

		startForeground(SERVICE_ID, notifi);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isAlive = false;
		mNotificationManager.cancel(SERVICE_ID);
		socket.disconnect();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);

		Intent i = new Intent(this, ControlCenterActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setContentTitle("Patients app service")
				.setContentText("Service is ON.").setContentIntent(pi)
				.setSmallIcon(R.drawable.doctor_small)
				.setTicker("Service is ON.").setOngoing(true);
		Notification notifi = builder.build();
		notifi.flags |= Notification.FLAG_NO_CLEAR;
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(
				CustomSocketIOService.SERVICE_ID, notifi);

		startForeground(SERVICE_ID, notifi);

	}

	private void downloadNewCase(Bundle extras) {
		JSONObject json;
		try {
			json = new JSONObject(extras.getString("args"));
			String fileName = json.getString("file_name");
			list.add(fileName.substring(0, fileName.lastIndexOf(".")));
			FTPDownloader ftpD = new FTPDownloader(this,extras);
			// notification part is in the AsyncTask 
			ftpD.execute(fileName);
		} catch (JSONException e) {
			list.add("Error");
			e.printStackTrace();
		}


	}

	@Override
	public void on(String e, IOAcknowledge arg1, Object... arg2) {
		switch(e){
		case "new_case":
			newCaseRecieved(arg2);
			break;
		case "ready":
			JSONObject newConnection;
			try {
				newConnection = new JSONObject("{id: '"
						+ CustomSocketIOService.id
						+ "'}");
				Log.i("SocketIO", "not json exception");
				socket.emit("doctor_new_connection", newConnection);
			} catch (JSONException ex) {
				Log.i("SocketIO", "Error when introducing myself to the server");
				ex.printStackTrace();
			}
			break;
		}
		
	}

	private void newCaseRecieved(Object[] arg2) {
		Object[] arguments = arg2;
		String json = arguments[0].toString();
		Bundle extras = new Bundle();
		extras.putString("args", json);
		
		
		downloadNewCase(extras);

		
	}

	@Override
	public void onConnect() {
		Log.i("SocketIO", "Connected");
	}

	@Override
	public void onDisconnect() {
		Log.i("SocketIO", "Disconnected");
		if (CustomSocketIOService.isAlive) {
			socket.reconnect();
		}
	}

	@Override
	public void onError(SocketIOException arg0) {
		Log.i("SocketIO", "EROOR");
		arg0.printStackTrace();
		if (!socket.isConnected()) {
			socket.reconnect();
		}
		return;
	}

	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		Log.i("SocketIO",
				"onMessage: " + arg0 + "IOAcknowledge: " + arg1.toString());

	}

	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		Log.i("SocketIO", "onMessage: " + arg0.toString() + "IOAcknowledge: "
				+ arg1.toString());

	}

}
