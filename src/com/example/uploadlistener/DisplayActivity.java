package com.example.uploadlistener;

import java.util.ArrayList;

import com.example.uploadlistener.services.CustomSocketIOService;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayActivity extends ActionBarActivity {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		listView = (ListView) findViewById(R.id.displayListView);
		ArrayList<String> list = CustomSocketIOService.list;
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				String folderName = text.getText().toString();
				
				Bundle bundle = new Bundle();
				bundle.putString("folderName", folderName);
				
				Intent listFilesActivity = new Intent(DisplayActivity.this, ListFilesActivity.class);
				listFilesActivity.putExtras(bundle);
				DisplayActivity.this.startActivity(listFilesActivity);
				
				
			}
		});
		CustomSocketIOService.numMessages = 1;

	}

	@Override
	protected void onResume() {
		super.onResume();
		CustomSocketIOService.numMessages = 1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
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
}
