package com.example.uploadlistener;

import com.example.uploadlistener.services.CustomSocketIOService;
import com.example.uploadlistener.tasks.LoginTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	public EditText usernameEditText;
	public EditText passwordEditText;
	public Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CustomSocketIOService.id != 0) {
			startActivity(new Intent(this, DisplayActivity.class));
			finish();
		}

		setContentView(R.layout.activity_main);

		usernameEditText = (EditText) findViewById(R.id.usernameTextView);
		passwordEditText = (EditText) findViewById(R.id.passwordTextView);
		login = (Button) findViewById(R.id.loginButton);

		login.setOnClickListener(this);

		CustomSocketIOService.numMessages = 1;

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CustomSocketIOService.numMessages = 1;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		String username = this.usernameEditText.getText().toString();
		String password = this.passwordEditText.getText().toString();

		LoginTask loginTask = new LoginTask(username, password, this);
		loginTask.execute();

	}

	public void tryAgain() {
		this.usernameEditText.setText("");
		this.usernameEditText.setHint("Username");
		this.passwordEditText.setText("");
		this.passwordEditText.setHint("Password");

		Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG)
				.show();

	}

}
