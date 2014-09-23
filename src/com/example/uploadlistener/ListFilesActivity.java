package com.example.uploadlistener;

import java.io.File;
import java.util.ArrayList;

import com.example.uploadlistener.tasks.FTPDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFilesActivity extends Activity {

	static ArrayList<String> files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_files);

		ListView listFiles = (ListView) findViewById(R.id.listFiles);

		Bundle extras = this.getIntent().getExtras();
		File folder = new File(getCacheDir() + File.separator
				+ FTPDownloader.EXTRACTED_FILES_PATH + File.separator
				+ extras.getString("folderName"));
		files = new ArrayList<String>();
		listf(folder, files, "");
		if (folder.exists())
			Log.i("LISTFILESACTIVITY", files.toString());
		else
			Log.i("LISTFILESACTIVITY", "folder is not found");

		listFiles.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, files));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_files, menu);
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

	public void listf(File directory, ArrayList<String> files, String depth) {

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			files.add(depth + file.getName());
			if (file.isDirectory()) {
				listf(file, files, "\t" + depth);
			}
		}
	}
}
