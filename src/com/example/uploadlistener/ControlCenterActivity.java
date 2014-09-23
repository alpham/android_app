package com.example.uploadlistener;

import com.example.uploadlistener.services.CustomSocketIOService;

import android.support.v7.app.ActionBarActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class ControlCenterActivity extends ActionBarActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_center);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control_center, menu);
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

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.stopService:
			stopService();
			break;
		case R.id.startService:
			startService();
		    break;
		}
	}

	private void startService() {
		if(!CustomSocketIOService.isAlive){
			startService(new Intent(this,CustomSocketIOService.class));
		}
	}
	

	private void stopService() {
		if(CustomSocketIOService.isAlive){
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(CustomSocketIOService.SERVICE_ID);
			stopService(new Intent(this,CustomSocketIOService.class));
		}
		
	}
}
